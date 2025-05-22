package com.school.service.impl;

import com.school.dto.CourseScheduleDTO;
import com.school.entity.Course;
import com.school.entity.CourseSchedule;
import com.school.repository.CourseRepository;
import com.school.repository.CourseScheduleRepository;
import com.school.service.CourseScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseScheduleServiceImpl implements CourseScheduleService {

    private final CourseScheduleRepository courseScheduleRepository;
    private final CourseRepository courseRepository;

    public CourseScheduleServiceImpl(CourseScheduleRepository courseScheduleRepository,
                                   CourseRepository courseRepository) {
        this.courseScheduleRepository = courseScheduleRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseScheduleDTO createSchedule(CourseScheduleDTO scheduleDTO) {
        validateScheduleTime(scheduleDTO);
        
        CourseSchedule schedule = new CourseSchedule();
        mapDTOToEntity(scheduleDTO, schedule);
        CourseSchedule savedSchedule = courseScheduleRepository.save(schedule);
        return mapEntityToDTO(savedSchedule);
    }

    @Override
    public CourseScheduleDTO updateSchedule(Long id, CourseScheduleDTO scheduleDTO) {
        CourseSchedule schedule = courseScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
                
        validateScheduleTime(scheduleDTO);
        
        mapDTOToEntity(scheduleDTO, schedule);
        CourseSchedule updatedSchedule = courseScheduleRepository.save(schedule);
        return mapEntityToDTO(updatedSchedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        courseScheduleRepository.deleteById(id);
    }

    @Override
    public CourseScheduleDTO getScheduleById(Long id) {
        CourseSchedule schedule = courseScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return mapEntityToDTO(schedule);
    }

    @Override
    public List<CourseScheduleDTO> getSchedulesByCourseId(Long courseId) {
        return courseScheduleRepository.findByCourseId(courseId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseScheduleDTO> getSchedulesByTeacherForDay(Long teacherId, DayOfWeek dayOfWeek) {
        return courseScheduleRepository.findByDayOfWeekAndTeacherId(dayOfWeek, teacherId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isScheduleOverlapping(Long courseId, DayOfWeek dayOfWeek, 
                                       LocalTime startTime, LocalTime endTime) {
        List<CourseSchedule> overlappingSchedules = courseScheduleRepository
                .findOverlappingSchedules(courseId, dayOfWeek, startTime, endTime);
        return !overlappingSchedules.isEmpty();
    }

    private void validateScheduleTime(CourseScheduleDTO scheduleDTO) {
        if (scheduleDTO.getStartTime().isAfter(scheduleDTO.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }
        
        if (isScheduleOverlapping(scheduleDTO.getCourseId(), scheduleDTO.getDayOfWeek(),
                                scheduleDTO.getStartTime(), scheduleDTO.getEndTime())) {
            throw new RuntimeException("Schedule overlaps with existing schedule");
        }
    }

    private void mapDTOToEntity(CourseScheduleDTO dto, CourseSchedule entity) {
        BeanUtils.copyProperties(dto, entity, "id", "courseId");
        
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        entity.setCourse(course);
    }

    private CourseScheduleDTO mapEntityToDTO(CourseSchedule entity) {
        CourseScheduleDTO dto = new CourseScheduleDTO();
        BeanUtils.copyProperties(entity, dto);
        
        dto.setCourseId(entity.getCourse().getId());
        dto.setCourseName(entity.getCourse().getName());
        dto.setCourseCode(entity.getCourse().getCode());
        dto.setTeacherName(entity.getCourse().getTeacher().getFirstName() + " " + 
                          entity.getCourse().getTeacher().getLastName());
        
        return dto;
    }
}
