package com.example.leavecalendar.controller;

import com.example.leavecalendar.entity.TeamMember;
import com.example.leavecalendar.service.TeamMemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173")
public class TeamMemberController {

    private final TeamMemberService service;

    public TeamMemberController(TeamMemberService service) {
        this.service = service;
    }

    @GetMapping
    public List<TeamMember> getAll() {
        return service.getAll();
    }
}