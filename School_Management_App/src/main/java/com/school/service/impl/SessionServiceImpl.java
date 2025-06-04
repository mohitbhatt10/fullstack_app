package com.school.service.impl;

import com.school.dto.SessionDTO;
import com.school.entity.Session;
import com.school.repository.CourseEnrollmentRepository;
import com.school.repository.CourseRepository;
import com.school.repository.SessionRepository;
import com.school.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository, 
                             CourseRepository courseRepository,
                             CourseEnrollmentRepository courseEnrollmentRepository) {
        this.sessionRepository = sessionRepository;
        this.courseRepository = courseRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
    }

    @Override
    @Transactional
    public SessionDTO createSession(SessionDTO sessionDTO) {
        Session session = new Session();
        session.setName(sessionDTO.getName());
        session.setStartDate(sessionDTO.getStartDate());
        session.setEndDate(sessionDTO.getEndDate());
        session.setActive(sessionDTO.isActive());

        // If this session is being set as active, deactivate all other sessions
        if (sessionDTO.isActive()) {
            deactivateAllSessions();
        }

        Session savedSession = sessionRepository.save(session);
        return convertToDTO(savedSession);
    }

    @Override
    @Transactional
    public SessionDTO updateSession(Long id, SessionDTO sessionDTO) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));

        session.setName(sessionDTO.getName());
        session.setStartDate(sessionDTO.getStartDate());
        session.setEndDate(sessionDTO.getEndDate());

        // Only update active status if it's changing to active
        if (!session.isActive() && sessionDTO.isActive()) {
            deactivateAllSessions();
            session.setActive(true);
        }

        Session updatedSession = sessionRepository.save(session);
        return convertToDTO(updatedSession);
    }

    @Override
    @Transactional
    public void deleteSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));

        // Check if session has any courses
        if (!session.getCourses().isEmpty()) {
            throw new IllegalStateException("Cannot delete session with associated courses");
        }

        sessionRepository.delete(session);
    }

    @Override
    public SessionDTO getSessionById(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));
        return convertToDTO(session);
    }

    @Override
    public SessionDTO getSessionByName(String name) {
        Session session = sessionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with name: " + name));
        return convertToDTO(session);
    }

    @Override
    public SessionDTO getActiveSession() {
        Session session = sessionRepository.findByActive(true)
                .orElseThrow(() -> new EntityNotFoundException("No active session found"));
        return convertToDTO(session);
    }

    @Override
    public List<SessionDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SessionDTO activateSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));

        // Deactivate all sessions
        deactivateAllSessions();

        // Activate the selected session
        session.setActive(true);
        Session updatedSession = sessionRepository.save(session);

        return convertToDTO(updatedSession);
    }

    private void deactivateAllSessions() {
        Optional<Session> activeSession = sessionRepository.findByActive(true);
        if (activeSession.isPresent()) {
            Session session = activeSession.get();
            session.setActive(false);
            sessionRepository.save(session);
        }
    }

    private SessionDTO convertToDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());
        dto.setName(session.getName());
        dto.setStartDate(session.getStartDate());
        dto.setEndDate(session.getEndDate());
        dto.setActive(session.isActive());

        // Get counts for display
        dto.setCourseCount(session.getCourses().size());

        // Count all active enrollments for this session
        Integer studentCount = courseEnrollmentRepository.findBySessionIdAndActive(session.getId(), true).size();
        dto.setStudentCount(studentCount);

        return dto;
    }
}
