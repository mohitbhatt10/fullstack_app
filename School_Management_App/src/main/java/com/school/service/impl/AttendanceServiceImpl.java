package com.school.service.impl;

import com.school.dto.AttendanceDTO;
import com.school.entity.Attendance;
import com.school.entity.Course;
import com.school.entity.Student;
import com.school.entity.Teacher;
import com.school.entity.CourseSchedule;
import com.school.repository.AttendanceRepository;
import com.school.repository.CourseRepository;
import com.school.repository.StudentRepository;
import com.school.repository.TeacherRepository;
import com.school.repository.CourseScheduleRepository;
import com.school.service.AttendanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseScheduleRepository courseScheduleRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository,
                                 TeacherRepository teacherRepository,
                                 CourseScheduleRepository courseScheduleRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.courseScheduleRepository = courseScheduleRepository;
    }

    @Override
    public AttendanceDTO createAttendance(AttendanceDTO attendanceDTO) {
        //validateAttendanceSchedule(attendanceDTO);

        Attendance attendance = new Attendance();
        mapDTOToEntity(attendanceDTO, attendance);
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapEntityToDTO(savedAttendance);
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        mapDTOToEntity(attendanceDTO, attendance);
        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return mapEntityToDTO(updatedAttendance);
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public AttendanceDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        return mapEntityToDTO(attendance);
    }

    @Override
    public List<AttendanceDTO> getAttendanceByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceByCourseId(Long courseId) {
        return attendanceRepository.findByCourseId(courseId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceByStudentIdAndCourseId(Long studentId, Long courseId) {
        return attendanceRepository.findByStudentIdAndCourseId(studentId, courseId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceByDateBetween(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public double getAttendancePercentage(Long studentId, Long courseId) {
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (attendances.isEmpty()) {
            return 0.0;
        }
        long presentCount = attendances.stream()
                .filter(Attendance::getPresent)
                .count();
        return (double) presentCount / attendances.size() * 100;
    }

    @Override
    public List<AttendanceDTO> getRecentAttendanceByTeacher(String username) {
        return attendanceRepository.findByCourseTeacherUsername(username).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByCourseStudentId(studentId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceBySemester(Integer semester) {
        return attendanceRepository.findByCourseSemester(semester).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, LocalTime> getAttendanceTimeWindow(Long courseId, LocalDate date) {
        List<CourseSchedule> schedules = courseScheduleRepository.findByCourseId(courseId);
        
        if (schedules.isEmpty()) {
            return null;
        }

        // Find schedule for the given date
        Optional<CourseSchedule> matchingSchedule = schedules.stream()
                .filter(schedule -> schedule.getDayOfWeek() == date.getDayOfWeek())
                .findFirst();

        if (matchingSchedule.isEmpty()) {
            return null;
        }

        CourseSchedule schedule = matchingSchedule.get();
        
        // Calculate the attendance window
        LocalTime windowStart = schedule.getStartTime().minusMinutes(15);
        LocalTime windowEnd = schedule.getEndTime().plusMinutes(30);

        Map<String, LocalTime> timeWindow = new HashMap<>();
        timeWindow.put("startTime", windowStart);
        timeWindow.put("endTime", windowEnd);
        timeWindow.put("classStartTime", schedule.getStartTime());
        timeWindow.put("classEndTime", schedule.getEndTime());

        return timeWindow;
    }

    private void validateAttendanceSchedule(AttendanceDTO attendanceDTO) {
        DayOfWeek dayOfWeek = attendanceDTO.getDate().getDayOfWeek();
        LocalTime currentTime = LocalTime.now();

        List<CourseSchedule> schedules = courseScheduleRepository.findByCourseId(attendanceDTO.getCourseId());
        
        if (schedules.isEmpty()) {
            throw new RuntimeException("No schedule found for this course. Please set up a course schedule first.");
        }

        // Find a matching schedule for the attendance date
        CourseSchedule matchingSchedule = schedules.stream()
                .filter(schedule -> schedule.getDayOfWeek() == dayOfWeek)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                    String.format("No class scheduled for %s", dayOfWeek.toString())
                ));

        // Allow marking attendance within 15 minutes before class starts and up to 30 minutes after class ends
        LocalTime adjustedStartTime = matchingSchedule.getStartTime().minusMinutes(15);
        LocalTime adjustedEndTime = matchingSchedule.getEndTime().plusMinutes(30);

        // For attendance marked on the same day
        if (attendanceDTO.getDate().equals(LocalDate.now())) {
            if (currentTime.isBefore(adjustedStartTime)) {
                throw new RuntimeException(
                    String.format("Too early to mark attendance. Class starts at %s (you can mark attendance from %s)",
                        matchingSchedule.getStartTime(),
                        adjustedStartTime)
                );
            }
            if (currentTime.isAfter(adjustedEndTime)) {
                throw new RuntimeException(
                    String.format("Too late to mark attendance. Class ended at %s (attendance window closed at %s)",
                        matchingSchedule.getEndTime(),
                        adjustedEndTime)
                );
            }
        } 
        // For past dates, just verify it was a scheduled class day
        else if (attendanceDTO.getDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Cannot mark attendance for future dates");
        }
    }

    private void mapDTOToEntity(AttendanceDTO dto, Attendance entity) {
        BeanUtils.copyProperties(dto, entity, "id", "studentId", "courseId", "markedByTeacherId");

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        entity.setStudent(student);

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        entity.setCourse(course);

        Teacher teacher = teacherRepository.findById(dto.getMarkedByTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        entity.setMarkedByTeacher(teacher);

        CourseSchedule schedule = courseScheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Course Schedule not found"));
        entity.setSchedule(schedule);
    }

    private AttendanceDTO mapEntityToDTO(Attendance entity) {
        AttendanceDTO dto = new AttendanceDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setStudentId(entity.getStudent().getId());
        dto.setCourseId(entity.getCourse().getId());
        dto.setMarkedByTeacherId(entity.getMarkedByTeacher().getId());

        // Set additional display fields
        dto.setStudentName(entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName());
        dto.setCourseName(entity.getCourse().getName());
        dto.setTeacherName(entity.getMarkedByTeacher().getFirstName() + " " + entity.getMarkedByTeacher().getLastName());
        dto.setScheduleId(entity.getSchedule() == null ? null : entity.getSchedule().getId());
        dto.setScheduleInfo(entity.getSchedule() == null? null : entity.getSchedule().getDayOfWeek()+": "+entity.getSchedule().getStartTime()+"-"+entity.getSchedule().getEndTime());
        return dto;
    }
}
