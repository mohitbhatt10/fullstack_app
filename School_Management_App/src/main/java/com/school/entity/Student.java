package com.school.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student extends User {
    
    @Column(nullable = false, unique = true)
    private String rollNumber;
    
    @Column(nullable = false)
    private Integer semester;
    
    @Column(nullable = false)
    private String department;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<CourseEnrollment> enrollments = new HashSet<>();

    // Helper method to get all courses this student is enrolled in
    @Transient
    public Set<Course> getCourses() {
        return enrollments.stream()
                .filter(CourseEnrollment::isActive)
                .map(CourseEnrollment::getCourse)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return getId() != null && getId().equals(student.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
