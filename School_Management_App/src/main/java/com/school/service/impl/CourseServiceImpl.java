package com.school.service.impl;

import com.school.dto.CourseDTO;
import com.school.entity.Course;
import com.school.entity.CourseEnrollment;
import com.school.entity.Student;
import com.school.entity.Teacher;
import com.school.repository.CourseEnrollmentRepository;
import com.school.repository.CourseRepository;
import com.school.repository.SessionRepository;
import com.school.repository.StudentRepository;
import com.school.repository.TeacherRepository;
import com.school.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    public CourseServiceImpl(CourseRepository courseRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository,
                             SessionRepository sessionRepository,
                             CourseEnrollmentRepository courseEnrollmentRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.sessionRepository = sessionRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        mapDTOToEntity(courseDTO, course);
        Course savedCourse = courseRepository.save(course);
        return mapEntityToDTO(savedCourse);
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        mapDTOToEntity(courseDTO, course);
        Course updatedCourse = courseRepository.save(course);
        return mapEntityToDTO(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapEntityToDTO(course);
    }

    @Override
    public CourseDTO getCourseByCode(String code) {
        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapEntityToDTO(course);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesBySemester(Integer semester) {
        return courseRepository.findBySemester(semester).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeachersId(teacherId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return student.getCourses().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesByTeacherUsername(String username) {
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return courseRepository.findByTeachersId(teacher.getId()).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addTeacherToCourse(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        if (course.getTeachers() == null) {
            course.setTeachers(new HashSet<>());
        }
        course.getTeachers().add(teacher);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void removeTeacherFromCourse(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        if (course.getTeachers() != null) {
            course.getTeachers().remove(teacher);
            courseRepository.save(course);
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return courseRepository.existsByCode(code);
    }

    @Override
    @Transactional
    public void addStudentToCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        CourseEnrollment enrollment = new CourseEnrollment();        
          // Validate semester and department match
        if (!student.getSemester().equals(course.getSemester())) {
            throw new RuntimeException("Student's semester (" + student.getSemester() 
                + ") does not match course semester (" + course.getSemester() + ")");
        }

        if (!student.getDepartment().equals(course.getDepartment())) {
            throw new RuntimeException("Student's department (" + student.getDepartment()
                + ") does not match course department (" + course.getDepartment() + ")");
        }
        
        if (course.getStudents() == null) {
            course.setStudents(new HashSet<>());
        }

        // Add the CourseEnrollment for the student
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setActive(true);
        enrollment.setSession(course.getSession());
        
        courseEnrollmentRepository.save(enrollment);
        
        course.getStudents().add(student);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.
                findByStudentIdAndCourseIdAndActive(studentId, courseId, true);
        if (enrollments != null && !enrollments.isEmpty()) {
            courseEnrollmentRepository.deleteAll(enrollments);
        }

        if (course.getStudents() != null) {
            course.getStudents().remove(student);
            courseRepository.save(course);
        }
    }

    private void mapDTOToEntity(CourseDTO dto, Course entity) {        
        BeanUtils.copyProperties(dto, entity, "id", "teacherIds", "studentIds");
        
        // Handle teachers
        if (dto.getTeacherIds() != null && !dto.getTeacherIds().isEmpty()) {
            Set<Teacher> teachers = dto.getTeacherIds().stream()
                    .map(id -> teacherRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id)))
                    .collect(Collectors.toSet());
            entity.setTeachers(teachers);
        } else {
            entity.setTeachers(new HashSet<>());
        }

        // Handle students
        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            Set<Student> students = dto.getStudentIds().stream()
                    .map(id -> studentRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Student not found")))
                    .collect(Collectors.toSet());
            entity.setStudents(students);
        }

        // Handle session
        if (dto.getSessionId() != null) {
            entity.setSession(sessionRepository.findById(dto.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found with id: " + dto.getSessionId())));
        } else {
            entity.setSession(null);
        }
    }    
    private CourseDTO mapEntityToDTO(Course entity) {
                
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(entity, dto);
        
        // Handle teachers
        if (entity.getTeachers() != null && !entity.getTeachers().isEmpty()) {
            Set<Long> teacherIds = entity.getTeachers().stream()
                    .map(Teacher::getId)
                    .collect(Collectors.toSet());
            dto.setTeacherIds(teacherIds);
            
            String teacherNames = entity.getTeachers().stream()
                    .map(teacher -> teacher.getFirstName() + " " + teacher.getLastName())
                    .collect(Collectors.joining(", "));
            dto.setTeacherNames(teacherNames);
        } else {
            dto.setTeacherIds(new HashSet<>());
            dto.setTeacherNames("");
        }

        // Handle session
        if(entity.getSession() != null) {
            dto.setSessionName(entity.getSession().getName());
            dto.setSessionId(entity.getSession().getId());
        }
        else {
            dto.setSessionName("N/A");
            dto.setSessionId(null);
        }
        
        // Handle students
        if (entity.getStudents() != null) {
            Set<Long> studentIds = entity.getStudents().stream()
                    .map(Student::getId)
                    .collect(Collectors.toSet());
            dto.setStudentIds(studentIds);
            dto.setStudentCount(studentIds.size());
        } else {
            dto.setStudentCount(0);
        }
        return dto;
    }

    @Override
    public Integer getCourseCount() {
        return (int) courseRepository.count();
    }
}
