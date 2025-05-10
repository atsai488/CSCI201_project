package com.example.demo.dto;

public class MessageDTO {
    private String text;
    private int senderId;
    private int receiverId;
    private String timestamp;

    // Default constructor (required for Jackson/STOMP)
    public MessageDTO() {}

    // All-args constructor (optional)
    public MessageDTO(String text, int senderId, int receiverId, String timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
    }

    // Getters & setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
