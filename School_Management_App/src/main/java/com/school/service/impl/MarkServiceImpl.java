package com.school.service.impl;

import com.school.dto.MarkDTO;
import com.school.entity.Mark;
import com.school.entity.Course;
import com.school.entity.Student;
import com.school.entity.Teacher;
import com.school.entity.ExamType;
import com.school.repository.MarkRepository;
import com.school.repository.CourseRepository;
import com.school.repository.StudentRepository;
import com.school.repository.TeacherRepository;
import com.school.repository.ExamTypeRepository;
import com.school.service.MarkService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ExamTypeRepository examTypeRepository;

    public MarkServiceImpl(
            MarkRepository markRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            TeacherRepository teacherRepository,
            ExamTypeRepository examTypeRepository) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.examTypeRepository = examTypeRepository;
    }

    @Override
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
        return mapEntityToDTO(savedMark);
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
}
