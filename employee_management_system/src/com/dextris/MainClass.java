package com.dextris;

import java.util.List;
import java.util.Scanner;

import com.dextris.beans.Employee;
import com.dextris.service.EmployeeService;
import com.dextris.service.EmployeeServiceImplementation;

public class MainClass {
	private static Scanner sc;

	public static void main(String[] args) {
		sc = new Scanner(System.in);
		EmployeeService employeeService = new EmployeeServiceImplementation();
		System.out.println("******EMS**********");
		
	
		while (true) {
			System.out.println("1. Register.\n2. Login.\n3. Search.\n4. Show All Employees.\n5. Logout.");
			int choice = sc.nextInt();

			switch (choice) {
			case 1:
				Employee employee = new Employee();
				System.out.println("Enter Employee Name");
				employee.setEmployeeName(sc.next());
				System.out.println("Enter Employee DOB");
				employee.setDateOfBirth(sc.next());
				System.out.println("Enter Employee Email");
				employee.setEmail(sc.next());
				System.out.println("Enter Employee Password");
				employee.setPassword(sc.next());
				System.out.println("Enter Employee Mobile Number");
				employee.setMobileNumber(sc.nextLong());
				System.out.println(employeeService.addEmployee(employee) ? "Registration Success": "Failed to Register");

				break;

			case 2:
				System.out.println("Enter Employee Email");
				String email = sc.next();

				System.out.println("Enter Employee Password");
				String password = sc.next();
				System.out.println(
						employeeService.employeeLogin(email, password) ? "Login Success" : "Invalid Credentials");
				break;

			case 3:
				System.out.println("a. Search By ID.\nb. Search by email");
				char options = sc.next().charAt(0);
				if (options == 'a') {
					System.out.println("Enter ID");
					Long id = sc.nextLong();
					System.out.println(employeeService.search(id));
				}

				else if (options == 'b') {
					System.out.println("Enter Email ID");
					Long id = sc.nextLong();
					System.out.println(employeeService.search(id));
				} else {
					throw new IllegalArgumentException("Unexpected value: " + options);
				}

				break;

			case 4:
				List<Employee> employees = employeeService.getAllEmployee();
				for (Employee e : employees) {
					System.out.println(e);
				}

				break;

			case 5:
				System.out.println("Enter ID to Logout");
				Long id = sc.nextLong();
				System.out.println(employeeService.employeeLogout(id) ? "Logout Success" : "Please Login to perform Logout Operation");
				break;

			default:
				throw new IllegalArgumentException("Unexpected value: " + choice);
			}
		}
	}
}
