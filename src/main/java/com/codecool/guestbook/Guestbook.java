package com.codecool.guestbook;

import com.codecool.guestbook.DatabaseConnection.QueryHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Guestbook implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        String table = getTable();

        if (method.equals("GET")) {
            response = "<html><body>" +
                    table +
                    "<br><form method=\"POST\">\n" +
                    "Name:<br>\n" +
                    "<input type=\"text\" name=\"name\">\n" +
                    "<br>\n" +
                    "Message:<br>\n" +
                    "<input type=\"text\" name=\"message\">\n" +
                    "<br><br>\n" +
                    "<input type=\"submit\" value=\"Submit\">\n" +
                    "</form> " +
                    "</body></html>";
        }

        if (method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();

            System.out.println(formData);
            Map inputs = parseFormData(formData);
            String name = inputs.get("name").toString();
            String message = inputs.get("message").toString();
            insertToTable(name, message);

            response = "Thank you! Your entry has been saved.";

        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

    private String getTable() {
        StringBuilder builder = new StringBuilder();
        ResultSet rs = QueryHandler.getInstance().executeQuery("SELECT * FROM guestbook;");
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String message = rs.getString("message");
                String date = rs.getString("date");
                builder.append("<b>" + message + "</b><br>");
                builder.append("Name: " + "<b>" + name + "</b><br>");
                builder.append("Date: " + date + "<br>");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private void insertToTable(String name, String message) {
        String time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        try {
            Connection c = QueryHandler.getInstance().getConnection();
            String query = "INSERT INTO guestbook (date, name, message) " +
                    "VALUES (?, ?, ?);";
            PreparedStatement statement = c.prepareStatement(query);

            statement.setString(1, time);
            statement.setString(2, name);
            statement.setString(3, message);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
