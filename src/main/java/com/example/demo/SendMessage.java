import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

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
@WebServlet("/send-message-servlet")
public class SendMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Value("${spring.datasource.url}")
	private String db;
	
	@Value("${spring.datasource.username}")
	private String dbUsername;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
       
    public SendMessage() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String senderID = request.getParameter("yourUserID");
		String recieverID = request.getParameter("otherUserID");
		String message = request.getParameter("message");
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			//first update the messages table with the new message


			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			String sql = "INSERT INTO Messages (message, senderID, receiverID, timestamp) VALUES (?, ?, ?, ?)";			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, message);
			ps.setString(2, senderID);
			ps.setString(3, recieverID);
			ps.setTimestamp(4, timestamp);
			ps.executeUpdate();
			
			out.write("{\"success\": \"Message sent successfully!\"}");
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
}