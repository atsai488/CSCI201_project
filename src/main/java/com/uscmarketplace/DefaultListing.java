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
@WebServlet("/default-listings-servlet")
public class DefaultListing extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Value("${spring.datasource.url}")
	private String db;
	
	@Value("${spring.datasource.username}")
	private String dbUsername;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
       
    public DefaultListing() {
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
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			String sql = "SELECT * FROM Product LIMIT 12;";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            Listing[] listings = new Listing[12];
            int i = 0;
            while (rs.next()) {
                Listing listing = new Listing(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getString("image1"),
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
            out.print(json);
            out.flush(); 

		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} catch (ClassNotFoundException e) {
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

class Listing{
    public int id;
    public String product_name;
    public float price;
    public String description;
    public String image1;
    public int sellerId;
    public String category;

    public Listing(int id, String product_name, float price, String description, String image1, int sellerId) {
        this.id = id;
        this.product_name = product_name;
        this.price = price;
        this.description = description;
        this.image1 = image1;
        this.sellerId = sellerId;
    }
}