package com.example.leavecalendar.controller;

import com.example.leavecalendar.dto.OnCallWeekDto;
import com.example.leavecalendar.entity.TeamMember;
import com.example.leavecalendar.service.OnCallService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oncall")
@CrossOrigin(origins = "http://localhost:5173")
public class OnCallController {

    private final OnCallService service;

    public OnCallController(OnCallService service) {
        this.service = service;
    }

    @GetMapping("/schedule")
    public List<OnCallWeekDto> getSchedule(
            @RequestParam(defaultValue = "8") int weeks) {
        return service.getSchedule(weeks);
    }

    @GetMapping("/available")
    public List<TeamMember> getAvailable(
            @RequestParam int year,
            @RequestParam int weekNumber) {
        return service.getAvailableMembers(year, weekNumber);
    }

    @PutMapping("/override")
    public OnCallWeekDto setOverride(@RequestBody Map<String, Object> body) {
        int year = (int) body.get("year");
        int weekNumber = (int) body.get("weekNumber");
        Long memberId = Long.valueOf(body.get("memberId").toString());
        return service.setOverride(year, weekNumber, memberId);
    }
}