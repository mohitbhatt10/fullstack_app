package com.school.repository;

import com.school.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByName(String name);
    Optional<Session> findByActive(boolean active);
}
