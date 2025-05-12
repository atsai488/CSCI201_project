package com.uscmarketplace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;

@Controller
public class ChatController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    public static class Message {
        private String text;
        private int senderId;
        private long timestamp;

        public Message() {}

        public Message(String text, int senderId, long timestamp) {
            this.text = text;
            this.senderId = senderId;
            this.timestamp = timestamp;
        }

        public String getText() { return text; }
        public int getSenderId() { return senderId; }
        public long getTimestamp() { return timestamp; }

        public void setText(String text) { this.text = text; }
        public void setSenderId(int senderId) { this.senderId = senderId; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    public static String formatTimestamp(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/publicmessages")
    public Message handleMessage(Message chatMessage) throws Exception {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO Messages (message, timeStamp, SenderID) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, chatMessage.getText());
                ps.setTimestamp(2, Timestamp.from(Instant.ofEpochMilli(chatMessage.getTimestamp())));
                ps.setInt(3, chatMessage.getSenderId());
                ps.executeUpdate();
            }
        }
        
        return chatMessage;
    }
}
