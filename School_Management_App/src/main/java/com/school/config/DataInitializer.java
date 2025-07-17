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
            CourseScheduleRepository courseScheduleRepository,
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
                teacher.setDepartment("CSE");
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
                student.setDepartment("CSE");
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
                course.setDepartment("CSE");
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
                
                // Create additional courses for better timetable demonstration
                Course course2 = new Course();
                course2.setName("Data Structures");
                course2.setCode("CS201");
                course2.setSemester(1);
                course2.setDepartment("CSE");
                course2.setSyllabus("Advanced data structures and algorithms");
                course2.setTeachers(new HashSet<>());
                course2.getTeachers().add(teacher);
                course2.setSession(session);
                course2.setStudents(new HashSet<>());
                course2.getStudents().add(student);
                courseRepository.save(course2);
                
                Course course3 = new Course();
                course3.setName("Database Systems");
                course3.setCode("CS301");
                course3.setSemester(1);
                course3.setDepartment("CSE");
                course3.setSyllabus("Database design and management systems");
                course3.setTeachers(new HashSet<>());
                course3.getTeachers().add(teacher);
                course3.setSession(session);
                course3.setStudents(new HashSet<>());
                course3.getStudents().add(student);
                courseRepository.save(course3);
                
                // Create course schedules for better timetable demonstration
                createCourseSchedule(courseScheduleRepository, course, java.time.DayOfWeek.MONDAY, 
                    java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 30), "Room A101");
                createCourseSchedule(courseScheduleRepository, course, java.time.DayOfWeek.WEDNESDAY, 
                    java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 30), "Room A101");
                createCourseSchedule(courseScheduleRepository, course, java.time.DayOfWeek.FRIDAY, 
                    java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 30), "Room A101");
                    
                createCourseSchedule(courseScheduleRepository, course2, java.time.DayOfWeek.TUESDAY, 
                    java.time.LocalTime.of(11, 0), java.time.LocalTime.of(12, 30), "Room B201");
                createCourseSchedule(courseScheduleRepository, course2, java.time.DayOfWeek.THURSDAY, 
                    java.time.LocalTime.of(11, 0), java.time.LocalTime.of(12, 30), "Room B201");
                    
                createCourseSchedule(courseScheduleRepository, course3, java.time.DayOfWeek.MONDAY, 
                    java.time.LocalTime.of(14, 0), java.time.LocalTime.of(15, 30), "Lab C301");
                createCourseSchedule(courseScheduleRepository, course3, java.time.DayOfWeek.WEDNESDAY, 
                    java.time.LocalTime.of(14, 0), java.time.LocalTime.of(15, 30), "Lab C301");
                createCourseSchedule(courseScheduleRepository, course3, java.time.DayOfWeek.FRIDAY, 
                    java.time.LocalTime.of(14, 0), java.time.LocalTime.of(15, 30), "Lab C301");
            }
        };
    }
    
    private void createCourseSchedule(CourseScheduleRepository courseScheduleRepository, 
                                    Course course, java.time.DayOfWeek dayOfWeek, 
                                    java.time.LocalTime startTime, java.time.LocalTime endTime, 
                                    String classroom) {
        com.school.entity.CourseSchedule schedule = new com.school.entity.CourseSchedule();
        schedule.setCourse(course);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setClassroom(classroom);
        courseScheduleRepository.save(schedule);
    }
}