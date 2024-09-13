package com.mb.tutorials.student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentGradeImpl {

	public static void main(String[] args) {

		List<StudentGrade> studentGrades = new ArrayList<>();
		
        Map<String, Integer> val = new HashMap<>();
        StudentGrade student1 = new StudentGrade("Student1", val);
        val.put("Math", 90);
        val.put("Physics", 85);
        val.put("History", 78);
        
        Map<String, Integer> val2 = new HashMap<>();
        StudentGrade student2 = new StudentGrade("Student2", val2);
        
        val2.put("Math", 92);
        val2.put("Physics", 88);
        val2.put("History", 75);
        
        Map<String, Integer> val3 = new HashMap<>();
        StudentGrade student3 = new StudentGrade("Student3", val3);
        val3.put("Math", 85);
        val3.put("Physics", 95);
        val3.put("History", 80);
        studentGrades.add(student1);
        studentGrades.add(student2);       
        studentGrades.add(student3);
        
        //studentGrades.stream()
        //.flatMap(student -> student.getGrades().entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue)));
        
        // Aggregate the grades for each subject
        Map<String, Double> averageGrades = studentGrades.stream()
                .flatMap(student -> student.getGrades().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.averagingDouble(Map.Entry::getValue)
                ));

        // Identify the subject with the highest average grade
        Optional<Map.Entry<String, Double>> topSubject = averageGrades.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        // Print average grades for each subject
        averageGrades.forEach((subject, average) -> 
                System.out.println("Average grade for " + subject + ": " + average));

        // Print the subject with the highest average grade
        topSubject.ifPresent(subject -> 
                System.out.println("Subject with the highest average grade: " + subject.getKey() 
                                   + " with average grade " + subject.getValue()));
        
	}

}
