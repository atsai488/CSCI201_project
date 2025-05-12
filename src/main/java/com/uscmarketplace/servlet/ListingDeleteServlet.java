// src/main/java/com/uscmarketplace/servlet/ListingDeleteServlet.java
package com.uscmarketplace.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;

@Component
@WebServlet("/api/listings/*")
public class ListingDeleteServlet extends HttpServlet {
  @Value("${spring.datasource.url}")      private String dbUrl;
  @Value("${spring.datasource.username}") private String dbUser;
  @Value("${spring.datasource.password}") private String dbPass;

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // CORS if needed
    resp.setHeader("Access‑Control‑Allow‑Origin", "http://localhost:3000");

    String path = req.getPathInfo();              // e.g. "/42"
    int listingId = Integer.parseInt(path.substring(1));

    Long sellerId = (Long) req.getSession().getAttribute("userId");
    if (sellerId == null) {
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    String sql = "DELETE FROM Product WHERE ID = ? AND sellerID = ?";
    try (
      Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setInt(1, listingId);
      ps.setLong(2, sellerId);
      int rows = ps.executeUpdate();
      if (rows==1) resp.setStatus(HttpServletResponse.SC_OK);
      else          resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    } catch (SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
