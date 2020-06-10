import java.io.*;
import java.net.Socket;

public class ClientChatConnection extends ChatConnection{

    ClientChatConnection(ConnectionListener listener,String chatAddress, int port) throws IOException{
        connectionListener = listener;
        connectionSocket = new Socket(chatAddress, port);

        receiveStream = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        sendStream = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
    }
}
