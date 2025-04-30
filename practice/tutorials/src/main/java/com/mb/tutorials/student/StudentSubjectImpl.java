package com.mb.tutorials.student;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * This program calculates the student name with maximum marks in "Maths" Subject. 
 */


public class StudentSubjectImpl {

	public static void main(String[] args) {

		Student s1 = new Student("Ramesh",
				Arrays.asList(new Subject("Maths", 98), new Subject("Physics", 94), new Subject("Chemistry", 90)));
		Student s2 = new Student("Suresh",
				Arrays.asList(new Subject("Maths", 94), new Subject("Physics", 95), new Subject("Chemistry", 91)));
		Student s3 = new Student("Mahesh",
				Arrays.asList(new Subject("Maths", 96), new Subject("Physics", 90), new Subject("Chemistry", 92)));

		List<Student> students = Arrays.asList(s1, s2, s3);

		Comparator<Student> comparator = (student1, student2) -> {

			Integer student1MathsMarks = student1.getSubjects().stream()
					.filter(st -> st.getSubjectName().equals("Maths")).map(Subject::getSubjectMarks)
					.collect(Collectors.toList()).get(0);
			Integer student2MathsMarks = student2.getSubjects().stream()
					.filter(st -> st.getSubjectName().equals("Maths")).map(Subject::getSubjectMarks)
					.collect(Collectors.toList()).get(0);
			return student1MathsMarks.compareTo(student2MathsMarks);
		};

		String studentName = students.stream()
				.filter(stu -> stu.getSubjects().stream().anyMatch(sub -> sub.getSubjectName().equals("Maths")))
				.max(comparator).get().getStudentName();

		System.out.println(studentName);

	}

}
