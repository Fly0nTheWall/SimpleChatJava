import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class ChatConnection {
    ConnectionListener connectionListener;
    Socket connectionSocket;
    BufferedReader receiveStream;
    BufferedWriter sendStream;
    String connectionName;
    boolean isInterrupted;
}
