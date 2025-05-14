package com.example.javaproject.Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Myconnection {
    private static final String URL = "jdbc:mysql://localhost:3306/govibe";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection cnx;
    private static Myconnection instance;

    private Myconnection() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public static Myconnection getInstance() {
        if (instance == null) {
            instance = new Myconnection();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.out.println("Reconnecting to database failed: " + e.getMessage());
        }
        return cnx;
    }
}