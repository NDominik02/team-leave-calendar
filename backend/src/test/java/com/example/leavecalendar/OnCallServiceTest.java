package com.example.leavecalendar;

import com.example.leavecalendar.dto.LeaveRequestDto;
import com.example.leavecalendar.dto.OnCallWeekDto;
import com.example.leavecalendar.entity.TeamMember;
import com.example.leavecalendar.repository.LeaveRequestRepository;
import com.example.leavecalendar.repository.OnCallOverrideRepository;
import com.example.leavecalendar.repository.TeamMemberRepository;
import com.example.leavecalendar.service.LeaveRequestService;
import com.example.leavecalendar.service.OnCallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OnCallServiceTest {

    @Autowired
    private OnCallService onCallService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private OnCallOverrideRepository overrideRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @BeforeEach
    void setUp() {
        leaveRequestRepository.deleteAll();
        overrideRepository.deleteAll();
        teamMemberRepository.deleteAll();
        teamMemberRepository.save(new TeamMember("Alice"));
        teamMemberRepository.save(new TeamMember("Bob"));
        teamMemberRepository.save(new TeamMember("Charlie"));
        teamMemberRepository.save(new TeamMember("Diana"));
    }

    @Test
    void shouldReturn8WeeksByDefault() {
        List<OnCallWeekDto> schedule = onCallService.getSchedule(8);
        assertEquals(8, schedule.size());
    }

    @Test
    void shouldRotateEvery4Weeks() {
        List<OnCallWeekDto> schedule = onCallService.getSchedule(8);

        // Week 0 and week 4 should be the same person
        assertEquals(
            schedule.get(0).getMemberName(),
            schedule.get(4).getMemberName()
        );

        // Week 1 and week 5 should be the same person
        assertEquals(
            schedule.get(1).getMemberName(),
            schedule.get(5).getMemberName()
        );
    }

    @Test
    void shouldDetectConflictWhenOnCallPersonHasApprovedLeave() {
        List<OnCallWeekDto> schedule = onCallService.getSchedule(8);
        OnCallWeekDto firstWeek = schedule.get(0);

        // Find the on-call member by ID (not name)
        TeamMember onCallMember = teamMemberRepository.findById(firstWeek.getMemberId())
            .orElseThrow();

        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setMemberId(onCallMember.getId());
        dto.setStartDate(firstWeek.getWeekStart());
        dto.setEndDate(firstWeek.getWeekEnd());
        var created = leaveRequestService.create(dto);
        leaveRequestService.updateStatus(created.getId(),
            com.example.leavecalendar.entity.LeaveRequest.LeaveStatus.APPROVED);

        List<OnCallWeekDto> updated = onCallService.getSchedule(8);
        assertTrue(updated.get(0).isHasConflict());
    }

    @Test
    void shouldNotDetectConflictForPendingLeave() {
        List<OnCallWeekDto> schedule = onCallService.getSchedule(8);
        OnCallWeekDto firstWeek = schedule.get(0);

        TeamMember onCallMember = teamMemberRepository.findAll().stream()
            .filter(m -> m.getName().equals(firstWeek.getMemberName()))
            .findFirst()
            .orElseThrow();

        // Create leave but leave it PENDING
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setMemberId(onCallMember.getId());
        dto.setStartDate(firstWeek.getWeekStart());
        dto.setEndDate(firstWeek.getWeekEnd());
        leaveRequestService.create(dto);

        List<OnCallWeekDto> updated = onCallService.getSchedule(8);
        assertFalse(updated.get(0).isHasConflict());
    }

    @Test
    void shouldApplyOverride() {
        List<OnCallWeekDto> schedule = onCallService.getSchedule(8);
        OnCallWeekDto firstWeek = schedule.get(0);

        // Find a different member than the current on-call
        TeamMember replacement = teamMemberRepository.findAll().stream()
            .filter(m -> !m.getName().equals(firstWeek.getMemberName()))
            .findFirst()
            .orElseThrow();

        OnCallWeekDto updated = onCallService.setOverride(
            firstWeek.getYear(), firstWeek.getWeekNumber(), replacement.getId());

        assertEquals(replacement.getName(), updated.getMemberName());
        assertTrue(updated.isOverride());
    }

    @Test
    void shouldReturnAvailableMembersForWeek() {
        List<OnCallWeekDto> schedule = onCallService.getSchedule(8);
        OnCallWeekDto firstWeek = schedule.get(0);

        // All 4 members should be available initially
        List<TeamMember> available = onCallService.getAvailableMembers(
            firstWeek.getYear(), firstWeek.getWeekNumber());
        assertEquals(4, available.size());
    }
}