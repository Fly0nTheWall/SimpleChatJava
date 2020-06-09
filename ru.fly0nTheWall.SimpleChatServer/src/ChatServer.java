import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer implements ConnectionListener, Runnable {

    public static void main(String[] args) {
        ChatServer server = new ChatServer("127.0.0.1", 100, 8080, "SERVER");
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    private final String serverIpAddress;
    private final int serverPort;
    private final int serverBacklog;
    private ServerSocket serverSocket;
    private final ArrayList<ChatConnection> connectionsList = new ArrayList<>();
    private final String serverName;
    private boolean isShutDown;

    public ChatServer(String ipAddress, int backlog, int port, String name) {
        serverIpAddress = ipAddress;
        serverPort = port;
        serverBacklog = backlog;
        serverName = name + ": ";

        try {
            serverSocket = new ServerSocket(serverPort, serverBacklog, InetAddress.getByName(serverIpAddress));
        } catch (IOException e) {
            System.out.println(serverName + "serverSocketExc: " + e.getMessage());
            isShutDown = true;
        }
    }

    public void run() {
        if (!isShutDown) System.out.println(serverName + "running!");
        while (!isShutDown) {
            try {
                Socket newSocket = serverSocket.accept();
                ChatConnection newChatConnection = new ChatConnection(this, newSocket);
                connectionsList.add(newChatConnection);
                Thread newChatConnectionThread = new Thread(newChatConnection);
                newChatConnectionThread.start();
            } catch (IOException e) {
                System.out.println(serverName + "adding new connection Exc: ");
            }
        }
    }

    private void sendAll(ChatConnection connection, String message) {
        for (ChatConnection chatConnection : connectionsList) {
            if (!(chatConnection == connection)) {
                chatConnection.sendMessage(message);
            }
        }
    }

    private void sendAll(String message) {
        for (ChatConnection chatConnection : connectionsList) {
            chatConnection.sendMessage(message);
        }
    }

    private void disconnectConnection(ChatConnection connection) {
        connection.disconnect();
    }

    @Override
    public void onReceivingMessage(ChatConnection connection, String message) {
        System.out.println(serverName + connection.getConnectionName() + " sent a message: \"" + message + "\"");
        if (message.equals("null")) onDisconnection(connection);
        if (message.equals("DISCONNECT")) disconnectConnection(connection);
        sendAll(connection, connection.getConnectionName() + ": " + message);
    }

    @Override
    public void onException(ChatConnection connection, Exception e) {
        System.out.println(serverName + connection.getConnectionName() + "connection thrown an exception: \"" + e.getMessage() + "\"");
    }

    @Override
    public void onConnection(ChatConnection connection) {
        System.out.println(serverName + connection.getConnectionName() + " connected!");
        sendAll(connection.getConnectionName() + " connected!");
    }

    @Override
    public void onDisconnection(ChatConnection connection) {
        System.out.println(serverName + connection.getConnectionName() + " disconnected!");
        connectionsList.remove(connection);
        sendAll(connection.getConnectionName() + " disconnected!");
    }
}
