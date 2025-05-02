package com.uscmarketplace;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@WebServlet("/create-listing-servlet")
public class CreateListing extends HttpServlet {
	private static final long serialVersionUID = 2L;
	
	@Value("${spring.datasource.url}")
	private String db;
	
	@Value("${spring.datasource.username}")
	private String dbUsername;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
       
    public CreateListing() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// Retrieving variables to verify log in
		String name = request.getParameter("name");
		float price = Float.valueOf(request.getParameter("price"));
		String category = request.getParameter("category");
		String description = request.getParameter("description");
		String image1 = request.getParameter("image1");
		String image2 = request.getParameter("image2");
		String image3 = request.getParameter("image3");
		String email = request.getParameter("userEmail");
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			// Get sellerID
			ps = conn.prepareStatement("SELECT SID FROM Users WHERE email = ?");
			ps.setString(1, email);
			rs = ps.executeQuery();
			int sellerID = -1;
			if (rs.next()) {
				sellerID = rs.getInt("userID");
			} else {
				out.println("{\"error\": \"An error occurred.\"}");
				return;
			}

			// Insert product
			ps = conn.prepareStatement("INSERT INTO Product (product_name, price, descript, image1, image2, image3, category, sellerID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, name);
			ps.setFloat(2, price);
			ps.setString(3, description);
			ps.setString(4, image1);
			ps.setString(5, image2);
			ps.setString(6, image3);
			ps.setString(7, category);
			ps.setInt(8, sellerID); 
			ps.executeUpdate();
			
			out.println("{\"status\": 200}");
			
		} catch (SQLException sqle) {
			out.println("{\"error\": \"An error occurred.\"}");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			out.println("{\"error\": \"An error occurred.\"}");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}

}
