package websockets;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.Notification;

public class WebSocketFacade extends Endpoint{

    private Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws URISyntaxException, DeploymentException, IOException {

        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");
        this.notificationHandler = notificationHandler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);


        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                notificationHandler.notify(message);
                notificationHandler.load(message);
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

    public void makeMove(String authToken, Integer gameID, ChessMove move, ChessGame game) throws IOException {
        var action = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move, game);
//        var action = new UserGameCommand(CommandType.MAKE_MOVE, authToken, gameID);
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
