public interface ConnectionListener {
    void onReceivingMessage(ChatConnection connection, String message);
    void onException(ChatConnection connection, Exception e);
    void onConnection(ChatConnection connection);
    void onDisconnection(ChatConnection connection);
}
