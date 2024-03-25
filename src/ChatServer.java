import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private List<Socket> clientSockets;
    private Map<String, Socket> usernameToSocketMap;
    private Map<String, ChatGroup> groupNameToGroupMap;


    public ChatServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newFixedThreadPool(10);
            clientSockets = new ArrayList<>();
            usernameToSocketMap = new HashMap<>();
            groupNameToGroupMap = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Chat server started on port " + PORT);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                clientSockets.add(clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                threadPool.execute(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addUser(String username, Socket socket) {
        usernameToSocketMap.put(username, socket);
        System.out.println("Usuario '" + username + "' conectado desde " + socket.getInetAddress());
    }

    public void createGroup(String groupName, String username) {
        ChatGroup newGroup = new ChatGroup();
        groupNameToGroupMap.put(groupName, newGroup);
        System.out.println("Nuevo grupo '" + groupName + "' creado.");
        addUserToGroup(groupName, username);
    }

    //Broadcast
    public void broadcastMessage(String message, Socket sender) {
        for (Socket clientSocket : clientSockets) {
            if (clientSocket != sender) { // No enviar el mensaje al remitente original
                try {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Group
    public void sendMessageToGroup(String groupName, String senderUsername, String message) {
        ChatGroup group = groupNameToGroupMap.get(groupName);
        if (group != null) {
            for (User user : group.getUsers()) {
                if (!user.getUsername().equals(senderUsername)) { // Verificar si el usuario no es el remitente
                    sendMessageToUser(user.getUsername(), senderUsername, "[" + groupName + "] " + message);
                }
            }
        } else {
            System.out.println("El grupo " + groupName + " no existe.");
        }
    }

    //ToUser
    public void sendMessageToUser(String recipientUsername, String senderUsername,String message) {
        Socket recipientSocket = usernameToSocketMap.get(recipientUsername);
        if (recipientSocket != null) {
            try {
                PrintWriter out = new PrintWriter(recipientSocket.getOutputStream(), true);
                out.println(senderUsername + ": " + message);
                recipientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El usuario " + recipientUsername + " no está conectado.");
        }
    }

    //ENVIAR AUDIOS
    public void sendVoiceMessage(String audioFilePath, List<User> recipients) throws IOException {
        for (User recipient : recipients) {
            // Envía la ruta del archivo de audio
            PrintWriter out = new PrintWriter(recipient.getSocket().getOutputStream(), true);
            out.println(audioFilePath);
        }
    }

    public void addUserToGroup(String groupName, String username) {
        ChatGroup group = groupNameToGroupMap.get(groupName);
        if (group != null) {
            // Verificar si el usuario ya está en el grupo
            for (User user : group.getUsers()) {
                if (user.getUsername().equals(username)) {
                    System.out.println("El usuario ya está en el grupo.");
                    return;
                }
            }
            // Si el usuario no está en el grupo, buscar su socket y agregarlo
            Socket socket = usernameToSocketMap.get(username);
            if (socket != null) {
                User user = new User(username, socket);
                group.addUser(user);
                System.out.println("Usuario '" + username + "' añadido al grupo '" + groupName + "'.");
            } else {
                System.out.println("Usuario '" + username + "' no encontrado.");
            }
        } else {
            System.out.println("El grupo '" + groupName + "' no existe.");
        }
    }

}