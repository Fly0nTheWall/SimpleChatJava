import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClientController implements ConnectionListener, ChatGUIListener, Runnable {

    public static void main(String[] args) {
        ChatClientController chatClientController = new ChatClientController("127.0.0.1", 8080, "ChatClient");
        Thread chatClientThread = new Thread(chatClientController);
        chatClientThread.start();
    }

    private String chatAddress;
    private int chatPort;
    private String clientName;
    private ClientChatConnection clientChatConnection;
    private final BufferedReader clientConsoleReader = new BufferedReader(new InputStreamReader(System.in));
    volatile boolean isShutDown;
    private Thread exitThread;
    private ChatClientGUI clientGUI;

    public ChatClientController(String chatAddress, int chatPort, String clientName) {
        this.chatAddress = chatAddress;
        this.chatPort = chatPort;
        this.clientName = clientName + ": ";

        exitThread = new Thread(){
            @Override
            public void run() {
                while (true) {
                    if (ChatClientController.this.isShutDown) {
                        System.exit(1);
                        break;
                    }
                }
            }
        };
        exitThread.start();

        Thread clientGUIThread = new Thread(){
            @Override
            public void run() {
                clientGUI = new ChatClientGUI(ChatClientController.this);
            }
        };
        clientGUIThread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            clientChatConnection = new ClientChatConnection(this, chatAddress, chatPort);
            Thread connectionThread = new Thread(clientChatConnection);
            connectionThread.start();
        } catch (IOException e) {
            System.out.println(clientName + "Establishing connection exc: " + e.getMessage());
            isShutDown = true;
        }

    }

    public void run() {
        if (!isShutDown) System.out.println(clientName + "running!");
//        while (!isShutDown) {
//            try {
//                String newMessage = clientConsoleReader.readLine();
//                clientChatConnection.sendMessage(newMessage);
//            } catch (IOException e) {
//                System.out.println(clientName + "Client console exception: " + e.getMessage());
//            }
//        }
    }

    @Override
    public void onReceivingMessage(ChatConnection connection, String message) {
        if (message == null) {
            connection.disconnect();
            return;
        }
//        System.out.println(clientName + message);
        sendMessageToGUI(message);
    }

    @Override
    public void onException(ChatConnection connection, Exception e) {
        System.out.println(clientName + "connection thrown an exception: \"" + e.getMessage() + "\"");
    }

    @Override
    public void onConnection(ChatConnection connection) {
        System.out.println(clientName + "connection is ready!");
    }

    @Override
    public void onDisconnection(ChatConnection connection) {
        System.out.println(clientName + "disconnected!");
        isShutDown = true;
        try {
            clientConsoleReader.close();
        } catch (IOException e) {
            System.out.println(clientName + "closing client console exc: " + e.getMessage());
        }
    }

    @Override
    public void getMessageFromGUI(String message) {
        clientChatConnection.sendMessage(message);
    }

    @Override
    public void sendMessageToGUI(String message) {
            clientGUI.inputList.add(message);
            clientGUI.outputList.add("");
    }

}
