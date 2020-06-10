import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClientController implements ConnectionListener, Runnable{

    public static void main(String[] args) {
        ChatClientController chatClientController = new ChatClientController("127.0.0.1", 8080, "ALICE");
        Thread chatClientThread = new Thread(chatClientController);
        chatClientThread.start();
    }

    private String chatAddress;
    private int chatPort;
    private String clientName;
    private ServerChatConnection clientChatConnection;
    private BufferedReader clientConsoleReader = new BufferedReader(new InputStreamReader(System.in));
    private boolean isInterrupted;

    public ChatClientController(String chatAddress, int chatPort, String clientName) {
        this.chatAddress = chatAddress;
        this.chatPort = chatPort;
        this.clientName = clientName + " CLIENT: ";

        try {
            clientChatConnection = new ClientChatConnection(this, chatAddress, chatPort);
            Thread connectionThread = new Thread(clientChatConnection);
            connectionThread.start();
        } catch (IOException e) {
            System.out.println(clientName + "Establishing connection exc: " + e.getMessage());
            isInterrupted = true;
        }
    }

    public void run() {
        if (!isInterrupted) System.out.println(clientName + "running!");
        while (!isInterrupted) {
            try {
                String newMessage = clientConsoleReader.readLine();
                clientChatConnection.sendMessage(newMessage);
            } catch (IOException e) {
                System.out.println(clientName + "Client console exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onReceivingMessage(ServerChatConnection connection, String message) {
        System.out.println(connection.getConnectionName() + ": " + message);
    }

    @Override
    public void onException(ServerChatConnection connection, Exception e) {
        System.out.println(clientName + connection.getConnectionName() + "connection thrown an exception: " + e.getMessage());
    }

    @Override
    public void onConnection(ServerChatConnection connection) {
        System.out.println(clientName + connection.getConnectionName() + " connection is ready!");
    }

    @Override
    public void onDisconnection(ServerChatConnection connection) {
        System.out.println(clientName + connection.getConnectionName() + " connection is disconnected!");
        isInterrupted = true;
    }
}
