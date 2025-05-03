package com.example.demo.servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashSet;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component
@WebServlet("/get-conversation-servlet")
public class GetConversation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Value("${spring.datasource.url}")
	private String db = "jdbc:mysql://localhost:3306/uscmarketplace";
	
	@Value("${spring.datasource.username}")
	private String dbUsername = "root";
	
	@Value("${spring.datasource.password}")
	private String dbPassword = "root";
       
    public GetConversation() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");

		
		String senderID = request.getParameter("yourUserID");
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			HashSet<Integer> userIds = new HashSet<>();
            String sql = "SELECT receiverID FROM Messages WHERE senderID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(senderID));
            rs = ps.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("receiverID"));
            }
            

            sql = "SELECT senderID FROM Messages WHERE receiverID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(senderID));
            rs = ps.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("senderID"));  
            }

            sql = "Select username from USERS where SID = ?;";
            ps = conn.prepareStatement(sql);  // Re-prepare the statement for the new query
            ArrayList<Profile> usernames = new ArrayList<Profile>();

            for (Integer id : userIds) {
                ps.setInt(1, id);
                rs = ps.executeQuery();
                if (rs.next()){
                    usernames.add(new Profile(rs.getString("username"), id));
                }
            }

            JsonArray jsonArray = new JsonArray();

            for (Profile profile : usernames) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("otherUserId", profile.getID());
                jsonObject.addProperty("otherUserName", profile.getUsername());
                jsonArray.add(jsonObject);
            }

            String jsonResponse = jsonArray.toString();
            out.write(jsonResponse);
			out.flush();
			
		} catch (SQLException sqle) {
			out.write("{\"error\": \"" + sqle.getMessage() + "\"}");
			out.flush();
		} catch (ClassNotFoundException e) {
			out.write("{\"error\": \"" + e.getMessage() + "\"}");
			out.flush();
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

class Profile{
    private String username;
    private int id;
    public Profile(String username, int id){
        this.username = username;
        this.id = id;
    }
    public int getID(){
        return id;
    }
    public String getUsername(){
        return username;
    }
}