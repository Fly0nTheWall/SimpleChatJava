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
    private final ArrayList<ServerChatConnection> connectionsList = new ArrayList<>();
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
                ServerChatConnection newServerChatConnection = new ServerChatConnection(this, newSocket);
                connectionsList.add(newServerChatConnection);
                Thread newChatConnectionThread = new Thread(newServerChatConnection);
                newChatConnectionThread.start();
            } catch (IOException e) {
                System.out.println(serverName + "adding new connection Exc: ");
            }
        }
    }

    private void sendAll(ChatConnection connection, String message) {
        for (ServerChatConnection serverChatConnection : connectionsList) {
            if (!(serverChatConnection == connection)) {
                serverChatConnection.sendMessage(message);
            }
        }
    }

    private void sendAll(String message) {
        for (ChatConnection ChatConnection : connectionsList) {
            ChatConnection.sendMessage(message);
        }
    }

    private void disconnectConnection(ChatConnection connection) {
        connection.disconnect();
    }

    @Override
    public synchronized void onReceivingMessage(ChatConnection connection, String message) {
        ServerChatConnection serverConnection = (ServerChatConnection)connection;
        System.out.println(serverName + serverConnection.getConnectionName() + " sent a message: \"" + message + "\"");
        if (message.equals("null")) onDisconnection(connection);
        if (message.equals("DISCONNECT")) disconnectConnection(connection);
        sendAll(connection, serverConnection.getConnectionName() + ": " + message);
    }

    @Override
    public synchronized void onException(ChatConnection connection, Exception e) {
        ServerChatConnection serverConnection = (ServerChatConnection)connection;
        System.out.println(serverName + serverConnection.getConnectionName() + " connection thrown an exception: \"" + e.getMessage() + "\"");
        if (e.getMessage().equals("Connection reset"))
        {
            disconnectConnection(connection);
        }
    }

    @Override
    public synchronized void onConnection(ChatConnection connection) {
        ServerChatConnection serverConnection = (ServerChatConnection)connection;
        System.out.println(serverName + serverConnection.getConnectionName() + " connected!");
        sendAll(serverConnection.getConnectionName() + " connected!");
    }

    @Override
    public synchronized void onDisconnection(ChatConnection connection) {
        ServerChatConnection serverConnection = (ServerChatConnection)connection;
        System.out.println(serverName + serverConnection.getConnectionName() + " disconnected!");
        connectionsList.remove(connection);
        sendAll(serverConnection.getConnectionName() + " disconnected!");
    }
}
