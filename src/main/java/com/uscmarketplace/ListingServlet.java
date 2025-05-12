package com.uscmarketplace;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@WebServlet("/listing-details-servlet")
public class ListingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Value("${spring.datasource.url}")
    private String db;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
       
    public ListingServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String id = request.getParameter("id");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(db, dbUsername, dbPassword)) {
                
                // 1) Fetch the product row
                String productSql = 
                    "SELECT * FROM Product WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(productSql)) {
                    ps.setInt(1, Integer.parseInt(id));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.write("{\"error\":\"Product not found\"}");
                            return;
                        }

                        int productId      = rs.getInt("id");
                        String name        = rs.getString("product_name");
                        float price        = rs.getFloat("price");
                        String desc        = rs.getString("descript");
                        String img1        = rs.getString("image1");
                        String img2        = rs.getString("image2");
                        String img3        = rs.getString("image3");
                        int   sellerId     = rs.getInt("sellerID");

                        // 2) Fetch the seller's username
                        String sellerUsername = "";
                        String userSql = "SELECT username FROM Users WHERE SID = ?";
                        try (PreparedStatement ups = conn.prepareStatement(userSql)) {
                            ups.setInt(1, sellerId);
                            try (ResultSet urs = ups.executeQuery()) {
                                if (urs.next()) {
                                    sellerUsername = urs.getString("username");
                                }
                            }
                        }

                        // 3) Build JSON response
                        StringBuilder json = new StringBuilder();
                        json.append("{\"listings\":[{")
                            .append("\"id\":").append(productId).append(",")
                            .append("\"product_name\":\"").append(escape(name)).append("\",")
                            .append("\"price\":").append(price).append(",")
                            .append("\"description\":\"").append(escape(desc)).append("\",")
                            .append("\"image1\":\"").append(escape(img1)).append("\",")
                            .append("\"image2\":\"").append(escape(img2)).append("\",")
                            .append("\"image3\":\"").append(escape(img3)).append("\",")
                            .append("\"sellerId\":").append(sellerId).append(",")
                            .append("\"sellerUsername\":\"").append(escape(sellerUsername)).append("\"")
                          .append("}]}");

                        out.print(json.toString());
                        out.flush();
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + escape(e.getMessage()) + "\"}");
            out.flush();
        }
    }

    // Simple JSON value escaper
    private String escape(String s) {
        return s == null 
          ? "" 
          : s.replace("\\","\\\\").replace("\"","\\\"");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
