package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import service.Service;
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
import static websocket.messages.ServerMessage.ServerMessageType.*;
import java.io.IOException;
import java.util.Objects;


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
            String message =  "Error: Unauthorized Access";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendRootMessage(error, session);
        }

        // Verify GameID
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            String message =  "Error: Invalid GameID";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendRootMessage(error, session);
            return;
        }

        // Register Connection
        connections.add(user, gameData.gameID(), session);


        switch (action.getCommandType()) {
            case CommandType.CONNECT -> connect(user, gameData, session);
            case CommandType.MAKE_MOVE -> makeMove(new Gson().fromJson(msg, MakeMoveCommand.class), user, gameData, session);
            case CommandType.LEAVE -> leave(user, gameData, session);
            case CommandType.RESIGN -> resign(new Gson().fromJson(msg, ResignCommand.class), user, session);
        }
    }

    public void connect(String user, GameData gameData, Session session) throws IOException, DataAccessException {

        // Send Root Message
        LoadGame loadGame = new LoadGame(LOAD_GAME, new ChessGame());
        connections.sendRootMessage(loadGame, session);

        String color;
        if (user.equals(gameData.whiteUsername())) {
            color = "white";
        } else if (user.equals(gameData.blackUsername())) {
            color = "black";
        } else {
            color = "an observer";
        }

        // Update Game Data
        gameDAO.updateGame(gameData);

        // Send Other Players Message
        String message = String.format("%s connected to %s as %s.", user, gameData.gameName(), color);
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendGameParticipantsMessage(notification, gameData.gameID(), user);
    }

    public boolean player(String user, GameData gameData) {
        // Determine whether User is a Player or Observer
        return Objects.equals(user, gameData.whiteUsername()) || Objects.equals(user, gameData.blackUsername());
    }

    public void makeMove(MakeMoveCommand makeMove, String user, GameData gameData,
                         Session session) throws IOException, InvalidMoveException, DataAccessException {

        // Update Board with Chess Move
        ChessGame game = gameData.game();
        ChessMove move = makeMove.getMove();

        // Verify User is a Player and Not an Observer
        if (!player(user, gameData)) {
            String message = "Error: Observer not Authorized to Make Move";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendIndividualMessage(error, user, session);
            return;
        }

        // Determine Player who Moved
        String playerMoved;
        String wrongPlayer;
        ChessGame.TeamColor color = game.getTeamTurn();
        if (color == ChessGame.TeamColor.WHITE) {
            playerMoved = gameData.whiteUsername();
            wrongPlayer = gameData.blackUsername();
        }
        else {
            playerMoved = gameData.blackUsername();
            wrongPlayer = gameData.whiteUsername();
        }

        if (!Objects.equals(playerMoved, user)) {
            String message = "Error: Player Move Out of Turn";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendIndividualMessage(error, wrongPlayer, session);
            return;
        }

        if (!game.gameOver()) {

            if (game.validMoves(move.getStartPosition()).contains(move)) {
                game.makeMove(move);
                gameDAO.updateGame(gameData);
            }
            else {
                String message = "Error: Invalid Move";
                ErrorMessage error = new ErrorMessage(ERROR, message);
                connections.sendIndividualMessage(error, playerMoved, session);
                return;
            }

            // Load Game for Game Participants
            LoadGame loadGame = new LoadGame(LOAD_GAME, game);
            connections.sendGameParticipantsMessage(loadGame, gameData.gameID(),null);

            // Send Notification to Game Participants
            String message = String.format("%s made move from %s to %s", user, "PLACE1", "PLACE2");
            Notification notification = new Notification(NOTIFICATION, message);
            connections.sendGameParticipantsMessage(notification, gameData.gameID(), playerMoved);

        }
        else {
            String message = "Error: Game Over -- No More Moves Can Be Made";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendIndividualMessage(error, playerMoved, session);
        }

    }

    public void leave(String user, GameData gameData, Session session) throws IOException, DataAccessException {

        connections.remove(user);

        Integer gameID = gameData.gameID();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();


        GameData updatedGameData;
        if (Objects.equals(user, gameData.whiteUsername()) || Objects.equals(user, gameData.blackUsername())) {
            if (Objects.equals(user, gameData.whiteUsername())) {
                updatedGameData = new GameData(gameID, null, gameData.blackUsername(), gameName, game);
            } else {
                updatedGameData = new GameData(gameID, gameData.whiteUsername(), null, gameName, game);
            }

            gameDAO.updateGame(updatedGameData);
        }
        else {
            String message = "Error: You Must Be Connected to Game as Black or White to Leave";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendIndividualMessage(error, user, session);
        }


        // Send Other Players Message
        String message = String.format("%s left the game", user);
        Notification notification = new Notification(NOTIFICATION, message);
        connections.sendGameParticipantsMessage(notification, gameData.gameID(), user);
    }

    public void resign(ResignCommand resign, String user, Session session) throws DataAccessException, IOException {

        // Grab Game
        Integer gameID = resign.getGameID();
        GameData gameData = gameDAO.getGame(gameID);

        // Verify User is a Player and Not an Observer
        if (!player(user, gameData)) {
            String message = "Error: Must be Player to Resign";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendIndividualMessage(error, user, session);
            return;
        }

        if (!gameData.game().gameOver()) {

            // Resign
            gameData.game().resign();

            // Update Database
            gameDAO.updateGame(gameData);

            // Send Notification Message to Game Participants
            String message = String.format("%s resigned", user);
            Notification notification = new Notification(NOTIFICATION, message);
            connections.sendGameParticipantsMessage(notification, gameData.gameID(), null);

        }
        else {
            String message = "Error: Cannot Resign -- Game is Over";
            ErrorMessage error = new ErrorMessage(ERROR, message);
            connections.sendRootMessage(error, session);

        }

    }

}