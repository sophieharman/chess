package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.GameData;
import service.BadRequestException;
import service.Service;
import service.UnauthorizedException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
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
            connections.sendRootMessage(error, session);
        }

        // Verify GameID
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            String message =  "Error: Message HERE!";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendRootMessage(error, session);
            return;
        }

        // Register Connection
        connections.add(user, gameData.gameID(), session);


        switch (action.getCommandType()) {
            case CommandType.CONNECT -> connect(user, gameData, session);
            case CommandType.MAKE_MOVE -> makeMove(new Gson().fromJson(msg, MakeMoveCommand.class), user, gameData, session);
            case CommandType.LEAVE -> leave(user, session);
            case CommandType.RESIGN -> resign(new Gson().fromJson(msg, ResignCommand.class));
        }
    }

    public void connect(String user, GameData gameData, Session session) throws IOException {

        // Determine Player v. Observer (LATER)

        // Send Root Message
        LoadGame loadGame = new LoadGame(LOAD_GAME, new ChessGame()); // FIX THIS
        connections.sendRootMessage(loadGame, session);

        // Send Other Players Message
        String message = "Message HERE!";
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendOthersMessage(notification, user, session);
    }

    public void makeMove(MakeMoveCommand makeMove, String user, GameData gameData, Session session) throws IOException, InvalidMoveException {

        // Update Board with Chess Move
        ChessGame game = gameData.game();
        ChessMove move = makeMove.getMove();
        if (game.validMoves(move.getStartPosition()).contains(move)) {
            game.makeMove(move);
        }

        // Load Game for Game Participants
        LoadGame loadGame = new LoadGame(LOAD_GAME, game);
        connections.sendGameParticipantsMessage(loadGame, gameData.gameID());

        // Send Notification to White and Black Player
        String message = "Message HERE!";
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendGameParticipantsMessage(notification, gameData.gameID());
    }

    public void leave(String user, Session session) throws IOException {
        connections.remove(user);

        // Send Other Players Message
        String message = "Message HERE!";
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendOthersMessage(notification, user, session);
    }

    public void resign(ResignCommand resign) {
        System.out.println("Implement");
    }

}