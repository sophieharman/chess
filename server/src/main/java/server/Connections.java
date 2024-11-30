package server;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connections {

    public Session session;
    public String username;

    public Connections(Session session, String username) {

        this.session = session;
        this.username = username;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
