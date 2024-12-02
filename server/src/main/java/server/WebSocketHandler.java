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
import websocket.messages.ErrorMessage;
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
            String message =  "Message HERE!";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendRootMessage(error, user, session);
        }

        // Register Connection
        connections.add(user, session);

        // Verify GameID
        GameData gameData = gameDAO.getGame(gameID);

        if (gameData == null) {
            String message =  "Error: Message HERE!";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendRootMessage(error, user, session);
            return;
        }

        switch (action.getCommandType()) {
            case CommandType.CONNECT -> connect(user, gameData, session);
            case CommandType.MAKE_MOVE -> makeMove(user, gameData, session);
            case CommandType.LEAVE -> leave();
            case CommandType.RESIGN -> resign();
        }
    }

    public void connect(String user, GameData gameData, Session session) throws IOException {

        // Determine Player v. Observer (LATER)

        // Send Root Message
        LoadGame loadGame = new LoadGame(LOAD_GAME, new ChessGame()); // FIX THIS
        connections.sendRootMessage(loadGame, user, session);

        // Send Other Players Message
        String message = "Message HERE!";
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendOthersMessage(notification, user, session);
    }

    public void makeMove(String user, GameData gameData, Session session) throws IOException {

        // Load Game for White and Black
        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        LoadGame loadGame = new LoadGame(LOAD_GAME, new ChessGame()); // FIX THIS
        connections.sendRootMessage(loadGame, whiteUser, session);
        connections.sendRootMessage(loadGame, blackUser, session);

        // Load Game for Observers

        // Send Notification to White and Black Player
        String message = "Message HERE!";
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendRootMessage(notification, user, session);
        connections.sendRootMessage(notification, user, session);

        // Send Notification to Observer

    }

    public void leave() {
        System.out.println("Implement");
    }

    public void resign() {
        System.out.println("Implement");
    }

}