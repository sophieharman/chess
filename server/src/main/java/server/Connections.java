package server;
import org.eclipse.jetty.websocket.api.Session;

public class Connections {

    public Session session;

    public Connections(Session session) {
        this.session = session;
    }

    public void send() {
        System.out.println("Implement");
    }
}
