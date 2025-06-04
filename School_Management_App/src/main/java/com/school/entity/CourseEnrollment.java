package com.school.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "course_enrollments")
public class CourseEnrollment {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    private Session session;

    @Column(nullable = false)
    private LocalDate enrollmentDate;

    @Column
    private LocalDate withdrawalDate;

    @Column(nullable = false)
    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseEnrollment)) return false;
        CourseEnrollment that = (CourseEnrollment) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
