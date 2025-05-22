package com.school.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "marks")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private String examType;

    @Column(nullable = false)
    private Double marks;

    @Column(nullable = false)
    private Double maxMarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entered_by_teacher_id", nullable = false)
    @ToString.Exclude
    private Teacher enteredByTeacher;
}
