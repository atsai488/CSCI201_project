import java.net.*;     // For networking (ServerSocket, Socket)
import java.io.*;      // For input/output streams
import java.util.*;    // For collections like List and Map
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


// MessagingServer handles client connections and routes messages
public class ServerMessaging {
	
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/ChatApp";
	private static final String JDBC_USER = "root";
	private static final String JDBC_PASSWORD = "Aapledaaple123jjm!"; // Replace this

    // Keep track of all client sockets (for potential future broadcast)
    private static List<Socket> clients = new ArrayList<>();

    // Map userId → their Socket (used for direct messaging)
    public static Map<String, Socket> userSockets = new HashMap<>();
    
    public static Set<String> getOnlineUsernames() {
        return userSockets.keySet();
    }

    public static void main(String[] args) throws IOException {
        // Start server on port 1233
        ServerSocket serverSocket = new ServerSocket(1233);
        System.out.println("Messaging server started on port 1233...");

        // Loop forever accepting client connections
        while (true) {
            Socket socket = serverSocket.accept(); // Block until client connects
            clients.add(socket); // Store the socket

            System.out.println("New client connected.");

            // Start a thread to handle this client
            new Thread(() -> handleClient(socket)).start();
        }
    }
    
    private static void saveMessageToDatabase(Message msg) {
        String sql = "INSERT INTO MessageLog (senderUsername, receiverUsername, content, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, msg.getSenderUsername());
            ps.setString(2, msg.getReceiverUsername());
            ps.setString(3, msg.getContent());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(msg.getTimestamp()));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert message: " + e.getMessage());
        }
    }


    // Handles a single client connection
    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // First message should be a registration (sender info)
            String registrationMsg = in.readLine();
            Message reg = Message.fromJson(registrationMsg);

            if (reg != null) {
                userSockets.put(reg.getSenderUsername(), socket); // Save this user's socket
                System.out.println("Registered user: " + reg.getSenderUsername());
            }

            // Listen for future messages from this client
            String message;
            while ((message = in.readLine()) != null) {
                Message msg = Message.fromJson(message);
                if (msg != null) {
                    System.out.println("DM from " + msg.getSenderUsername()
                            + " to ID " + msg.getReceiverUsername() + ": " + msg.getContent());

                    // Send the message directly to the receiver
                    sendDirectMessage(msg);
                    
                    // Send to MySQL database
                    saveMessageToDatabase(msg);
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }

    // Send a direct message to the specific receiverId (not broadcast)
    private static void sendDirectMessage(Message msg) {
        Socket targetSocket = userSockets.get(msg.getReceiverUsername());

        if (targetSocket != null) {
            try {
                PrintWriter out = new PrintWriter(targetSocket.getOutputStream(), true);
                out.println("From " + msg.getSenderUsername() + ": " + msg.getContent());
            } catch (IOException e) {
                System.out.println("Error sending DM to user ID " + msg.getReceiverUsername());
            }
        } else {
            System.out.println("User ID " + msg.getReceiverUsername() + " is not online.");
        }
    }
}
