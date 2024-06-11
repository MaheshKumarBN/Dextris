package com.dextris.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.dextris.ConnectionSingleton;
import com.dextris.beans.Employee;
import com.dextris.exception.EmployeeNotFoundException;
import com.dextris.exception.EmployeeNotLoggedInException;
import com.dextris.exception.InvalidCredentialsException;

public class EmployeeServiceImplementation implements EmployeeService {
	private static Connection connection = ConnectionSingleton.getConnectionInstance();
	private static final String SELECT_QUERY = "SELECT * FROM EMPLOYEE_DB";
	private static final String SEARCH_BY_ID_QUERY = "SELECT * FROM EMPLOYEE_DB WHERE EMPLOYEE_ID = ?";
	private static final String SEARCH_BY_EMAIL_QUERY = "SELECT * FROM EMPLOYEE_DB WHERE EMPLOYEE_NAME = ?";
	private static final String SEARCH_BY_EMAIL_AND_PASSWORD_QUERY = "SELECT * FROM EMPLOYEE_DB WHERE EMPLOYEE_EMAIL = ? AND EMPLOYEE_PASSWORD = ?";
	private static final String UPDATE_STATUS_QUERY_AND_LOGIN_TIME = "UPDATE EMPLOYEE_DB SET STATUS = ?, LOGIN_TIME = ? WHERE EMPLOYEE_EMAIL = ?";
	private static final String REGISTER_EMPLOYEE_QUERY = "INSERT INTO EMPLOYEE_DB (EMPLOYEE_NAME, DATE_OF_BIRTH, EMPLOYEE_EMAIL, EMPLOYEE_PASSWORD, MOBILE_NUMBER, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_STATUS_QUERY_AND_LOGOUT_TIME = "UPDATE EMPLOYEE_DB SET STATUS = ?, LOGOUT_TIME = ? WHERE EMPLOYEE_ID = ?";

	@Override
	public List<Employee> getAllEmployee() {
		List<Employee> employees = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				Employee employee = new Employee();
				employee.setEmployeeId(rs.getLong("employee_id"));
				employee.setEmployeeName(rs.getString("employee_name"));
				employee.setDateOfBirth(changeDateFormat2(rs.getString("date_of_birth")));
				employee.setEmail(rs.getString("employee_email"));
//				employee.setStatus(rs.getBoolean("status"));
				employee.setLoginTime(rs.getString("login_time"));
				employee.setLogoutTime(rs.getString("logout_time"));
//				employee.setPassword(rs.getString("employee_password"));
				employees.add(employee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employees;
	}

	@Override
	public Boolean employeeLogin(String email, String password) {
		try {
			connection.setAutoCommit(false);
			PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_BY_EMAIL_AND_PASSWORD_QUERY);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);

			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				PreparedStatement preparedStatement1 = connection.prepareStatement(UPDATE_STATUS_QUERY_AND_LOGIN_TIME);
				preparedStatement1.setBoolean(1, true);
				LocalTime loginTime = LocalTime.now();
				preparedStatement1.setString(2,
						loginTime.getHour() + ":" + loginTime.getMinute() + ":" + loginTime.getSecond());
				preparedStatement1.setString(3, email);
				int rowsAffected = preparedStatement1.executeUpdate();
				if (rowsAffected == 1) {
					connection.setAutoCommit(true);
					return true;
				}
			} else {
				throw new InvalidCredentialsException("Invalid Crendials");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Boolean addEmployee(Employee employee) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(REGISTER_EMPLOYEE_QUERY);

			preparedStatement.setString(1, employee.getEmployeeName());
			preparedStatement.setString(2, changeDateFormat1(employee.getDateOfBirth()));
			preparedStatement.setString(3, employee.getEmail());
			preparedStatement.setString(4, employee.getPassword());
			preparedStatement.setLong(5, employee.getMobileNumber());
			preparedStatement.setBoolean(6, false);

			int isInserted = preparedStatement.executeUpdate();
			if (isInserted == 1) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Employee search(Long id) {
		PreparedStatement preparedStatement;
		Employee employee = new Employee();
		try {
			preparedStatement = connection.prepareStatement(SEARCH_BY_ID_QUERY);
			preparedStatement.setLong(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				employee.setEmployeeName(rs.getString("employee_name"));
				employee.setEmail(rs.getString("employee_email"));
				employee.setStatus(rs.getBoolean("status"));
				employee.setLoginTime(rs.getString("login_time"));
				employee.setLogoutTime(rs.getString("logout_time"));
			}
		} catch (SQLException e) {
			throw new EmployeeNotFoundException("Employee not found with an id:" + id);
		}
		return employee;
	}

	@Override
	public Employee search(String email) {
		PreparedStatement preparedStatement;
		Employee employee = new Employee();
		try {
			preparedStatement = connection.prepareStatement(SEARCH_BY_EMAIL_QUERY);
			preparedStatement.setString(1, email);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs != null) {
				employee.setEmployeeName(rs.getString("employee_name"));
				employee.setEmail(rs.getString("employee_email"));
				employee.setStatus(rs.getBoolean("status"));
				employee.setLoginTime(rs.getString("login_time"));
				employee.setLogoutTime(rs.getString("logout_time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public Boolean employeeLogout(Long id) {

		Employee employee = search(id);
		LocalTime logoutTime = LocalTime.now();

		if (employee != null && employee.getStatus()) {
			try {
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STATUS_QUERY_AND_LOGOUT_TIME);
				preparedStatement.setBoolean(1, false);
				preparedStatement.setString(2,
						logoutTime.getHour() + ":" + logoutTime.getMinute() + ":" + logoutTime.getSecond());
				preparedStatement.setLong(3, id);
				int rowsUpdated = preparedStatement.executeUpdate();
				if (rowsUpdated == 1) {
					System.out.println("Employee logged out successfully");
					return true;
				}
			} catch (SQLException e) {
				System.err.println("Error during logout: " + e.getMessage());
			}
		} else {
			if (employee != null) {
				throw new EmployeeNotLoggedInException("Employee Not Logged in");
			}
		}
		throw new EmployeeNotFoundException("Employee not found with an id:" + id);
	}

	public static String changeDateFormat1(String dateString) {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}

		try {
			SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat newFormat = new SimpleDateFormat("dd-MMM-yyyy");
			return newFormat.format(oldFormat.parse(dateString));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid input date format. Expected: dd/MM/yyyy");
		}
	}

	public static String changeDateFormat2(String dateString) {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}

		try {
			SimpleDateFormat oldFormat = new SimpleDateFormat("dd-MMM-yyyy");
			SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy");
			return newFormat.format(oldFormat.parse(dateString));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid input date format. Expected: dd-MMM-yyyy");
		}
	}

}
