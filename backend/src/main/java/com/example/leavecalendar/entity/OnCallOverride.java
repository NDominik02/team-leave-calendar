package com.example.leavecalendar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "oncall_overrides")
public class OnCallOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int weekNumber;

    @Column(name = "week_year", nullable = false)
    private int year;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private TeamMember member;

    public OnCallOverride() {}

    public Long getId() { return id; }
    public int getWeekNumber() { return weekNumber; }
    public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public TeamMember getMember() { return member; }
    public void setMember(TeamMember member) { this.member = member; }
}