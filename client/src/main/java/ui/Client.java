package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websockets.NotificationHandler;
import websockets.WebSocketFacade;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Client implements NotificationHandler {

    ChessGame game;
    private String playerColor;
    private WebSocketFacade ws;
    BoardDisplay display = new BoardDisplay();
    ChessBoard board = new ChessBoard();
    boolean listGamesCalled = false;
    GameIDs ids = new GameIDs();
    GameInfo gameInfo = new GameInfo();
    private String username;
    private String authToken;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private GameState gameState = GameState.OUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.board = board;
        this.serverUrl = serverUrl;
        this.playerColor = null;
        this.game = new ChessGame();


        try {
            new Repl(serverUrl, this).run();
        } catch (ResponseException e) {
            System.out.println("Error: ");
        }
    }



    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "createGame" -> createGame(params);
                case "listGames" -> listGames(params);
                case "joinGame" -> joinGame(params);
                case "observeGame" -> observeGame(params);
                case "redrawBoard" -> redrawBoard(params);
                case "leave" -> leave(params);
                case "makeMove" -> makeMove(params);
                case "resign" -> resign(params);
                case "showLegalMoves" -> showLegalMoves(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException | IOException | DeploymentException | URISyntaxException | InvalidMoveException e) {
            return e.getMessage();
        }

    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            UserData userInfo = new UserData(params[0], params[1], params[2]);
            RegisterResult result = server.register(userInfo);
            authToken = result.authToken();
            return String.format("You have successfully registered as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (state == State.SIGNEDIN) {
            throw new ResponseException(400, "You must be logged out in order to log in.");
        }
        if (params.length == 2) {
            UserData userInfo = new UserData(params[0], params[1], null);
            LoginResult result = server.login(userInfo, params[1]);
            state = State.SIGNEDIN;
            authToken = result.authToken();
            username = params[0];
            return String.format("You have successfully logged in as %s.", userInfo.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logout(String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must be logged in in order to log out.");
        }
        if (params.length == 0) {
            server.logout(authToken);
            state = State.SIGNEDOUT;
            return "You have successfully logged out";
        }
        throw new ResponseException(400, "Expected: logout <>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateGameResult game = server.createGame(params[0], authToken);

            gameInfo.addInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.game());

            // Reset Board
            board.resetBoard();

            return String.format("New Game Created: %s", params[0]);
        }
        throw new ResponseException(400, "Expected: <GameName>");
    }

    public String listGames(String... params) throws ResponseException {
        int idCount = 0;
        if (params.length == 0) {
            ListGamesResult result = server.listGames(authToken);

            System.out.printf("%-10s %-10s %-10s %-10s%n", "GameID", "GameName", "White", "Black");
            for(GameData game: result.games()) {

                // Record Game IDs in Client
                idCount++;
                ids.addID(game.gameID(), idCount);

                // Record Game Information in Client
                gameInfo.addInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game);
                listGamesCalled = true;

                System.out.printf("%-10s %-10s %-10s %-10s%n", idCount, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
            return "";
        }
        throw new ResponseException(400, "Expected: <>");
    }

    public String joinGame(Object... params) throws ResponseException, IOException, DeploymentException, URISyntaxException {
        if (!listGamesCalled) {
            throw new ResponseException(500, "Error: Please list games before attempting to join a game");
        }
        if (params.length == 2) {

            // Verify 2nd Parameter is an Integer
            try {
                Integer.valueOf(params[1].toString());
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Error: GameID must be an Integer");
            }

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            // Unpack Parameters
            String color = params[0].toString().toUpperCase();
            Integer gameID = Integer.valueOf(params[1].toString());

            // Grab Game Data
            String whiteUser = gameInfo.getWhiteUser(primaryID);
            String blackUser = gameInfo.getBlackUser(primaryID);
            GameData game = gameInfo.getGame(primaryID);

            // Verify Position is Open
            if (color.equals("WHITE") && whiteUser != null || color.equals("BLACK") && blackUser != null) {
                throw new ResponseException(400, "Error: Player Color Occupied");
            }

            server.joinGame(color, authToken, primaryID);
            gameState = GameState.IN;

            ws = new WebSocketFacade(serverUrl, this);
            ws.connect(authToken, gameID);

            if (color.equals("WHITE")) {
                playerColor = "white";
                display.main(game.game().getBoard(),"white", false, null);
            }
            if (color.equals("BLACK")) {
                playerColor = "black";
                display.main(game.game().getBoard(), "black", false, null);
            }

            return "You have successfully joined Game " + gameID;
        }
        throw new ResponseException(400, "Expected: <PlayerColor> <GameID>");
    }

    public String observeGame(String... params) throws ResponseException, DeploymentException, URISyntaxException, IOException {
        if (params.length == 1) {

            // Verify Parameter is an Integer
            try {
                Integer.valueOf(params[0].toString());
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Error: GameID must be an Integer");
            }

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[0].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }
            GameData game = gameInfo.getGame(primaryID);

            ws = new WebSocketFacade(serverUrl, this);
            ws.connect(authToken, primaryID);
            gameState = GameState.IN;

            display.main(game.game().getBoard(),"white", false, null);
            return "You have successfully joined the game as an observer";
        }
        throw new ResponseException(400, "Expected: <GameID>");
    }

    public String redrawBoard(String... params) throws ResponseException {
        if (params.length == 2) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }
            GameData game = gameInfo.getGame(primaryID);

            // Unpack Parameters
            String playerColor = params[0].toString().toUpperCase();
            Integer gameID = Integer.valueOf(params[1].toString());

            // Redraw Board
            if (playerColor.equals("white")) {
                display.main(game.game().getBoard(),"black", false, null);
            }
            if (playerColor.equals("black")) {
                display.main(game.game().getBoard(), "white", false, null);
            }
            return "";
        }
        throw new ResponseException(400, "Expected: <PlayerColor> <GameID>");
    }

    public String leave(Object... params) throws DeploymentException, URISyntaxException, IOException, ResponseException {
        if (params.length == 2) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            String user = params[0].toString();
;

            ws = new WebSocketFacade(serverUrl, this);
            ws.leave(user, primaryID);

            gameState = GameState.OUT;
            return "You've left the game.";
        }
        throw new ResponseException(400, "Expected: <Username> <GameID>");
    }

    public String makeMove(String... params) throws ResponseException, DeploymentException, URISyntaxException, IOException, InvalidMoveException {
        if (params.length == 3) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[0].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            String start = params[1].toString();
            String end = params[2].toString();

            ChessGame game = gameInfo.getGame(primaryID).game();

            // Make Move
            MoveMapping moveMapping = new MoveMapping(start, end, playerColor, game.getBoard());
            ChessMove move = moveMapping.convertToMove();
            game.makeMove(move);

            ws = new WebSocketFacade(serverUrl, this);
            ws.makeMove(authToken, primaryID, move, game);
            return "";
        }
        throw new ResponseException(400, "Expected: <GameID> <<a-h><1-8>> <<a-h><1-8>>");
    }

    public String resign(String... params) throws DeploymentException, URISyntaxException, IOException, ResponseException {

        if (params.length == 2) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            ws = new WebSocketFacade(serverUrl, this);
            ws.leave(authToken, primaryID);

            gameState = GameState.OUT;
            return "Game Over. You’ve forfeited the game.";
        }
        throw new ResponseException(400, "Expected: <GameID>");
    }

    public String showLegalMoves(String... params) throws ResponseException {
        if (params.length == 1) {

            // Verify Parameter
            String start = params[1].toString();
            MoveMapping map =  new MoveMapping(start, null, null, null);
            ChessPosition startPosition = map.convertToPosition(start);
            Collection<ChessMove> validMoves = game.validMoves(startPosition);

            // Redraw Board
            if (playerColor.equals("white")) {
                display.main(game.getBoard(),"black", true, validMoves);
            }
            if (playerColor.equals("black")) {
                display.main(game.getBoard(), "white", true, validMoves);
            }

            return "";



        }
        throw new ResponseException(400, "Expected: <StartPosition>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        else {
            if (gameState == GameState.IN) {
                return """
                - help
                - redrawBoard <PlayerColor> <GameID>
                - highlightLegalMoves <<a-h><1-8>>
                - makeMove <GameID> <<a-h><1-8>> <<a-h><1-8>>
                - leave <Username> <GameID>
                - resign
                """;
            }
            return """
                - listGames
                - joinGame <PlayerColor> <GameID>
                - observeGame <GameID>
                - createGame <GameName>
                - logout
                """;
        }

    }

    @Override
    public void load(String notification){
        JsonObject jsonObject = JsonParser.parseString(notification).getAsJsonObject();

        if (jsonObject.has("game")) {
            JsonObject gameJson = jsonObject.getAsJsonObject("game");
            ChessGame gameUpdated = new Gson().fromJson(gameJson, ChessGame.class);

            // Update Game Attribute
            game = gameUpdated;

            // Display Board
            if (playerColor.equals("white")) {
                display.main(game.getBoard(), "black", false, null);
            }
            if (playerColor.equals("black")) {
                display.main(game.getBoard(), "white", false, null);
            }

        }
    }

    @Override
    public void notify(String notification) {
        JsonObject jsonObject = JsonParser.parseString(notification).getAsJsonObject();
        if (jsonObject.has("message")) {
            String msg = jsonObject.get("message").getAsString();
            System.out.println(msg);
        }
    }
}
