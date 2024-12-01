package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private String message;
    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }
}
