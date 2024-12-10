import server.Server;

public class Main {
    public static void main(String[] args) {

        var port = 8080;

        var server = new Server();
        server.run(port);
    }
}