package com.mb.tutorials.employee;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeDetailsProcessor {

	private EmployeeDetailsProcessor() {

	}

	public static Employee getNthMaxSalariedEmployees(List<Employee> employees, int n) {

		return employees.stream().sorted(Comparator.comparingDouble(Employee::getSalary).reversed()).skip(n - 1)
				.findFirst().get();

	}

	public static List<Employee> getEmployeesHavingSalaryGraterThan(List<Employee> employees, double salaryLimit) {

		return employees.stream().filter(emp -> emp.getSalary() >= salaryLimit).collect(Collectors.toList());

	}

	public static Map<String, Optional<Employee>> getHighestSalaryEmployeesBasedonGender(List<Employee> employees) {

		return employees.stream().collect(Collectors.groupingBy(Employee::getGender,
				Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))));

	}

	public static List<Employee> getHighestSalaryEmployeesBasedonGenderAsList(List<Employee> employees) {

		return employees.stream()
				.collect(Collectors.groupingBy(Employee::getGender,
						Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))))
				.entrySet().stream().map(entry -> entry.getValue().get()).collect(Collectors.toList());

	}

}
