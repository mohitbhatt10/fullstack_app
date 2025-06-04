package com.school.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "2024-2025"

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active = false;

    @OneToMany(mappedBy = "session")
    @ToString.Exclude
    private Set<Course> courses = new HashSet<>();

    // Helper method to check if a date falls within this session
    public boolean isDateInSession(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        Session session = (Session) o;
        return id != null && id.equals(session.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
