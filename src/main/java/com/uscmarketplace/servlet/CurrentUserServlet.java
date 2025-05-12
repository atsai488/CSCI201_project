package com.uscmarketplace.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@WebServlet("/api/users/me")
public class CurrentUserServlet extends HttpServlet {
  @Value("${spring.datasource.url}")      private String db;
  @Value("${spring.datasource.username}") private String dbUsername;
  @Value("${spring.datasource.password}") private String dbPassword;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("application/json");

    HttpSession session = req.getSession(false);
    if (session == null) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      resp.getWriter().write("{\"error\":\"not logged in\"}");
      return;
    }

    // 1) Prefer a stored userId in session
    Object idAttr = session.getAttribute("userId");
    Long   userId = null;
    if (idAttr instanceof Long) {
      userId = (Long) idAttr;
    } else if (idAttr instanceof Integer) {
      userId = ((Integer) idAttr).longValue();
    }

    // 2) If no userId, maybe you stored email instead
    String email = null;
    if (userId == null) {
      email = (String) session.getAttribute("email");
      if (email == null) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write("{\"error\":\"not logged in\"}");
        return;
      }
    }

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      try (Connection conn = DriverManager.getConnection(db, dbUsername, dbPassword)) {
        PreparedStatement ps;
        if (userId != null) {
          ps = conn.prepareStatement("SELECT SID, username, role FROM Users WHERE SID = ?");
          ps.setLong(1, userId);
        } else {
          ps = conn.prepareStatement("SELECT SID, username, role FROM Users WHERE email = ?");
          ps.setString(1, email);
        }
        try (ResultSet rs = ps.executeQuery()) {
          if (!rs.next()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"unknown user\"}");
            return;
          }
          long   sid      = rs.getLong("SID");
          String username = rs.getString("username");
          String role     = rs.getString("role");

          resp.getWriter().write(String.format(
            "{\"id\":%d,\"username\":\"%s\",\"role\":\"%s\"}",
            sid, username, role
          ));
        }
      }
    } catch (ClassNotFoundException | SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.getWriter().write("{\"error\":\"db error\"}");
    }
  }
}
