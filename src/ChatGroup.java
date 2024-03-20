import java.util.ArrayList;
import java.util.List;

public class ChatGroup {
    private List<User> users;

    public ChatGroup() {
        users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }
}
