package com.uscmarketplace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant; 
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.JsonNode; 
import com.fasterxml.jackson.databind.ObjectMapper; 

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Component; 

@Component 
@ServerEndpoint("/chat") 
public class GlobalChatEndpoint {    
    private static final Set<Session> clients = ConcurrentHashMap.newKeySet();
    private static final ExecutorService executor = Executors.newFixedThreadPool(20);
    private static final ObjectMapper objectMapper = new ObjectMapper();    
    @Value("${spring.datasource.url}")
    private String dbUrl; 
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("Client connected: " + session.getId());
    }
    /**
     * Handles incoming messages from a client.
     *
     * @param message The message string received.
     * @param sender  The session of the client who sent the message.
     */
    @OnMessage
    public void onMessage(String message, Session sender) {
        
        executor.submit(() -> {
            Connection conn = null;
            try {
                JsonNode messageNode = objectMapper.readTree(message);
                String text = messageNode.get("text").asText();
                int senderId = messageNode.get("senderId").asInt();
                conn = getConnection(); 
                String sql = "INSERT INTO Messages (message, timeStamp, SenderID) VALUES (?, ?, ?)";                
                Timestamp serverTimestamp = Timestamp.from(Instant.now());
                try {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, text);
                    ps.setTimestamp(2, serverTimestamp);
                    ps.setInt(3, senderId);
                    int rowsAffected = ps.executeUpdate();
                }
                catch (SQLException e) {
                    return;
                }                    
                broadcast(message, sender); 
            } catch (SQLException sqle) {
                
            } catch (Exception e) { 
                 
            } finally {
                 if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        });
    }
    private void broadcast(String message, Session sender) {
        
        for (Session client : clients) {
            if (client.isOpen()) {
                
                client.getAsyncRemote().sendText(message, sendResult -> {
                    if (sendResult.getException() != null) {
                        System.err.println("Error sending message to client " + client.getId() + ": " + sendResult.getException().getMessage());
                        
                    }
                });
            }
        }
    }
    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    
    
    public static class Message {
        private String text;
        private int senderId;
        private String timestamp; 
        
        public Message(String text, int senderId, String timestamp) {
            this.text = text;
            this.senderId = senderId;
            this.timestamp = timestamp;
        }
        public String getText() { return text; }
        public int getSenderId() { return senderId; }
        public String getTimestamp() { return timestamp; } 
    }
    

}