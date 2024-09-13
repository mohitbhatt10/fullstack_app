package com.mb.tutorials.employee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Employee {
	
	private Integer id;
	private String name;
	private Integer age;
	private Double salary;
	private String gender;

}
