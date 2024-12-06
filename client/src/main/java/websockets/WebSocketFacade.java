package websockets;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import websocket.commands.UserGameCommand.CommandType;

public class WebSocketFacade extends Endpoint{

    private Session session;

    public WebSocketFacade(String url) throws URISyntaxException, DeploymentException, IOException {

        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);


        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println("IMPLEMENT");
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, Integer gameID) throws IOException {
        var action = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
    }

    public void makeMove(String authToken, Integer gameID) throws IOException {
        var action = new UserGameCommand(CommandType.MAKE_MOVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
    }

    public void leave(String authToken, Integer gameID) throws IOException {
        var action = new UserGameCommand(CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
    }

    public void resign(String authToken, Integer gameID) throws IOException {
        var action = new UserGameCommand(CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
    }


}