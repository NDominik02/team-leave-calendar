package com.example.leavecalendar.repository;

import com.example.leavecalendar.entity.LeaveRequest;
import com.example.leavecalendar.entity.LeaveRequest.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByMemberId(Long memberId);

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.member.id = :memberId
        AND lr.status != :excludedStatus
        AND lr.startDate <= :endDate
        AND lr.endDate >= :startDate
    """)
    List<LeaveRequest> findOverlapping(
        @Param("memberId") Long memberId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("excludedStatus") LeaveStatus excludedStatus
    );
}