import java.net.Socket;
import java.util.*;

class User {
    private String username;
    private Socket socket;
    private List<Message> messageHistory; // Historial de mensajes del usuario

    public User(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
        this.messageHistory = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }
    public List<Message> getMessageHistory() {
        return messageHistory;
    }
    public void addMessageToHistory(Message message) {
        messageHistory.add(message);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", socket=" + socket +
                ", messageHistory=" + messageHistory +
                '}';
    }
}
