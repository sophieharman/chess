package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsManager {

    public final ConcurrentHashMap<String, Connections> connections = new ConcurrentHashMap<>();

    public void add(String user, Session session) {
        var connection = new Connections(session);
        connections.put(user, connection);
    }

    public void remove(String user) {
        connections.remove(user);
    }

    public void broadcast() {
        for(var c: connections.values()) {
            c.send();
        }
    }
}
