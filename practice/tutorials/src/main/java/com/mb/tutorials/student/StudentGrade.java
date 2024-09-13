package com.mb.tutorials.student;


import java.util.Map;

class StudentGrade {
	private String name;
	private Map<String,Integer> grades;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Integer> getGrades() {
		return grades;
	}

	public void setGrades(Map<String, Integer> grades) {
		this.grades = grades;
	}

	StudentGrade(String name, Map<String,Integer> grades){
		this.name = name;
		this.grades = grades;
	}
}

