package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionsManager connections = new ConnectionsManager();

    private void join(String user, String authToken, String gameName, Integer gameID, Session session) throws IOException {
        connections.add(user, session);
        String message = String.format("%s joined %s", user, gameName);
//        var notification = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(notification);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception{
        System.out.println(msg);
    }
}



