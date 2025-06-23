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

import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.GrayColor;

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
    public List<CourseScheduleDTO> getSchedulesByClassroom(String classroom) {
        return courseScheduleRepository.findByClassroom(classroom).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void exportScheduleToPdf(Long courseId) {
        List<CourseSchedule> schedules = courseScheduleRepository.findByCourseId(courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream("schedule_" + courseId + ".pdf"));
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Course Schedule: " + course.getName() + " (" + course.getCode() + ")", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Create schedule table
            PdfPTable table = new PdfPTable(4); // 4 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add headers
            Stream.of("Day", "Time", "Classroom", "Teacher")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(new GrayColor(0.8f));
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPadding(5f);
                        table.addCell(header);
                    });

            // Add schedule data
            for (CourseSchedule schedule : schedules) {
                // Day
                table.addCell(schedule.getDayOfWeek().toString());
                
                // Time
                String time = String.format("%s - %s",
                        schedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        schedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                table.addCell(time);
                
                // Classroom
                table.addCell(schedule.getClassroom());
                
                // Teacher(s)
                String teachers = schedule.getCourse().getTeachers().stream()
                        .map(teacher -> teacher.getFirstName() + " " + teacher.getLastName())
                        .collect(Collectors.joining(", "));
                table.addCell(teachers);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export schedule to PDF: " + e.getMessage());
        }
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
        
        if (entity.getCourse().getTeachers() != null && !entity.getCourse().getTeachers().isEmpty()) {
            String teacherNames = entity.getCourse().getTeachers().stream()
                    .map(teacher -> teacher.getFirstName() + " " + teacher.getLastName())
                    .collect(Collectors.joining(", "));
            dto.setTeacherName(teacherNames);
        } else {
            dto.setTeacherName("No teachers assigned");
        }
        
        return dto;
    }
}
