import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Message {
	private int messageId;
	private int senderId;
	private int receiverId;
	private String senderUsername;
	private String content;
	private LocalDateTime timestamp;
	private boolean isRead;
	private String receiverUsername;
	
	public Message(int messageId, int senderId, int receiverId, String senderUsername, String receiverUsername, String content) {
		this.messageId = messageId;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.receiverUsername = receiverUsername;
		this.senderUsername = senderUsername;
		this.content = content;
		this.timestamp = LocalDateTime.now();
		this.isRead = false;
	}
	
	public void markAsRead()
	{
		isRead = true;
	}
	
	public String toJson() {
	    return String.format(
	        "{\"senderId\":%d,\"receiverId\":%d,\"senderUsername\":\"%s\",\"receiverUsername\":\"%s\",\"content\":\"%s\",\"timestamp\":\"%s\"}",
	        senderId,
	        receiverId,
	        senderUsername.replace("\"", "\\\""),
	        receiverUsername.replace("\"", "\\\""),
	        content.replace("\"", "\\\""),
	        timestamp.toString()
	    );
	}

	
	// parsing
	public static Message fromJson(String json) {
		try {
			json = json.trim().replace("{", "").replace("}", "");
			String[] parts = json.split(",");
			Map<String, String> map = new HashMap<>();

			for (String part : parts) {
				String[] kv = part.split(":", 2);
				map.put(kv[0].replace("\"", "").trim(), kv[1].replace("\"", "").trim());
			}

			int senderId = Integer.parseInt(map.get("senderId"));
			int receiverId = Integer.parseInt(map.get("receiverId"));
			String senderUsername = map.get("senderUsername");
			String content = map.get("content");
			String receiverUsername = map.get("receiverUsername");

			return new Message(0, senderId, receiverId, senderUsername, receiverUsername, content);
		} catch (Exception e) {
			System.out.println("Error parsing message JSON: " + json);
			return null;
		}
	}
	
	public String getSenderUsername() {
		return senderUsername;
	}
	
	public String getReceiverUsername() {
	    return receiverUsername;
	}
	
	public LocalDateTime getTimestamp() {
	    return timestamp;
	}


	public int getSenderId() { return senderId; }
	public String getContent() { return content; }
	
}