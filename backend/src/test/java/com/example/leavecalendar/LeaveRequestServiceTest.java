package com.example.leavecalendar;

import com.example.leavecalendar.dto.LeaveRequestDto;
import com.example.leavecalendar.entity.LeaveRequest;
import com.example.leavecalendar.entity.TeamMember;
import com.example.leavecalendar.repository.LeaveRequestRepository;
import com.example.leavecalendar.repository.TeamMemberRepository;
import com.example.leavecalendar.service.LeaveRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LeaveRequestServiceTest {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private TeamMember alice;

    @BeforeEach
    void setUp() {
        leaveRequestRepository.deleteAll();
        teamMemberRepository.deleteAll();
        alice = teamMemberRepository.save(new TeamMember("Alice"));
    }

    @Test
    void shouldCreateLeaveRequest() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setMemberId(alice.getId());
        dto.setStartDate(LocalDate.of(2026, 8, 11));
        dto.setEndDate(LocalDate.of(2026, 8, 14));
        dto.setReason("Vacation");

        LeaveRequest result = leaveRequestService.create(dto);

        assertNotNull(result.getId());
        assertEquals(LeaveRequest.LeaveStatus.PENDING, result.getStatus());
        assertEquals(alice.getId(), result.getMember().getId());
    }

    @Test
    void shouldRejectOverlappingLeaveRequest() {
        LeaveRequestDto first = new LeaveRequestDto();
        first.setMemberId(alice.getId());
        first.setStartDate(LocalDate.of(2026, 8, 11));
        first.setEndDate(LocalDate.of(2026, 8, 14));
        leaveRequestService.create(first);

        LeaveRequestDto overlapping = new LeaveRequestDto();
        overlapping.setMemberId(alice.getId());
        overlapping.setStartDate(LocalDate.of(2026, 8, 13));
        overlapping.setEndDate(LocalDate.of(2026, 8, 16));

        assertThrows(ResponseStatusException.class,
            () -> leaveRequestService.create(overlapping));
    }

    @Test
    void shouldAllowNonOverlappingLeaveRequests() {
        LeaveRequestDto first = new LeaveRequestDto();
        first.setMemberId(alice.getId());
        first.setStartDate(LocalDate.of(2026, 8, 11));
        first.setEndDate(LocalDate.of(2026, 8, 14));
        leaveRequestService.create(first);

        LeaveRequestDto second = new LeaveRequestDto();
        second.setMemberId(alice.getId());
        second.setStartDate(LocalDate.of(2026, 8, 15));
        second.setEndDate(LocalDate.of(2026, 8, 18));

        assertDoesNotThrow(() -> leaveRequestService.create(second));
    }

    @Test
    void shouldUpdateStatus() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setMemberId(alice.getId());
        dto.setStartDate(LocalDate.of(2026, 8, 11));
        dto.setEndDate(LocalDate.of(2026, 8, 14));
        LeaveRequest created = leaveRequestService.create(dto);

        LeaveRequest approved = leaveRequestService.updateStatus(
            created.getId(), LeaveRequest.LeaveStatus.APPROVED);

        assertEquals(LeaveRequest.LeaveStatus.APPROVED, approved.getStatus());
    }

    @Test
    void shouldRejectIfEndDateBeforeStartDate() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setMemberId(alice.getId());
        dto.setStartDate(LocalDate.of(2026, 8, 14));
        dto.setEndDate(LocalDate.of(2026, 8, 11));

        assertThrows(ResponseStatusException.class,
            () -> leaveRequestService.create(dto));
    }
}