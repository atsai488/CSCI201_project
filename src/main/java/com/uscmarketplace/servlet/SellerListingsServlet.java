package com.uscmarketplace.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@Component
@WebServlet("/api/listings/seller/*")
public class SellerListingsServlet extends HttpServlet {
    @Value("${spring.datasource.url}")
    private String db;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        // pathInfo is “/2” for /api/listings/seller/2
        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing sellerId\"}");
            return;
        }

        int sellerId;
        try {
            sellerId = Integer.parseInt(path.substring(1));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid sellerId\"}");
            return;
        }

        String sql = "SELECT ID AS id, product_name, price, image1 FROM Product WHERE sellerID = ?";
        List<Map<String,Object>> products = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(db, dbUsername, dbPassword);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, sellerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String,Object> p = new HashMap<>();
                        p.put("id", rs.getInt("id"));
                        p.put("product_name", rs.getString("product_name"));
                        p.put("price", rs.getDouble("price"));
                        p.put("image1", rs.getString("image1"));
                        products.add(p);
                    }
                }
            }
            mapper.writeValue(resp.getWriter(), products);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
        }
    }
}
