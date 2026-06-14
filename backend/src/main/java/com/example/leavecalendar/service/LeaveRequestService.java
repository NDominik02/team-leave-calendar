package com.example.leavecalendar.service;

import com.example.leavecalendar.dto.LeaveRequestDto;
import com.example.leavecalendar.entity.LeaveRequest;
import com.example.leavecalendar.entity.LeaveRequest.LeaveStatus;
import com.example.leavecalendar.entity.TeamMember;
import com.example.leavecalendar.repository.LeaveRequestRepository;
import com.example.leavecalendar.repository.TeamMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRepo;
    private final TeamMemberRepository memberRepo;

    public LeaveRequestService(LeaveRequestRepository leaveRepo,
                               TeamMemberRepository memberRepo) {
        this.leaveRepo = leaveRepo;
        this.memberRepo = memberRepo;
    }

    public List<LeaveRequest> getAll() {
        return leaveRepo.findAll();
    }

    public LeaveRequest create(LeaveRequestDto dto) {
        TeamMember member = memberRepo.findById(dto.getMemberId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Team member not found"));

        if (dto.getStartDate() == null || dto.getEndDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates are required");

        if (dto.getEndDate().isBefore(dto.getStartDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");

        List<LeaveRequest> overlaps = leaveRepo.findOverlapping(
            dto.getMemberId(), dto.getStartDate(), dto.getEndDate(), LeaveStatus.REJECTED);

        if (!overlaps.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Leave request overlaps with an existing request");

        LeaveRequest req = new LeaveRequest();
        req.setMember(member);
        req.setStartDate(dto.getStartDate());
        req.setEndDate(dto.getEndDate());
        req.setReason(dto.getReason());
        req.setStatus(LeaveStatus.PENDING);

        return leaveRepo.save(req);
    }

    public LeaveRequest updateStatus(Long id, LeaveStatus newStatus) {
        LeaveRequest req = leaveRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Leave request not found"));

        req.setStatus(newStatus);
        return leaveRepo.save(req);
    }

    public void delete(Long id) {
        if (!leaveRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave request not found");
        leaveRepo.deleteById(id);
    }
}