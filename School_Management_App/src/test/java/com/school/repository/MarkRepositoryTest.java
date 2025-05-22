package com.school.repository;

import com.school.config.TestConfig;
import com.school.entity.Course;
import com.school.entity.Mark;
import com.school.entity.Student;
import com.school.entity.Teacher;
import com.school.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class MarkRepositoryTest {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Student student;
    private Course course;
    private Teacher teacher;
    private Mark mark;

    @BeforeEach
    void setUp() {
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
        studentRepository.save(student);        // Create and save a course        course = new Course();
        course.setName("Test Course");
        course.setCode("TC101");
        course.setSemester(1);
        course.setDepartment("Computer Science");
        course.setTeacher(teacher);
        course.getStudents().add(student);
        courseRepository.save(course);

        // Create and save a mark
        mark = new Mark();
        mark.setStudent(student);
        mark.setCourse(course);
        mark.setSemester(1);
        mark.setExamType("Midterm");
        mark.setMarks(85.0);
        mark.setMaxMarks(100.0);
        mark.setEnteredByTeacher(teacher);
        markRepository.save(mark);
    }

    @Test
    void findByCourseStudentId_ShouldReturnMarksForStudent() {
        List<Mark> marks = markRepository.findByCourseStudentId(student.getId());
        
        assertThat(marks).isNotEmpty();
        assertThat(marks).hasSize(1);
        assertThat(marks.get(0).getStudent().getId()).isEqualTo(student.getId());
        assertThat(marks.get(0).getCourse().getId()).isEqualTo(course.getId());
    }

    @Test
    void findByStudentId_ShouldReturnMarksForStudent() {
        List<Mark> marks = markRepository.findByStudentId(student.getId());
        
        assertThat(marks).isNotEmpty();
        assertThat(marks).hasSize(1);
        assertThat(marks.get(0).getStudent().getId()).isEqualTo(student.getId());
    }

    @Test
    void findByStudentIdAndCourseId_ShouldReturnMarksForStudentAndCourse() {
        List<Mark> marks = markRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
        
        assertThat(marks).isNotEmpty();
        assertThat(marks).hasSize(1);
        assertThat(marks.get(0).getStudent().getId()).isEqualTo(student.getId());
        assertThat(marks.get(0).getCourse().getId()).isEqualTo(course.getId());
    }

    @Test
    void findByEnteredByTeacherUsername_ShouldReturnMarksEnteredByTeacher() {
        List<Mark> marks = markRepository.findByEnteredByTeacherUsername(teacher.getUsername());
        
        assertThat(marks).isNotEmpty();
        assertThat(marks).hasSize(1);
        assertThat(marks.get(0).getEnteredByTeacher().getUsername()).isEqualTo(teacher.getUsername());
    }
}
