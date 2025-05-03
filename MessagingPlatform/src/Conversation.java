import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private int conversationId;
    private User user1;
    private User user2;
    private List<Message> messages;

    public Conversation(int conversationId, User user1, User user2) {
        this.conversationId = conversationId;
        this.user1 = user1;
        this.user2 = user2;
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean isParticipant(User user) {
        return user.equals(user1) || user.equals(user2);
    }

    public List<Message> getMessages() {
        return messages;
    }
}