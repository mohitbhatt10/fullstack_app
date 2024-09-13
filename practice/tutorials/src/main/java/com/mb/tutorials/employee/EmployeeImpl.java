package com.mb.tutorials.employee;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeImpl {
	
	public static void main(String[] args) {
		
		List<Employee> list = EmployeeDAO.getEmployees(10);
		Collections.sort(list, Comparator.comparing(Employee::getSalary).reversed());
		for(int i =0;i<10;i++) {
			System.out.println(list.get(i));
		}
		System.out.println(5+"th Max Salaried Employee: "+EmployeeDetailsProcessor.getNthMaxSalariedEmployees(list, 5));
		Map<String, Optional<Employee>> highestSalaryEmployeesBasedonGender = EmployeeDetailsProcessor.getHighestSalaryEmployeesBasedonGender(list);

		highestSalaryEmployeesBasedonGender.forEach((key,value) ->
		
			System.out.println("Highest Salary for "+key+" is "+value.get())
		);
		
		System.out.println(EmployeeDetailsProcessor.getHighestSalaryEmployeesBasedonGenderAsList(list));
		
	}

}
