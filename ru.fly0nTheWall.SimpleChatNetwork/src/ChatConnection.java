import java.io.*;
import java.net.Socket;

public class ChatConnection implements Runnable{

    private ConnectionListener connectionListener;
    private Socket connectionSocket;
    private BufferedReader receiveStream;
    private BufferedWriter sendStream;
    private String connectionName;
    private boolean isInterrupted;

    ChatConnection(ConnectionListener listener, Socket socket) throws IOException{
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

    public void sendMessage(String message) {
        try {
            sendStream.write(message + "\r\n");
            sendStream.flush();
        } catch (IOException e) {
            connectionListener.onException(this, e);
        }
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

    public void disconnect() {
        isInterrupted = true;
        try {
            receiveStream.close();
            sendStream.close();
            connectionSocket.close();
        } catch (IOException e) {
            connectionListener.onException(this, e);
        }
    }

    public String getConnectionName() {
        return connectionName;
    }
}
