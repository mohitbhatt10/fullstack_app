package com.mb.tutorials.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Subject {
	
	private String subjectName;
	private Integer subjectMarks;

}
