import java.io.*;
import java.net.Socket;

public class ServerChatConnection extends ChatConnection implements Runnable{

    private String connectionName;

    ServerChatConnection(ConnectionListener listener, Socket socket) throws IOException{
        connectionListener = listener;
        connectionSocket = socket;

        receiveStream = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        sendStream = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
    }

    public void run() {
        introduction();
        connectionListener.onConnection(this);

        while (!isInterrupted) {
            try {
                String receivedMessage = receiveStream.readLine();
                connectionListener.onReceivingMessage(this, receivedMessage);
            } catch (IOException e) {
                connectionListener.onException(this, e);
            }
        }
        connectionListener.onDisconnection(this);
    }



    private void introduction() {
        sendMessage("Enter your name, please!");
        try {
            connectionName = receiveStream.readLine();
        } catch (IOException e) {
            connectionListener.onException(this, e);
        }
        sendMessage("Welcome to SimpleChat, " + connectionName + "!");
        sendMessage("To disconnect enter \"DISCONNECT\"!");
    }

    public String getConnectionName() {
        return connectionName;
    }

}
