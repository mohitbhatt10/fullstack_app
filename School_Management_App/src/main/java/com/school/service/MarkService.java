package com.school.service;

import com.school.dto.MarkDTO;
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
    List<MarkDTO> getMarksByExamType(String examType);
    double calculateStudentAverage(Long studentId, Integer semester);
    List<MarkDTO> getAllMarks();
    List<MarkDTO> getMarksByStudent(Long studentId);
    List<MarkDTO> getMarksBySemester(Integer semester);
    List<MarkDTO> getRecentMarksByTeacher(String username);
}
