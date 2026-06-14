package com.example.leavecalendar.controller;

import com.example.leavecalendar.dto.LeaveRequestDto;
import com.example.leavecalendar.entity.LeaveRequest;
import com.example.leavecalendar.entity.LeaveRequest.LeaveStatus;
import com.example.leavecalendar.service.LeaveRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-requests")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveRequestController {

    private final LeaveRequestService service;

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<LeaveRequest> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequest create(@RequestBody LeaveRequestDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}/status")
    public LeaveRequest updateStatus(@PathVariable Long id,
                                     @RequestBody Map<String, String> body) {
        LeaveStatus status = LeaveStatus.valueOf(body.get("status"));
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}