public interface ConnectionListener {
    void onReceivingMessage(ServerChatConnection connection, String message);
    void onException(ServerChatConnection connection, Exception e);
    void onConnection(ServerChatConnection connection);
    void onDisconnection(ServerChatConnection connection);
}
