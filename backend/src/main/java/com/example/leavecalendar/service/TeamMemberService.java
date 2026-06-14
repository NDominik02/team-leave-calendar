package com.example.leavecalendar.service;

import com.example.leavecalendar.entity.TeamMember;
import com.example.leavecalendar.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberService {

    private final TeamMemberRepository repository;

    public TeamMemberService(TeamMemberRepository repository) {
        this.repository = repository;
    }

    public List<TeamMember> getAll() {
        return repository.findAll();
    }

    public void seedIfEmpty() {
        if (repository.count() == 0) {
            repository.save(new TeamMember("Alice", "#22c55e"));
            repository.save(new TeamMember("Bob", "#3b82f6"));
            repository.save(new TeamMember("Charlie", "#f97316"));
            repository.save(new TeamMember("Diana", "#a855f7"));
        }
    }
}