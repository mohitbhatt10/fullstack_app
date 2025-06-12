package com.school.repository;

import com.school.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamTypeRepository extends JpaRepository<ExamType, Long> {
    
    List<ExamType> findByActiveTrue();
    
    @Query("SELECT e FROM ExamType e WHERE e.active = true ORDER BY e.displayOrder ASC, e.name ASC")
    List<ExamType> findActiveExamTypesOrdered();
    
    Optional<ExamType> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT e FROM ExamType e ORDER BY e.displayOrder ASC, e.name ASC")
    List<ExamType> findAllOrdered();
}