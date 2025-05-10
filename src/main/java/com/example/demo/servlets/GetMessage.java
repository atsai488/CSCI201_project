package com.example.demo.servlets;
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
import java.util.List;
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

	@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

  // CORS
  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
  response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  response.setHeader("Access-Control-Allow-Headers", "Content-Type");

  // 1️⃣ Read the correct params
  String senderID   = request.getParameter("yourUserID");
  String receiverID = request.getParameter("otherUserID");

  // 2️⃣ (optional) validate non-null, numeric…

  PrintWriter out = response.getWriter();
  Connection conn = null;
  PreparedStatement ps = null;
  ResultSet rs = null;
  try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    conn = DriverManager.getConnection(db, dbUsername, dbPassword);

    // 3️⃣ Fetch messages in both directions
    String sql = "SELECT message, timestamp, senderID FROM Messages "
               + "WHERE (senderID = ? AND receiverID = ?) "
               + "   OR (senderID = ? AND receiverID = ?)";
    ps = conn.prepareStatement(sql);
    int sid = Integer.parseInt(senderID);
    int rid = Integer.parseInt(receiverID);
    ps.setInt(1, sid);
    ps.setInt(2, rid);
    ps.setInt(3, rid);  // swapped for reverse direction
    ps.setInt(4, sid);
    rs = ps.executeQuery();

    // 4️⃣ Build JSON array in chronological order
    List<String> list = new ArrayList<>();
    while (rs.next()) {
      String text = rs.getString("message");
      String ts   = rs.getTimestamp("timestamp").toString();
      int    from = rs.getInt("senderID");
      list.add(String.format(
        "{\"text\":\"%s\",\"senderId\":%d,\"timestamp\":\"%s\"}",
        text, from, ts
      ));
    }
    String msgs = String.join(",", list);
    String json = String.format(
      "{\"otherUserId\":%s,\"messages\":[%s]}",
      receiverID, msgs
    );

    out.write(json);
    out.flush();
  } catch (Exception e) {
    out.write("{\"error\":\""+e.getMessage()+"\"}");
    out.flush();
  } finally {
    // close rs, ps, conn…
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
