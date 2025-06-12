package com.school.config;

import com.school.entity.*;
import com.school.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

@ConditionalOnProperty(name = "startup_entities_required", havingValue = "true")
@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            BaseUserRepository userRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            SessionRepository sessionRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@school.com");
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
            }

            // Create default teacher if not exists
            Teacher teacher;
            if (!teacherRepository.existsByUsername("teacher")) {
                teacher = new Teacher();
                teacher.setUsername("teacher");
                teacher.setPassword(passwordEncoder.encode("teacher123"));
                teacher.setEmail("teacher@school.com");
                teacher.setFirstName("John");
                teacher.setLastName("Smith");
                teacher.setRole(UserRole.TEACHER);
                teacher.setDepartment("Computer Science");
                teacher.setDesignation("Professor");
                teacher.setSpecialization("Software Engineering");
                teacher = teacherRepository.save(teacher);
            } else {
                teacher = teacherRepository.findByUsername("teacher")
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            }

            // Create default student if not exists
            Student student;
            if (!studentRepository.existsByUsername("student")) {
                student = new Student();
                student.setUsername("student");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setEmail("student@school.com");
                student.setFirstName("Jane");
                student.setLastName("Doe");
                student.setRole(UserRole.STUDENT);
                student.setDepartment("Computer Science");
                student.setSemester(1);
                student.setRollNumber("CS2025001");
                student = studentRepository.save(student);
            } else {
                student = studentRepository.findByUsername("student")
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            }

            // Create default session if not exists
            Session session;
            if(!sessionRepository.existsById(1L)){
                session = new Session();
                session.setName("2024-2025");
                session.setStartDate(java.time.LocalDate.of(2024, 1, 1));
                session.setEndDate(java.time.LocalDate.of(2025, 1, 1));
                session.setActive(true);
                session = sessionRepository.save(session);
            }
            else{
                session = sessionRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Session not found"));
                session.setActive(true);
            }

            // Create default course if not exists
            if (!courseRepository.existsByCode("CS101")) {
                Course course = new Course();                
                course.setName("Introduction to Programming");
                course.setCode("CS101");
                course.setSemester(1);
                course.setDepartment("Computer Science");
                course.setSyllabus("This course introduces students to the fundamentals of programming using modern programming languages. " +
                    "Topics covered include:\n\n" +
                    "• Programming fundamentals and problem-solving techniques\n" +
                    "• Variables, data types, and operators\n" +
                    "• Control structures (loops, conditionals)\n" +
                    "• Functions and modular programming\n" +
                    "• Arrays and basic data structures\n" +
                    "• Object-oriented programming concepts\n" +
                    "• File handling and input/output operations\n" +
                    "• Debugging and testing strategies\n" +
                    "• Software development best practices\n\n" +
                    "Learning Objectives:\n" +
                    "By the end of this course, students will be able to write, debug, and maintain simple to moderately complex programs, " +
                    "understand fundamental programming concepts, and apply problem-solving skills to develop software solutions.");
                course.setTeachers(new HashSet<>());
                course.getTeachers().add(teacher);
                course.setSession(session);
                course.setStudents(new HashSet<>());
                course.getStudents().add(student);
                courseRepository.save(course);
            }
        };
    }
}