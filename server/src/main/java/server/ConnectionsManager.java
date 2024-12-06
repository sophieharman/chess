package server;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;

public class ConnectionsManager {

    public final ConcurrentHashMap<String, Connections> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, HashSet<String>> gameConnections = new ConcurrentHashMap<>();

    public void add(String user, Integer gameID, Session session) {
        var connection = new Connections(session, user);
        connections.put(user, connection);

        // Update Game Connections
        HashSet<String> players = gameConnections.get(gameID);
        if (players == null) {
            players = new HashSet<>();
        }
        players.add(user);
        gameConnections.put(gameID, players);

    }

    public void remove(String user) {
        connections.remove(user);
    }

    public void sendRootMessage(ServerMessage msgType, Session session) throws IOException {
        session.getRemote().sendString(new Gson().toJson(msgType));
    }

    public void sendIndividualMessage(ServerMessage msgType, String username, Session session) {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.username.equals(username)) {
                    try {
                        c.send(new Gson().toJson(msgType));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void sendGameParticipantsMessage(ServerMessage msgType, Integer gameID, String playerMoved) {

        HashSet<String> players = gameConnections.get(gameID);

        for (var c : connections.values()) {
            if (c.session.isOpen()) {

                if (!players.contains(c.username)) {
                    continue;
                }

                try {
                    if (!Objects.equals(c.username, playerMoved)){
                    c.send(new Gson().toJson(msgType));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

}
