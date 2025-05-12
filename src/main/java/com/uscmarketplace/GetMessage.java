package com.uscmarketplace;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@WebServlet("/get-messages-servlet")
public class GetMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Value("${spring.datasource.url}")
	private String db = "jdbc:mysql://localhost:3306/uscmarketplace";
	
	@Value("${spring.datasource.username}")
	private String dbUsername = "root";
	
	@Value("${spring.datasource.password}")
	private String dbPassword = "root";
       
    public GetMessage() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			
			String sql = "SELECT message, timeStamp, SenderID FROM Messages;";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			PriorityQueue<Message> messageHeap = new PriorityQueue<Message>(new Comparator<Message>() {
				@Override
				public int compare(Message m1, Message m2) {
					Timestamp t1 = Timestamp.valueOf(m1.getTimestamp());
					Timestamp t2 = Timestamp.valueOf(m2.getTimestamp());
					return t1.compareTo(t2);
				}
			});
		
			while (rs.next()) {
				Message message = new Message(rs.getString("message"), rs.getInt("SenderID"), rs.getString("timeStamp"));
				messageHeap.add(message);
			}
			
			//get messages in order of timestamp
			ArrayList<String> messageList = new ArrayList<>();
			while (!messageHeap.isEmpty()) {
				Message message = messageHeap.poll();
				String jsonMessage = "{\"text\": \"" + message.getText() + "\", \"senderId\": " + message.getSenderId() + ", \"timestamp\": \"" + message.getTimestamp() + "\"}";
				messageList.add(jsonMessage);
			}
			String jsonArray = String.join(",", messageList);
			String fullJson = "{\"messages\":[" + jsonArray + "]}";
			out.write(fullJson);		
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

class Message {
	private String text;
	private int senderId;
	private String timestamp;
	
	public Message(String text, int senderId, String timestamp) {
		this.text = text;
		this.senderId = senderId;
		this.timestamp = timestamp;
	}
	
	public String getText() {
		return text;
	}
	
	public int getSenderId() {
		return senderId;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
}
