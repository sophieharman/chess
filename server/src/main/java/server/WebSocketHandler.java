package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

public class WebSocketHandler {

    private final ConnectionsManager connections = new ConnectionsManager();

    private void join(String user, String authToken, String gameName, Integer gameID, Session session) {
        connections.add(user, session);
        String message = String.format("%s joined %s", user, gameName);
        var notification = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
        connections.broadcast();
    }
}

