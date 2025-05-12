package com.uscmarketplace;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Value("${spring.datasource.url}")
    private String db;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private static class UserDto {
        public long   id;
        public String username;
        public String role;
        public UserDto(long i, String u, String r){ id = i; username = u; role = r; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();  // e.g. "/2"
        if (path == null || path.length() < 2) {
            resp.sendError(400, "Missing userId");
            return;
        }
        long userId = Long.parseLong(path.substring(1));

        resp.setContentType("application/json");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(db, dbUsername, dbPassword);
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT SID, username, role FROM Users WHERE SID = ?"
                 )) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        resp.sendError(404, "User not found");
                        return;
                    }
                    UserDto dto = new UserDto(
                        rs.getLong("SID"),
                        rs.getString("username"),
                        rs.getString("role").toUpperCase()
                    );
                    new ObjectMapper()
                      .writeValue(resp.getOutputStream(), dto);
                }
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        } catch (ClassNotFoundException e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
