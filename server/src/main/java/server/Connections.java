package server;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connections {

    public Session session;

    public Connections(Session session) {
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
