package com.dextris.service;

import java.util.List;

import com.dextris.beans.Employee;

public interface EmployeeService {
	
	List<Employee> getAllEmployee();

	Boolean employeeLogin(String email, String password);

	Boolean addEmployee(Employee employee);

	Employee search(Long id);

	Employee search(String email);
	
	Boolean employeeLogout(Long id);
}
