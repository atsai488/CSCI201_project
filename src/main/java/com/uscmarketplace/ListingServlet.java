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
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String id = request.getParameter("id");

		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			
			String sql = "SELECT * FROM Product WHERE id = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(id));
            rs = ps.executeQuery();
            
            Listing listing = null;
            
            while (rs.next()) {
            	listing = new Listing(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getFloat("price"),
                        rs.getString("descript"),
                        rs.getString("image1"),
                        rs.getString("image2"),
                        rs.getString("image3"),
                        rs.getInt("sellerId")
                );
            }
            String json = "{\"listings\":[";
            json += "{\"id\":" + listing.id + ",\"product_name\":\"" + listing.product_name + "\",\"price\":" + listing.price + ",\"description\":\"" + listing.description + "\",\"image1\":\"" + listing.image1 + "\",\"image2\":\"" + listing.image2 + "\",\"image3\":\"" + listing.image3 + "\",\"sellerId\":" + listing.sellerId + "}";
            json += "]}";
            response.setContentType("application/json");
            out.print(json);
            out.flush(); 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e){
			
		}finally {
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