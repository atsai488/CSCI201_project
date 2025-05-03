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
@WebServlet("/login-servlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Value("${spring.datasource.url}")
	private String db;
	
	@Value("${spring.datasource.username}")
	private String dbUsername;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
       
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// Retrieving variables to verify log in
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		PrintWriter out = response.getWriter();
		
		if(email.equals("guest") && password.equals("guest")) {
			out.write("guestSuccess");
			return;
		}
		
		BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
		
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			
			ps = conn.prepareStatement("SELECT * FROM Users WHERE email = ?");
			ps.setString(1, email);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				String passwordHash = rs.getString("password");
				if(bc.matches(password, passwordHash)) {
					out.println("success");
				}
				else {
					out.println("fail");
				}
			}
			else {
				out.println("fail");
			}
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
