package com.school.repository;

import com.school.config.TestConfig;
import com.school.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class AttendanceRepositoryTest {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Student student;
    private Course course;
    private Teacher teacher;
    private Attendance attendance;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        
        // Create and save a teacher
        teacher = new Teacher();
        teacher.setUsername("teacher1");
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setPassword("password");
        teacher.setEmail("teacher1@school.com");
        teacher.setDepartment("Mathematics");
        teacher.setDesignation("Professor");
        teacher.setSpecialization("Algebra");
        teacher.setRole(UserRole.TEACHER);
        teacherRepository.save(teacher);

        // Create and save a student
        student = new Student();
        student.setFirstName("Test");
        student.setLastName("Student");
        student.setUsername("student1");
        student.setPassword("password");
        student.setEmail("student1@school.com");
        student.setRollNumber("2023001");
        student.setSemester(1);
        student.setDepartment("Computer Science");
        student.setRole(UserRole.STUDENT);
        studentRepository.save(student);        // Create and save a course        
        course = new Course();
        course.setName("Test Course");
        course.setCode("TC102");
        course.setSemester(1);
        course.setDepartment("Computer Science");
        course.getTeachers().add(teacher);
        course.getStudents().add(student);
        courseRepository.save(course);

        // Create and save attendance
        attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setCourse(course);
        attendance.setDate(today);
        attendance.setPresent(true);
        attendance.setMarkedByTeacher(teacher);
        attendanceRepository.save(attendance);
    }

    @Test
    void findByCourseStudentId_ShouldReturnAttendanceForStudent() {
        List<Attendance> attendanceList = attendanceRepository.findByCourseStudentId(student.getId());
        
        assertThat(attendanceList).isNotEmpty();
        assertThat(attendanceList).hasSize(1);
        assertThat(attendanceList.get(0).getStudent().getId()).isEqualTo(student.getId());
        assertThat(attendanceList.get(0).getCourse().getId()).isEqualTo(course.getId());
    }

    @Test
    void findByStudentId_ShouldReturnAttendanceForStudent() {
        List<Attendance> attendanceList = attendanceRepository.findByStudentId(student.getId());
        
        assertThat(attendanceList).isNotEmpty();
        assertThat(attendanceList).hasSize(1);
        assertThat(attendanceList.get(0).getStudent().getId()).isEqualTo(student.getId());
    }

    @Test
    void findByStudentIdAndCourseId_ShouldReturnAttendanceForStudentAndCourse() {
        List<Attendance> attendanceList = attendanceRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
        
        assertThat(attendanceList).isNotEmpty();
        assertThat(attendanceList).hasSize(1);
        assertThat(attendanceList.get(0).getStudent().getId()).isEqualTo(student.getId());
        assertThat(attendanceList.get(0).getCourse().getId()).isEqualTo(course.getId());
    }

    @Test
    void findByDate_ShouldReturnAttendanceForDate() {
        List<Attendance> attendanceList = attendanceRepository.findByDate(today);
        
        assertThat(attendanceList).isNotEmpty();
        assertThat(attendanceList).hasSize(1);
        assertThat(attendanceList.get(0).getDate()).isEqualTo(today);
    }

    @Test
    void findByCourseTeacherUsername_ShouldReturnAttendanceMarkedByTeacher() {
        List<Attendance> attendanceList = attendanceRepository.findByCourseTeacherUsername(teacher.getUsername());
        
        assertThat(attendanceList).isNotEmpty();
        assertThat(attendanceList).hasSize(1);
        assertThat(attendanceList.get(0).getCourse().getTeachers()).contains(teacher);
    }
}
