package websocket.messages;

import websocket.commands.UserGameCommand;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsManager {

    public final ConcurrentHashMap<String, Connections> connections = new ConcurrentHashMap<>();

    public void add(String user) {
        var connection = new Connections();
        connections.put(user, connection);
    }

    public void remove(String user) {
        connections.remove(user);
    }

    public void broadcast(UserGameCommand notification) {
        for(var c: connections.values()) {
            c.send();
        }
    }
}
