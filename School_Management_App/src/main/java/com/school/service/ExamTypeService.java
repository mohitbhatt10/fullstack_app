package com.school.service;

import com.school.dto.ExamTypeDTO;

import java.util.List;

public interface ExamTypeService {
    ExamTypeDTO createExamType(ExamTypeDTO examTypeDTO);
    ExamTypeDTO updateExamType(Long id, ExamTypeDTO examTypeDTO);
    void deleteExamType(Long id);
    ExamTypeDTO getExamTypeById(Long id);
    List<ExamTypeDTO> getAllExamTypes();
    List<ExamTypeDTO> getActiveExamTypes();
    List<ExamTypeDTO> getActiveExamTypesOrdered();
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}