package com.school.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(callSuper = true, exclude = {"courses"})
@Table(name = "teachers")
public class Teacher extends User {
    
    @Column(nullable = false)
    private String department;
    
    @Column(nullable = false)
    private String designation;
    
    @Column(nullable = false)
    private String specialization;
    
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Course> courses = new HashSet<>();
}
