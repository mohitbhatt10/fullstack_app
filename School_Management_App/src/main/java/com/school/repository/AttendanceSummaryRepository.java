package com.school.repository;

import com.school.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, Long> {
    
    @Query("SELECT a FROM AttendanceSummary a WHERE a.schedule.id = :scheduleId AND a.date = :date")
    Optional<AttendanceSummary> findByScheduleAndDate(@Param("scheduleId") Long scheduleId, @Param("date") LocalDate date);

    @Query("SELECT a FROM AttendanceSummary a WHERE a.teacher.id = :teacherId ORDER BY a.updatedDate DESC")
    List<AttendanceSummary> findByTeacherId(@Param("teacherId") Long teacherId);

    List<AttendanceSummary> findByScheduleId(Long scheduleId);

    @Query("SELECT a FROM AttendanceSummary a WHERE a.date BETWEEN :startDate AND :endDate ORDER BY a.updatedDate DESC")
    List<AttendanceSummary> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
} 