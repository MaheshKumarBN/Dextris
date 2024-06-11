package com.dextris.exception;

public class EmployeeNotLoggedInException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmployeeNotLoggedInException(String message) {
		super(message);
	}
}
