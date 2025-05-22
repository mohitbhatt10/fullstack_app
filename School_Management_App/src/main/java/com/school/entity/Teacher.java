package com.school.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "teachers")
public class Teacher extends User {
    
    @Column(nullable = false)
    private String department;
    
    @Column(nullable = false)
    private String designation;
    
    @Column(nullable = false)
    private String specialization;
}
