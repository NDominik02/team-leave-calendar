package com.example.leavecalendar.repository;

import com.example.leavecalendar.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    boolean existsByName(String name);
}