package com.mb.tutorials.student;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Student {
	
	private String studentName;
	private List<Subject> subjects;

}
