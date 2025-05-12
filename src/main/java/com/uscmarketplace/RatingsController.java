package com.uscmarketplace;

import com.uscmarketplace.dto.SellerRatingsDto;
import com.uscmarketplace.dto.RatingResponseDto;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingsController {
    @Value("${spring.datasource.url}")      String db;
    @Value("${spring.datasource.username}") String dbUser;
    @Value("${spring.datasource.password}") String dbPass;

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    @GetMapping("/seller/{sellerId}")
    public SellerRatingsDto getSellerRatings(@PathVariable int sellerId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        double avg = 0;
        List<RatingResponseDto> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(db, dbUser, dbPass)) {
            // compute average
            try (PreparedStatement ps = c.prepareStatement("SELECT AVG(stars) FROM Ratings WHERE sellerID=?")) {
                ps.setInt(1, sellerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) avg = rs.getDouble(1);
            }

            // fetch each rating
            try (PreparedStatement ps = c.prepareStatement(
                   "SELECT r.id,r.stars,r.comment,r.createdAt,u.username " +
                   "FROM Ratings r JOIN Users u ON r.buyerID=u.SID WHERE r.sellerID=?")) {
                ps.setInt(1, sellerId);
                ResultSet rs = ps.executeQuery();
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

        return new SellerRatingsDto(avg, list);
    }

    // (you can similarly add a POST mapping here)
}
