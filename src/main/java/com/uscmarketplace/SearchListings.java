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
@WebServlet("/search-listings-servlet")
public class SearchListings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Value("${spring.datasource.url}")
	private String db;
	
	@Value("${spring.datasource.username}")
	private String dbUsername;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
       
    public SearchListings() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String search = request.getParameter("search");
			if (search == null || search.isEmpty()) {
				out.print("{\"error\":\"Search term is required\"}");
				out.flush();
				throw new IllegalArgumentException("Search term is required");
			}
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			String sql = "SELECT * FROM Product WHERE product_name LIKE ? LIMIT 12;";
            ps = conn.prepareStatement(sql);
			ps.setString(1, "%" + search + "%");
            rs = ps.executeQuery();
            
            Listing[] listings = new Listing[12];
            int i = 0;
            while (rs.next()) {
            	Listing listing = new Listing(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getFloat("price"),
                        rs.getString("descript"),
                        rs.getString("image1"),
                        rs.getString("image2"),
                        rs.getString("image3"),
                        rs.getInt("sellerId")
                );
                listings[i] = listing;
                i++;
            }
            String json = "{\"listings\":[";
            for (int j = 0; j < i; j++) {
                json += "{\"id\":" + listings[j].id + ",\"product_name\":\"" + listings[j].product_name + "\",\"price\":" + listings[j].price + ",\"description\":\"" + listings[j].description + "\",\"image1\":\"" + listings[j].image1 + "\",\"sellerId\":" + listings[j].sellerId + "}";
                if (j < i - 1) {
                    json += ",";
                }
            }
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