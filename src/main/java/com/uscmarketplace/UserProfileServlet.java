package com.uscmarketplace;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@WebServlet("/api/user/profile")
@Component
public class UserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Value("${spring.datasource.url}")
    private String db;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        // Get user ID from session
        Long userId = (Long) request.getSession().getAttribute("userId");
        System.out.println("Session userId: " + userId); // Debug log
        
        if (userId == null) {
            System.out.println("No userId found in session"); // Debug log
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"Not logged in\"}");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(db, dbUsername, dbPassword);
            
            String sql = "SELECT username, email FROM Users WHERE SID = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                
                System.out.println("Found user: " + username); // Debug log
                
                // Create JSON response
                String json = String.format(
                    "{\"username\":\"%s\",\"email\":\"%s\"}",
                    username, email
                );
                out.write(json);
            } else {
                System.out.println("No user found with ID: " + userId); // Debug log
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"User not found\"}");
            }
            
        } catch (Exception e) {
            System.out.println("Error in UserProfileServlet: " + e.getMessage()); // Debug log
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Database error\"}");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
} 