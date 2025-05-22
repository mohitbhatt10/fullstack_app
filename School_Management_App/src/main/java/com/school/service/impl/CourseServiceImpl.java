package com.school.service.impl;

import com.school.dto.CourseDTO;
import com.school.entity.Course;
import com.school.entity.Student;
import com.school.entity.Teacher;
import com.school.repository.CourseRepository;
import com.school.repository.StudentRepository;
import com.school.repository.TeacherRepository;
import com.school.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CourseServiceImpl(CourseRepository courseRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
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
        return courseRepository.findByTeacherId(teacherId).stream()
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
        return courseRepository.findByTeacherId(teacher.getId()).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
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
        
        if (course.getStudents() != null) {
            course.getStudents().remove(student);
            courseRepository.save(course);
        }
    }

    private void mapDTOToEntity(CourseDTO dto, Course entity) {        BeanUtils.copyProperties(dto, entity, "id", "teacherId", "studentIds");
        
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        entity.setTeacher(teacher);

        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            Set<Student> students = dto.getStudentIds().stream()
                    .map(id -> studentRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Student not found")))
                    .collect(Collectors.toSet());
            entity.setStudents(students);
        }
    }    private CourseDTO mapEntityToDTO(Course entity) {        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(entity, dto);
        
        if (entity.getTeacher() != null) {
            dto.setTeacherId(entity.getTeacher().getId());
            dto.setTeacherName(entity.getTeacher().getFirstName() + " " + entity.getTeacher().getLastName());
        }
        
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
}
