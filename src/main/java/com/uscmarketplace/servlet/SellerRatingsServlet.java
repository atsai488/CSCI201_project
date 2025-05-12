package com.uscmarketplace.servlet;

import com.uscmarketplace.dto.RatingResponseDto;
import com.uscmarketplace.dto.SellerRatingsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@WebServlet("/api/ratings/seller/*")
public class SellerRatingsServlet extends HttpServlet {
    @Value("${spring.datasource.url}")
    private String db;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // CORS (if needed)
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // extract sellerId from URL (/api/ratings/seller/{id})
        String path = req.getPathInfo();            // e.g. "/2"
        long sellerId = Long.parseLong(path.substring(1));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(db, dbUsername, dbPassword)) {
                // 1) compute average
                double avg = 0.0;
                String avgSql = "SELECT AVG(stars) FROM Ratings WHERE sellerID = ?";
                try (PreparedStatement psAvg = conn.prepareStatement(avgSql)) {
                  psAvg.setLong(1, sellerId);
                  try (ResultSet rsAvg = psAvg.executeQuery()) {
                    if (rsAvg.next()) {
                      avg = rsAvg.getDouble(1);
                    }
                  }
                }

                // 2) fetch all ratings
                List<RatingResponseDto> list = new ArrayList<>();
                String listSql = """
                  SELECT r.id, r.stars, r.comment, r.createdAt, u.username
                  FROM Ratings r
                  JOIN Users u ON r.buyerID = u.SID
                  WHERE r.sellerID = ?
                  ORDER BY r.createdAt DESC
                  """;
                try (PreparedStatement psList = conn.prepareStatement(listSql)) {
                  psList.setLong(1, sellerId);
                  try (ResultSet rs = psList.executeQuery()) {
                    while (rs.next()) {
                      list.add(new RatingResponseDto(
                        rs.getLong("id"),
                        rs.getInt("stars"),
                        rs.getString("comment"),
                        rs.getString("username"),
                        rs.getTimestamp("createdAt").toInstant()
                      ));
                    }
                  }
                }

                // 3) build and send DTO
                SellerRatingsDto outDto = new SellerRatingsDto(avg, list);
                resp.setContentType("application/json");
                try (PrintWriter writer = resp.getWriter()) {
                  mapper.writeValue(writer, outDto);
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter w = resp.getWriter()) {
              w.write("{\"error\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
            }
        }
    }
}
