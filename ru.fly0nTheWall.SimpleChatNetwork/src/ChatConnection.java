import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class ChatConnection implements Runnable{
    ConnectionListener connectionListener;
    Socket connectionSocket;
    BufferedReader receiveStream;
    BufferedWriter sendStream;
    boolean isInterrupted;

    public void run() {
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

}
