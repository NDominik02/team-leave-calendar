package com.example.leavecalendar.repository;

import com.example.leavecalendar.entity.OnCallOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnCallOverrideRepository extends JpaRepository<OnCallOverride, Long> {
    Optional<OnCallOverride> findByYearAndWeekNumber(int year, int weekNumber);
}