package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.GameData;
import service.BadRequestException;
import service.Service;
import service.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;
import java.io.IOException;
import java.util.Map;



@WebSocket
public class WebSocketHandler {

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;
    private Service service;
    private final ConnectionsManager connections = new ConnectionsManager();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception{

        UserGameCommand action = new Gson().fromJson(msg, UserGameCommand.class);

        // Grab Relevant Information
        String authToken = action.getAuthToken();
        Integer gameID = action.getGameID();
        String user = authDAO.getUser(authToken);

        // Verify Authentication
        if (user == null) {
            throw new UnauthorizedException();
        }

        // Verify GameID
        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new BadRequestException();
        }

        String gameName = game.gameName();


        switch (action.getCommandType()) {
            case CommandType.CONNECT -> connect(user, game, session);
        }
    }

    public void connect(String user, GameData game, Session session) throws IOException {

        // Determine Player v. Observer (LATER)


        // Register Connection
        connections.add(user, session);

        // Send Root Message
        connections.sendRootMessage(LOAD_GAME, user, game, session);

        // Send Other Players Message
        connections.sendOthersMessage(NOTIFICATION, user, game, session);
    }

}