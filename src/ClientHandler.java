import java.io.*;
import java.net.Socket;
import java.util.List;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ChatServer server;

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Leer nombre de usuario del cliente
            String username = in.readLine();
            User user = new User(username, clientSocket);
            server.addUser(username, clientSocket);

            String message;
            out.println("Bienvenido al chat. Para enviar un mensaje privado, usa: /msg <usuario_destino> <mensaje>");
            out.println("Para enviar un mensaje a un grupo, usa: /msggroup <nombre_grupo> <mensaje>");
            out.println("Para crear un nuevo grupo, usa: /creategroup <nombre_grupo>");
            out.println("Para unirse a un grupo, usa: /join <nombre_grupo> <usuario_a_anadir>");
            out.println("Para enviar un mensaje de broadcast, simplemente escribe el mensaje.");
            out.println("Para visualizar el historial de mensajes, usa: /history");
            out.println("Escribe 'exit' para salir del chat.");

            while ((message = in.readLine()) != null) {
                if (message.equals("exit")) {
                    break;
                } else if (message.startsWith("/msg ")) {
                    // Verificar si el mensaje es un mensaje privado
                    String[] parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        String recipient = parts[1];
                        String messageContent = parts[2];
                        server.sendMessageToUser(recipient, username,messageContent);
                        Message messageToSend = new Message(username, recipient, messageContent, System.currentTimeMillis());
                        // Agregar mensaje al historial del usuario remitente
                        addMessageToUserHistory(username, messageToSend);
                        // Obtener el usuario destinatario y agregar el mensaje a su historial
                        User recipientUser = server.getUserByUsername(recipient);
                        if (recipientUser != null) {
                            recipientUser.addMessageToHistory(messageToSend);
                        }
                    }
                } else if (message.startsWith("/msggroup ")) {
                    // Verificar si el mensaje es un mensaje de grupo
                    String[] parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        String groupName = parts[1];
                        boolean flag = validateUserBelongsGroup(username, groupName);
                        if (!flag){
                            out.println("No puede enviar mensajes a este grupo porque no es un participante.");
                        } else {
                            String messageContent = parts[2];
                            server.sendMessageToGroup(groupName, username,messageContent);
                            Message messageToSend = new Message(username, "Grupo_" + groupName, messageContent, System.currentTimeMillis());
                            // Agregar mensaje al historial del usuario remitente
                            addMessageToUserHistory(username, messageToSend);
                            // Obtener los usuarios del grupo y agregar el mensaje a sus historiales
                            ChatGroup group = server.getGroupByName(groupName);
                            if (group != null) {
                                for (User groupUser : group.getUsers()) {
                                    System.out.println(messageToSend.toString());
                                    groupUser.addMessageToHistory(messageToSend);
                                }
                            }
                        }

                    }
                } else if (message.startsWith("/creategroup ")) {
                    // Verificar si el mensaje es para crear un grupo
                    String[] parts = message.split(" ", 2);
                    if (parts.length == 2) {
                        String groupName = parts[1];
                        server.createGroup(groupName, username);
                    }
                } else if (message.startsWith("/join ")) {
                    String[] parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        String groupName = parts[1];
                        String usernameToAdd = parts[2];
                        server.addUserToGroup(groupName, usernameToAdd); // Agregar al usuario actual al grupo
                    }
                } else if (message.startsWith("/history")) {
                    // Mostrar historial de mensajes del usuario
                    User userHistory = server.getUserByUsername(username);
                    List<Message> messageHistory = userHistory.getMessageHistory();
                    out.println(messageHistory);
                    out.println("Historial de mensajes:");
                    for (Message msg : messageHistory) {
                        String sender = msg.getSender();
                        String recipient = msg.getRecipient() != null ? msg.getRecipient() : "Broadcast";
                        String content = msg.getContent();
                        out.println("Sender: " + sender + " / " + "Recipient: " + recipient + " / " + "Message: " + content);
                    }
                } else {
                    // Broadcast del mensaje a todos los clientes
                    server.broadcastMessage(username,message, clientSocket);
                    Message messageToSend = new Message( username,null, message, System.currentTimeMillis());
                    User senderUser = server.getUserByUsername(username);
                    // Agregar mensaje al historial del usuario remitente
                    if (senderUser != null) {
                        senderUser.addMessageToHistory(messageToSend);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMessageToUserHistory(String username, Message messageToSend) {
        User senderUser = server.getUserByUsername(username);
        if (senderUser != null) {
            senderUser.addMessageToHistory(messageToSend);
        }
    }

    public boolean validateUserBelongsGroup(String username, String groupName) {
        ChatGroup group = server.getGroupByName(groupName);
        boolean flag = false;
        if (group != null) {
            for (User groupUser : group.getUsers()) {
                if (groupUser.getUsername().equals(username)){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}