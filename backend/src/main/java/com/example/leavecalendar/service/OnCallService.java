package com.example.leavecalendar.service;

import com.example.leavecalendar.dto.OnCallWeekDto;
import com.example.leavecalendar.entity.LeaveRequest;
import com.example.leavecalendar.entity.OnCallOverride;
import com.example.leavecalendar.entity.TeamMember;
import com.example.leavecalendar.repository.LeaveRequestRepository;
import com.example.leavecalendar.repository.OnCallOverrideRepository;
import com.example.leavecalendar.repository.TeamMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OnCallService {

    private final TeamMemberRepository memberRepo;
    private final LeaveRequestRepository leaveRepo;
    private final OnCallOverrideRepository overrideRepo;

    public OnCallService(TeamMemberRepository memberRepo,
                         LeaveRequestRepository leaveRepo,
                         OnCallOverrideRepository overrideRepo) {
        this.memberRepo = memberRepo;
        this.leaveRepo = leaveRepo;
        this.overrideRepo = overrideRepo;
    }

    public List<OnCallWeekDto> getSchedule(int weeksCount) {
        List<TeamMember> members = memberRepo.findAll();
        if (members.isEmpty()) return List.of();

        List<OnCallWeekDto> schedule = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 0; i < weeksCount; i++) {
            LocalDate currentWeekStart = weekStart.plusWeeks(i);
            LocalDate currentWeekEnd = currentWeekStart.plusDays(6);

            int weekNumber = currentWeekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int year = currentWeekStart.get(IsoFields.WEEK_BASED_YEAR);

            Optional<OnCallOverride> override = overrideRepo.findByYearAndWeekNumber(year, weekNumber);

            TeamMember onCallMember;
            boolean isOverride = false;

            if (override.isPresent()) {
                onCallMember = override.get().getMember();
                isOverride = true;
            } else {
                // ISO week 1 of 2026 → index 0 = Alice, 1 = Bob, etc.
                int baseWeek = getBaseWeekIndex(currentWeekStart);
                onCallMember = members.get(baseWeek % members.size());
            }

            boolean hasConflict = hasApprovedLeave(onCallMember, currentWeekStart, currentWeekEnd);

            OnCallWeekDto dto = new OnCallWeekDto();
            dto.setYear(year);
            dto.setWeekNumber(weekNumber);
            dto.setWeekStart(currentWeekStart);
            dto.setWeekEnd(currentWeekEnd);
            dto.setMemberId(onCallMember.getId());
            dto.setMemberName(onCallMember.getName());
            dto.setHasConflict(hasConflict);
            dto.setOverride(isOverride);

            schedule.add(dto);
        }

        return schedule;
    }

    public List<TeamMember> getAvailableMembers(int year, int weekNumber) {
        LocalDate weekStart = LocalDate.now()
            .with(IsoFields.WEEK_BASED_YEAR, year)
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        return memberRepo.findAll().stream()
            .filter(m -> !hasApprovedLeave(m, weekStart, weekEnd))
            .toList();
    }

    public OnCallWeekDto setOverride(int year, int weekNumber, Long memberId) {
        TeamMember member = memberRepo.findById(memberId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Team member not found"));

        OnCallOverride override = overrideRepo
            .findByYearAndWeekNumber(year, weekNumber)
            .orElse(new OnCallOverride());

        override.setYear(year);
        override.setWeekNumber(weekNumber);
        override.setMember(member);
        overrideRepo.save(override);

        return getSchedule(12).stream()
            .filter(w -> w.getYear() == year && w.getWeekNumber() == weekNumber)
            .findFirst()
            .orElseThrow();
    }

    private boolean hasApprovedLeave(TeamMember member, LocalDate weekStart, LocalDate weekEnd) {
        return leaveRepo.findOverlapping(
            member.getId(), weekStart, weekEnd, LeaveRequest.LeaveStatus.PENDING
        ).stream().anyMatch(lr -> lr.getStatus() == LeaveRequest.LeaveStatus.APPROVED);
    }
    
    private int getBaseWeekIndex(LocalDate weekStart) {
        // Week 1 of 2026 as anchor (index 0 = Alice)
        LocalDate anchor = LocalDate.of(2026, 1, 5); // Monday of week 1, 2026
        long weeksDiff = java.time.temporal.ChronoUnit.WEEKS.between(anchor, weekStart);
        return (int) ((weeksDiff % 4 + 4) % 4);
    }
}