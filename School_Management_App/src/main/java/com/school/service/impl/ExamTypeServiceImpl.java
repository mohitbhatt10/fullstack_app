package com.school.service.impl;

import com.school.dto.ExamTypeDTO;
import com.school.entity.ExamType;
import com.school.repository.ExamTypeRepository;
import com.school.service.ExamTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamTypeServiceImpl implements ExamTypeService {

    private final ExamTypeRepository examTypeRepository;

    @Override
    public ExamTypeDTO createExamType(ExamTypeDTO examTypeDTO) {
        if (existsByName(examTypeDTO.getName())) {
            throw new RuntimeException("Exam type with name '" + examTypeDTO.getName() + "' already exists");
        }

        ExamType examType = new ExamType();
        BeanUtils.copyProperties(examTypeDTO, examType);
        
        ExamType savedExamType = examTypeRepository.save(examType);
        return mapEntityToDTO(savedExamType);
    }

    @Override
    public ExamTypeDTO updateExamType(Long id, ExamTypeDTO examTypeDTO) {
        ExamType existingExamType = examTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam type not found with id: " + id));

        if (existsByNameAndIdNot(examTypeDTO.getName(), id)) {
            throw new RuntimeException("Exam type with name '" + examTypeDTO.getName() + "' already exists");
        }

        existingExamType.setName(examTypeDTO.getName());
        existingExamType.setDescription(examTypeDTO.getDescription());
        existingExamType.setActive(examTypeDTO.getActive());
        existingExamType.setDisplayOrder(examTypeDTO.getDisplayOrder());

        ExamType updatedExamType = examTypeRepository.save(existingExamType);
        return mapEntityToDTO(updatedExamType);
    }

    @Override
    public void deleteExamType(Long id) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam type not found with id: " + id));
        
        // Instead of hard delete, we can soft delete by setting active to false
        examType.setActive(false);
        examTypeRepository.save(examType);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamTypeDTO getExamTypeById(Long id) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam type not found with id: " + id));
        return mapEntityToDTO(examType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamTypeDTO> getAllExamTypes() {
        return examTypeRepository.findAllOrdered().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamTypeDTO> getActiveExamTypes() {
        return examTypeRepository.findByActiveTrue().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamTypeDTO> getActiveExamTypesOrdered() {
        return examTypeRepository.findActiveExamTypesOrdered().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return examTypeRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Long id) {
        return examTypeRepository.findByNameIgnoreCase(name)
                .map(examType -> !examType.getId().equals(id))
                .orElse(false);
    }

    private ExamTypeDTO mapEntityToDTO(ExamType examType) {
        ExamTypeDTO dto = new ExamTypeDTO();
        BeanUtils.copyProperties(examType, dto);
        return dto;
    }
}