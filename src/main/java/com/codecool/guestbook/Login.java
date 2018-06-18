package com.codecool.guestbook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Login implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        HttpCookie cookie = null;

        if (cookieStr != null) {
            response = "<html><body>" +
                    "You are logged in.<br>" +
                    "<button type=\"button\" onclick=\"document.cookie = 'loggedin' + '=; Max-Age=0';location.href=location.href\">Log out</button>\n" +
                    "</body></html>";
        }
        else {
            if (method.equals("GET")) {
                response = "<html><body>" +
                        "<br><form method=\"POST\">\n" +
                        "Username:<br>\n" +
                        "<input type=\"text\" name=\"username\">\n" +
                        "<br>\n" +
                        "Password:<br>\n" +
                        "<input type=\"password\" name=\"password\">\n" +
                        "<br><br>\n" +
                        "<input type=\"submit\" value=\"Log in\">\n" +
                        "</form> " +
                        "</body></html>";
            }

            if (method.equals("POST")) {
                InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String loginData = br.readLine();

                Map inputs = parseFormData(loginData);
                String username = inputs.get("username").toString();
                String password = inputs.get("password").toString();

                if ((username.equals("a") && password.equals("a")) || (username.equals("b") && password.equals("b"))) {
                    cookie = new HttpCookie("loggedin", "true");
                    httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
                    response = "<html><body>" +
                            "You are logged in.<br>" +
                            "<button type=\"button\" onclick=\"document.cookie = 'loggedin' + '=; Max-Age=0';location.href=location.href\">Log out</button>\n" +
                            "</body></html>";
                } else {
                    response = "Wrong login or password!";
                }
            }
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
}
