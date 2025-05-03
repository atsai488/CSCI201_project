import java.io.*; // For input/output streams and readers
import java.net.*; // For socket programming (Socket)

public class MessagingClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1233);

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.print("Enter your username: ");
        String username = userInput.readLine();
        int senderId = username.hashCode();

        // REGISTER the client with the server
        Message registration = new Message(0, senderId, 0, username, "", "REGISTER");
        out.println(registration.toJson());

        System.out.println("Connected! Type your message:");

        // Thread to listen for incoming messages
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("New message: " + msg);
                }
            } catch (IOException e) {
                System.out.println("Disconnected.");
            }
        }).start();

        // Main thread to send messages
        while (true) {
            System.out.print("Send to (username): ");
            String receiverUsername = userInput.readLine();

            System.out.print("Your message: ");
            String content = userInput.readLine();

            int receiverId = receiverUsername.hashCode(); // optional, kept for consistency

            Message msg = new Message(0, senderId, receiverId, username, receiverUsername, content);
            out.println(msg.toJson());
        }
    }
}