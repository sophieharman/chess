package server;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;

public class ConnectionsManager {

    public final ConcurrentHashMap<String, Connections> connections = new ConcurrentHashMap<>();

    public void add(String user, Session session) {
        var connection = new Connections(session, user);
        connections.put(user, connection);
    }

    public void remove(String user) {
        connections.remove(user);
    }

    public void sendRootMessage(ServerMessage.ServerMessageType msgType, String username, GameData game, Session session) {
        ServerMessage serverMessage = new ServerMessage(msgType);
        serverMessage.notificationMessage("You", game.gameName());

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.username.equals(username)) {
                    try {
                        c.send(new Gson().toJson(serverMessage));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void sendOthersMessage(ServerMessage.ServerMessageType msgType, String username, GameData game, Session session) throws IOException {
        ServerMessage serverMessage = new ServerMessage(msgType);
        serverMessage.notificationMessage(username, game.gameName());

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(username)) {
                    try {
                        c.send(new Gson().toJson(serverMessage));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
