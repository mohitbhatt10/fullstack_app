package com.school.service.impl;

import com.school.dto.CourseEnrollmentDTO;
import com.school.entity.Course;
import com.school.entity.CourseEnrollment;
import com.school.entity.Session;
import com.school.entity.Student;
import com.school.repository.CourseEnrollmentRepository;
import com.school.repository.CourseRepository;
import com.school.repository.SessionRepository;
import com.school.repository.StudentRepository;
import com.school.service.CourseEnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {

    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SessionRepository sessionRepository;

    public CourseEnrollmentServiceImpl(CourseEnrollmentRepository courseEnrollmentRepository,
                                      StudentRepository studentRepository,
                                      CourseRepository courseRepository,
                                      SessionRepository sessionRepository) {
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<CourseEnrollmentDTO> getAllEnrollments() {
        return courseEnrollmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseEnrollmentDTO createEnrollment(CourseEnrollmentDTO enrollmentDTO) {
        Student student = studentRepository.findById(enrollmentDTO.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + enrollmentDTO.getStudentId()));

        Course course = courseRepository.findById(enrollmentDTO.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + enrollmentDTO.getCourseId()));

        Session session = sessionRepository.findById(enrollmentDTO.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + enrollmentDTO.getSessionId()));

        // Check if enrollment already exists and is active
        List<CourseEnrollment> existingEnrollments = courseEnrollmentRepository
                .findByStudentIdAndCourseIdAndSessionIdAndActive(
                        student.getId(), course.getId(), session.getId());

        if (!existingEnrollments.isEmpty()) {
            throw new IllegalStateException("Student is already enrolled in this course for the selected session");
        }

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSession(session);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setActive(true);

        CourseEnrollment savedEnrollment = courseEnrollmentRepository.save(enrollment);
        return convertToDTO(savedEnrollment);
    }

    @Override
    @Transactional
    public CourseEnrollmentDTO updateEnrollment(Long id, CourseEnrollmentDTO enrollmentDTO) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + id));

        enrollment.setActive(enrollmentDTO.isActive());

        if (enrollmentDTO.getWithdrawalDate() != null) {
            enrollment.setWithdrawalDate(enrollmentDTO.getWithdrawalDate());
        }

        CourseEnrollment updatedEnrollment = courseEnrollmentRepository.save(enrollment);
        return convertToDTO(updatedEnrollment);
    }

    @Override
    @Transactional
    public void deleteEnrollment(Long id) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + id));

        courseEnrollmentRepository.delete(enrollment);
    }

    @Override
    public CourseEnrollmentDTO getEnrollmentById(Long id) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + id));

        return convertToDTO(enrollment);
    }

    @Override
    public List<CourseEnrollmentDTO> getEnrollmentsByStudentId(Long studentId) {
        return courseEnrollmentRepository.findByStudentIdAndActive(studentId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseEnrollmentDTO> getEnrollmentsByCourseId(Long courseId) {
        return courseEnrollmentRepository.findByCourseIdAndActive(courseId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseEnrollmentDTO> getEnrollmentsBySessionId(Long sessionId) {
        return courseEnrollmentRepository.findBySessionIdAndActive(sessionId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseEnrollmentDTO> getActiveEnrollmentsByStudentId(Long studentId) {
        return courseEnrollmentRepository.findByStudentIdAndActive(studentId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseEnrollmentDTO> getActiveEnrollmentsByCourseId(Long courseId) {
        return courseEnrollmentRepository.findByCourseIdAndActive(courseId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseEnrollmentDTO withdrawStudentFromCourse(Long studentId, Long courseId) {
        List<CourseEnrollment> enrollments = courseEnrollmentRepository
                .findByStudentIdAndCourseIdAndActive(studentId, courseId, true);

        if (enrollments.isEmpty()) {
            throw new EntityNotFoundException("No active enrollment found for student ID: " + studentId + 
                                           " and course ID: " + courseId);
        }

        CourseEnrollment enrollment = enrollments.get(0);
        enrollment.setActive(false);
        enrollment.setWithdrawalDate(LocalDate.now());

        CourseEnrollment updatedEnrollment = courseEnrollmentRepository.save(enrollment);
        return convertToDTO(updatedEnrollment);
    }

    @Override
    public List<CourseEnrollmentDTO> getEnrollmentsByStudentIdAndSessionId(Long studentId, Long sessionId) {
        return courseEnrollmentRepository.findByStudentIdAndActive(studentId, true).stream()
                .filter(enrollment -> enrollment.getSession().getId().equals(sessionId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CourseEnrollmentDTO convertToDTO(CourseEnrollment enrollment) {
        CourseEnrollmentDTO dto = new CourseEnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setStudentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());
        dto.setStudentRollNumber(enrollment.getStudent().getRollNumber());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseName(enrollment.getCourse().getName());
        dto.setCourseCode(enrollment.getCourse().getCode());
        dto.setSessionId(enrollment.getSession().getId());
        dto.setSessionName(enrollment.getSession().getName());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setWithdrawalDate(enrollment.getWithdrawalDate());
        dto.setActive(enrollment.isActive());

        return dto;
    }
}
