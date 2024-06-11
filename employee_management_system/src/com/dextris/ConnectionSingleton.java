package com.dextris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to create only one connection for the entire application
 * instead of creating a connection object for each operation. This class
 * ensures thread safety and proper resource management.
 * 
 * @author Mahesh
 * @version 1.0
 */
public class ConnectionSingleton {
	private static Connection connection;

	/**
	 * Gets the singleton instance of the database connection.
	 * 
	 * @return The singleton instance of the Connection.
	 * @throws SQLException           if a database access error occurs.
	 * @throws ClassNotFoundException if the MySQL JDBC Driver class is not found.
	 */
	public static Connection getConnectionInstance() {
		if (connection == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connectionObject = DriverManager.getConnection("jdbc:mysql://localhost:3306/dextris_schema",
						"root", "root");
				connection = connectionObject;
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}

		}
		return connection;
	}
}
