package com.school.service;

import com.school.dto.MarkDTO;
import com.school.dto.MarksSummaryDTO;

import java.util.List;

public interface MarkService {
    MarkDTO createMark(MarkDTO markDTO);
    MarkDTO updateMark(Long id, MarkDTO markDTO);
    void deleteMark(Long id);
    MarkDTO getMarkById(Long id);
    List<MarkDTO> getMarksByStudentId(Long studentId);
    List<MarkDTO> getMarksByCourseId(Long courseId);
    List<MarkDTO> getMarksByStudentIdAndSemester(Long studentId, Integer semester);
    List<MarkDTO> getMarksByStudentIdAndCourseId(Long studentId, Long courseId);
    List<MarkDTO> getMarksByExamTypeId(Long examTypeId);
    List<MarkDTO> getMarksByExamTypeName(String examTypeName);
    double calculateStudentAverage(Long studentId, Integer semester);
    List<MarkDTO> getAllMarks();
    List<MarkDTO> getMarksByStudent(Long studentId);
    List<MarkDTO> getMarksBySemester(Integer semester);
    List<MarkDTO> getRecentMarksByTeacher(String username);
    List<MarksSummaryDTO> getMarksSummaryByCourseId(Long courseId);
    List<MarksSummaryDTO> getMarksSummaryByTeacher(String username);

    /**
     * Get the last N marks records for a student
     */
    List<MarkDTO> getRecentMarksForStudent(Long studentId, int limit);

    /**
     * Get the last N marks records for a teacher by username
     */
    List<MarkDTO> getRecentMarksForTeacher(String username, int limit);
}
