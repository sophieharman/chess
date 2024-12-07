package ui;

import chess.ChessBoard;
import exception.ResponseException;
import model.*;
import websockets.WebSocketFacade;

import java.io.IOException;
import java.util.*;

public class Client {

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

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;

        try {
            new Repl(serverUrl).run();
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
//                case "joinGame" -> joinGame(params);
                case "observeGame" -> observeGame(params);
                case "redrawBoard" -> redrawBoard(params);
                case "leave" -> leave(params);
                case "makeMove" -> makeMove(params);
                case "resign" -> resign(params);
                case "showLegalMoves" -> showLegalMoves(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
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

            gameInfo.addInfo(game.gameID(), game.whiteUsername(), game.blackUsername());

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
                gameInfo.addInfo(game.gameID(), game.whiteUsername(), game.blackUsername());
                listGamesCalled = true;

                System.out.printf("%-10s %-10s %-10s %-10s%n", idCount, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
            return "";
        }
        throw new ResponseException(400, "Expected: <>");
    }

    public String joinGame(Object... params) throws ResponseException, IOException {
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
            String playerColor = params[0].toString().toUpperCase();
            Integer gameID = Integer.valueOf(params[1].toString());

            // Grab Game Data
            String whiteUser = gameInfo.getWhiteUser(primaryID);
            String blackUser = gameInfo.getBlackUser(primaryID);

            // Verify Position is Open
            if (playerColor.equals("WHITE") && whiteUser != null || playerColor.equals("BLACK") && blackUser != null) {
                throw new ResponseException(400, "Error: Player Color Occupied");
            }

            server.joinGame(playerColor, authToken, primaryID);

//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.connect(authToken, gameID);

            display.main(board, "black");
            display.main(board,"white");
            return "You have successfully joined Game " + gameID;
        }
        throw new ResponseException(400, "Expected: <PlayerColor> <GameID>");
    }

    public String observeGame(String... params) throws ResponseException {
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
            board.resetBoard();
            display.main(board, "black");
            display.main(board,"white");
            return "You have successfully joined the game as an observer";
        }
        throw new ResponseException(400, "Expected: <GameID>");
    }

    public String redrawBoard(String... params) {
        System.out.println("Implement");
        return "";
    }

    public String leave(String... params) {
        System.out.println("Implement");
        return "You've left the game.";
    }

    public String makeMove(String... params) {
        System.out.println("Implement");
        return "";
    }

    public String resign(String... params) {
        System.out.println("Implement");
        return "Game Over. You’ve forfeited the game.";
    }

    public String showLegalMoves(String... params) {
        System.out.println("Implement");
        return "";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
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
