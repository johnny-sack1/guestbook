package com.codecool.guestbook.DatabaseConnection;

import java.sql.DriverManager;
import java.sql.Connection;


public class SQLiteJDBC {

    private Connection connection;

    public SQLiteJDBC () {
        Connection c;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:guestbook.db");
            connection = c;
            System.out.println("success");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    Connection getConnection() {
        return connection;
    }
}
