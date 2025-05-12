package com.uscmarketplace.servlet;

import com.uscmarketplace.dto.RatingDto;
import com.uscmarketplace.dto.RatingResponseDto;
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@WebServlet("/api/ratings")
public class RatingServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // CORS (if needed)
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // parse incoming JSON
        RatingDto dto = mapper.readValue(req.getReader(), RatingDto.class);

        // stub session‑user for now; replace with real auth later
        Long buyerId = (Long) req.getSession().getAttribute("userId");
        if (buyerId == null) {
          buyerId = 1L;
          req.getSession().setAttribute("userId", buyerId);
        }

        // insert the new rating, then fetch it back for the response
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(db, dbUsername, dbPassword)) {
                // 1) INSERT
                String insertSql = """
                  INSERT INTO Ratings(buyerID, sellerID, stars, comment)
                  VALUES (?, ?, ?, ?)
                  """;
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, buyerId);
                    ps.setLong(2, dto.getSellerId());
                    ps.setInt(3, dto.getStars());
                    ps.setString(4, dto.getComment());
                    ps.executeUpdate();

                    // get generated rating ID
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                      if (!keys.next()) {
                        throw new SQLException("No ID returned for new rating");
                      }
                      long ratingId = keys.getLong(1);

                      // 2) SELECT for response
                      String fetchSql = """
                        SELECT r.id, r.stars, r.comment, r.createdAt, u.username
                        FROM Ratings r
                        JOIN Users u ON r.buyerID = u.SID
                        WHERE r.id = ?
                        """;
                      try (PreparedStatement ps2 = conn.prepareStatement(fetchSql)) {
                        ps2.setLong(1, ratingId);
                        try (ResultSet rs = ps2.executeQuery()) {
                          if (rs.next()) {
                            RatingResponseDto outDto = new RatingResponseDto(
                              rs.getLong("id"),
                              rs.getInt("stars"),
                              rs.getString("comment"),
                              rs.getString("username"),
                              rs.getTimestamp("createdAt").toInstant()
                            );
                            resp.setContentType("application/json");
                            try (PrintWriter writer = resp.getWriter()) {
                              mapper.writeValue(writer, outDto);
                            }
                          }
                        }
                      }
                    }
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter writer = resp.getWriter()) {
              writer.write("{\"error\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
            }
        }
    }
}
