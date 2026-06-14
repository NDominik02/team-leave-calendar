package com.example.leavecalendar.dto;

import java.time.LocalDate;

public class OnCallWeekDto {
    private int year;
    private int weekNumber;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Long memberId;
    private String memberName;
    private boolean hasConflict;
    private boolean isOverride;

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getWeekNumber() { return weekNumber; }
    public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }
    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public boolean isHasConflict() { return hasConflict; }
    public void setHasConflict(boolean hasConflict) { this.hasConflict = hasConflict; }
    public boolean isOverride() { return isOverride; }
    public void setOverride(boolean override) { isOverride = override; }
}