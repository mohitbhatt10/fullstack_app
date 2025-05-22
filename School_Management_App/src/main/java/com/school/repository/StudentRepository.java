package com.school.repository;

import com.school.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends UserRepository<Student> {
    Optional<Student> findByRollNumber(String rollNumber);
    List<Student> findBySemester(Integer semester);
    List<Student> findByDepartment(String department);
    boolean existsByRollNumber(String rollNumber);

    @Query("SELECT s FROM Student s WHERE " +
           "(:department IS NULL OR s.department = :department) AND " +
           "(:semester IS NULL OR s.semester = :semester) AND " +
           "(:name IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Student> findByFilters(@Param("department") String department,
                              @Param("semester") Integer semester,
                              @Param("name") String name,
                              Pageable pageable);
}
