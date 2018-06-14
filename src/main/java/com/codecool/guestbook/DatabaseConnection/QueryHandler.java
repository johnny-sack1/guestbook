package com.codecool.guestbook.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryHandler {

    private static QueryHandler ourInstance;
    private SQLiteJDBC connectionEstablisher;
    private Connection connection;

    public static QueryHandler getInstance() {
        if (ourInstance == null) {
            ourInstance = new QueryHandler();
        }
        return ourInstance;
    }

    private QueryHandler() {
        this.connectionEstablisher = new SQLiteJDBC();
        this.connection = connectionEstablisher.getConnection();
    }

    public ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            return null;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
