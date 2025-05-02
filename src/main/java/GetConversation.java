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

@Component
@WebServlet("/get-conversation-servlet")
public class GetConversation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Value("${spring.datasource.url}")
	private String db;
	
	@Value("${spring.datasource.username}")
	private String dbUsername;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
       
    public GetConversation() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String senderID = request.getParameter("yourUserID");
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db, dbUsername, dbPassword);
			String sql = "SELECT receiverID FROM Messages WHERE senderID = ?;";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(senderID));
			rs = ps.executeQuery();
            HashSet<Integer> userIds = new HashSet<>();
            while (rs.next()) {
                userIds.add(rs.getInt("receiverID"));
            }
			
			//now get messages that were sent to the senderID from the receiverID
			sql = "SELECT senderID FROM Messages WHERE receiverID = ?;";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(senderID));
            while (rs.next()) {
                userIds.add(rs.getInt("receiverID"));
            }

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{\"ids\": [");
            int count = 0;
            for (Integer id : userIds) {
                if (count > 0) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append(id);
                count++;
            }
            jsonBuilder.append("]}");

            String jsonResponse = jsonBuilder.toString();
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
