package com.school.service.impl;

import com.school.dto.MarkDTO;
import com.school.dto.MarksSummaryDTO;
import com.school.entity.*;
import com.school.repository.*;
import com.school.service.MarkService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ExamTypeRepository examTypeRepository;
    private final MarksSummaryRepository marksSummaryRepository;

    public MarkServiceImpl(
            MarkRepository markRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            TeacherRepository teacherRepository,
            ExamTypeRepository examTypeRepository,
            MarksSummaryRepository marksSummaryRepository) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.examTypeRepository = examTypeRepository;
        this.marksSummaryRepository = marksSummaryRepository;
    }

    @Override
    @Transactional
    public MarkDTO createMark(MarkDTO markDTO) {
        Mark mark = new Mark();
        Course course = courseRepository.findById(markDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(markDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Teacher teacher = teacherRepository.findById(markDTO.getEnteredByTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        ExamType examType = examTypeRepository.findById(markDTO.getExamTypeId())
                .orElseThrow(() -> new RuntimeException("Exam type not found"));
        
        mark.setCourse(course);
        mark.setStudent(student);
        mark.setEnteredByTeacher(teacher);
        mark.setSemester(markDTO.getSemester());
        mark.setExamType(examType);
        mark.setMarks(markDTO.getMarks());
        mark.setMaxMarks(markDTO.getMaxMarks());
        mark.setRemarks(markDTO.getRemarks());
        
        Mark savedMark = markRepository.save(mark);
        
        // Update marks summary
        updateMarksSummary(course, examType, teacher);
        
        return mapEntityToDTO(savedMark);
    }

    private void updateMarksSummary(Course course, ExamType examType, Teacher teacher) {
        // Get all marks for this course and exam type
        List<Mark> marks = markRepository.findByCourseIdAndExamTypeId(course.getId(), examType.getId());
        
        if (marks.isEmpty()) {
            return;
        }

        // Calculate statistics
        DoubleSummaryStatistics stats = marks.stream()
                .mapToDouble(Mark::getMarks)
                .summaryStatistics();

        double maxMarks = marks.get(0).getMaxMarks(); // All marks should have same max marks
        long passedCount = marks.stream()
                .filter(m -> m.getMarks() >= (maxMarks * 0.4)) // Assuming 40% is pass marks
                .count();

        // Find existing summary or create new one
        MarksSummary summary = marksSummaryRepository
                .findByCourseAndExamType(course.getId(), examType.getId())
                .orElse(MarksSummary.builder()
                        .course(course)
                        .examType(examType)
                        .teacher(teacher)
                        .build());

        // Update summary
        summary.setMaxMarks(maxMarks);
        summary.setAverageMarks(stats.getAverage());
        summary.setTotalStudents((int) stats.getCount());
        summary.setPassedStudents((int) passedCount);
        summary.setPassPercentage((double) passedCount / stats.getCount() * 100);
        summary.setHighestMarks(stats.getMax());
        summary.setLowestMarks(stats.getMin());

        // Save summary
        marksSummaryRepository.save(summary);
    }

    @Override
    public MarkDTO updateMark(Long id, MarkDTO markDTO) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found"));
        
        Course course = courseRepository.findById(markDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(markDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Teacher teacher = teacherRepository.findById(markDTO.getEnteredByTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        ExamType examType = examTypeRepository.findById(markDTO.getExamTypeId())
                .orElseThrow(() -> new RuntimeException("Exam type not found"));
        
        mark.setCourse(course);
        mark.setStudent(student);
        mark.setEnteredByTeacher(teacher);
        mark.setSemester(markDTO.getSemester());
        mark.setExamType(examType);
        mark.setMarks(markDTO.getMarks());
        mark.setMaxMarks(markDTO.getMaxMarks());
        mark.setRemarks(markDTO.getRemarks());
        
        Mark updatedMark = markRepository.save(mark);
        return mapEntityToDTO(updatedMark);
    }

    @Override
    public void deleteMark(Long id) {
        markRepository.deleteById(id);
    }

    @Override
    public MarkDTO getMarkById(Long id) {
        return markRepository.findById(id)
                .map(this::mapEntityToDTO)
                .orElseThrow(() -> new RuntimeException("Mark not found"));
    }

    @Override
    public List<MarkDTO> getAllMarks() {
        return markRepository.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getMarksByStudentId(Long studentId) {
        return markRepository.findByStudentId(studentId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getMarksByCourseId(Long courseId) {
        return markRepository.findByCourseId(courseId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getMarksByStudentIdAndSemester(Long studentId, Integer semester) {
        return markRepository.findByStudentIdAndSemester(studentId, semester).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getMarksByStudentIdAndCourseId(Long studentId, Long courseId) {
        return markRepository.findByStudentIdAndCourseId(studentId, courseId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getMarksByExamTypeId(Long examTypeId) {
        return markRepository.findByExamTypeId(examTypeId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getMarksByExamTypeName(String examTypeName) {
        return markRepository.findByExamTypeName(examTypeName).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public double calculateStudentAverage(Long studentId, Integer semester) {
        List<Mark> marks = markRepository.findByStudentIdAndSemester(studentId, semester);
        if (marks.isEmpty()) {
            return 0.0;
        }

        double totalPercentage = marks.stream()
                .mapToDouble(mark -> (mark.getMarks() / mark.getMaxMarks()) * 100)
                .sum();
        
        return totalPercentage / marks.size();
    }

    @Override
    public List<MarkDTO> getMarksByStudent(Long studentId) {
        return markRepository.findByStudentId(studentId).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getMarksBySemester(Integer semester) {
        return markRepository.findBySemester(semester).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkDTO> getRecentMarksByTeacher(String username) {
        return markRepository.findByEnteredByTeacherUsername(username).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarksSummaryDTO> getMarksSummaryByCourseId(Long courseId) {
        return marksSummaryRepository.findByCourseId(courseId).stream()
                .map(this::mapSummaryToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarksSummaryDTO> getMarksSummaryByTeacher(String username) {
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return marksSummaryRepository.findByTeacherId(teacher.getId())
                .stream()
                .map(this::mapSummaryToDTO)
                .collect(Collectors.toList());
    }

    private MarkDTO mapEntityToDTO(Mark mark) {
        MarkDTO dto = new MarkDTO();
        BeanUtils.copyProperties(mark, dto);
        
        if (mark.getCourse() != null) {
            dto.setCourseId(mark.getCourse().getId());
            dto.setCourseName(mark.getCourse().getName());
        }
        if (mark.getStudent() != null) {
            dto.setStudentId(mark.getStudent().getId());
            dto.setStudentName(mark.getStudent().getFirstName() + " " + mark.getStudent().getLastName());
        }
        if (mark.getEnteredByTeacher() != null) {
            dto.setEnteredByTeacherId(mark.getEnteredByTeacher().getId());
            dto.setTeacherName(mark.getEnteredByTeacher().getFirstName() + " " + mark.getEnteredByTeacher().getLastName());
        }
        if (mark.getExamType() != null) {
            dto.setExamTypeId(mark.getExamType().getId());
            dto.setExamTypeName(mark.getExamType().getName());
        }
        
        return dto;
    }

    public MarksSummaryDTO mapSummaryToDTO(MarksSummary summary) {
        if (summary == null) {
            return null;
        }

        return MarksSummaryDTO.builder()
                .id(summary.getId())
                .courseId(summary.getCourse().getId())
                .courseName(summary.getCourse().getName())
                .courseCode(summary.getCourse().getCode())
                .examTypeId(summary.getExamType().getId())
                .examTypeName(summary.getExamType().getName())
                .teacherId(summary.getTeacher().getId())
                .teacherName(summary.getTeacher().getFirstName() + " " + summary.getTeacher().getLastName())
                .maxMarks(summary.getMaxMarks())
                .averageMarks(summary.getAverageMarks())
                .totalStudents(summary.getTotalStudents())
                .passedStudents(summary.getPassedStudents())
                .passPercentage(summary.getPassPercentage())
                .highestMarks(summary.getHighestMarks())
                .lowestMarks(summary.getLowestMarks())
                .createdDate(summary.getCreatedDate())
                .updatedDate(summary.getUpdatedDate())
                .build();
    }
}
