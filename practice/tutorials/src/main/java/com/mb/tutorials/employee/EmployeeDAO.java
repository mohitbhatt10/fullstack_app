package com.mb.tutorials.employee;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;

public class EmployeeDAO {

	public static List<Employee> getEmployees(int numberOfEmployees) {

		List<Employee> employees = new ArrayList<>();
		Faker faker = new Faker();

		for (int i = 0; i < numberOfEmployees; i++) {
			int id = i + 1;
			String name = faker.name().fullName();
			int age = faker.number().numberBetween(18, 60);
			String gender = faker.bool().bool()? "Male" : "Female";
			double salary = faker.number().randomDouble(2, 30000, 120000);
			Employee employee = new Employee(id, name, age, salary,gender);
			employees.add(employee);
		}
		return employees;

	}

}
