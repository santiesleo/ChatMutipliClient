import java.io.*;
import java.net.Socket;

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
            server.addUser(username, clientSocket);

            String message;
            out.println("Bienvenido al chat. Para enviar un mensaje privado, usa: /msg <usuario_destino> <mensaje>");
            out.println("Para enviar un mensaje a un grupo, usa: /msggroup <nombre_grupo> <mensaje>");
            out.println("Para crear un nuevo grupo, usa: /creategroup <nombre_grupo>");
            out.println("Para unirse a un grupo, usa: /join <nombre_grupo>");
            out.println("Para enviar un mensaje de broadcast, simplemente escribe el mensaje.");
            out.println("Escribe 'exit' para salir del chat.");

            while ((message = in.readLine()) != null) {
                if (message.equals("exit")) {
                    break;
                } else if (message.startsWith("/msg ")) {
                    // Verificar si el mensaje es un mensaje privado o
                    String[] parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        server.sendMessageToUser(parts[1], username,parts[2]);
                    }
                } else if (message.startsWith("/msggroup ")) {
                    // Verificar si el mensaje es un mensaje de grupo
                    String[] parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        server.sendMessageToGroup(parts[1], username,parts[2]);
                    }
                } else if (message.startsWith("/creategroup ")) {
                    // Verificar si el mensaje es para crear un grupo
                    String[] parts = message.split(" ", 2);
                    if (parts.length == 2) {
                        String groupName = parts[1];
                        server.createGroup(groupName);
                    }
                } else if (message.startsWith("/join ")) {
                    String[] parts = message.split(" ", 2);
                    if (parts.length == 2) {
                        String groupName = parts[1];
                        server.addUserToGroup(groupName, username); // Agregar al usuario actual al grupo
                    }
                }
                else {
                    // Broadcast del mensaje a todos los clientes
                    server.broadcastMessage(username + ": " + message, clientSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}