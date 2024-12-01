package server;

import chess.ChessGame;
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
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType;
import static websocket.messages.ServerMessage.ServerMessageType.*;

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
            // Error Message!!!!!
//            connections.sendRootMessage(ERROR, user, gameData, session);
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

    public void connect(String user, GameData gameData, Session session) throws IOException {

        // Determine Player v. Observer (LATER)

        // Register Connection
        connections.add(user, session);

        // Send Root Message
        LoadGame loadGame = new LoadGame(LOAD_GAME, new ChessGame()); // FIX THIS
        connections.sendRootMessage(loadGame, user, gameData, session);

        // Send Other Players Message
        String message = "Message HERE!";
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendOthersMessage(notification, user, gameData, session);
    }

}