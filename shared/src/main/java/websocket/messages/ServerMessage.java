package websocket.messages;

import model.GameData;

import java.sql.SQLOutput;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String user;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;

    }

//    public ServerMessage(ServerMessageType type, String user) {
//        this.serverMessageType = type;
//        this.user = user;
//    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String loadGameMessage() {
        System.out.println("Implement!");
        return "";
    }

    public String errorMessage() {
        System.out.println("Implement!");
        return "";
    }

    public String notificationMessage(String username, String gameName) {
        String message = String.format("%s joined %s", username, gameName);
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
