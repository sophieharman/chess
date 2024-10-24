package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO{

    final private HashMap<Integer, GameData> games = new HashMap<>();

    public Integer createGame(String gameName) {

        // Generate Game ID
        Random random = new Random();
        Integer gameID = random.nextInt(100_000_000, 1_000_000_000);

        // Create Game
        GameData game = new GameData(gameID, null, null, gameName, null);

        // Add Game to Memory
        games.put(gameID, game);

        return gameID;
    }

    public HashMap<Integer, GameData> listGames() {
        return games;
    }

    public GameData getGame(Integer gameID) {

        // Iterate through Games
        for (Map.Entry<Integer, GameData> game : games.entrySet()) {

            // Verify GameID Matches Given ID
            GameData gameInfo = game.getValue();
            if(gameInfo.gameID() == gameID)
            {
                return gameInfo;
            }
        }
        return null;
    }

    public void joinGame(String playerColor, String username, String authToken, Integer gameID) {


        GameData gameInfo = games.get(gameID);

        String whiteUsername = gameInfo.whiteUsername();
        String blackUsername = gameInfo.blackUsername();
        String gameName = gameInfo.gameName();
        ChessGame game = gameInfo.game();

        // Update White/Black Usernames
        if(Objects.equals(playerColor, "WHITE") && whiteUsername == null) {
            whiteUsername = username;
        }
        if(Objects.equals(playerColor, "BLACK") && blackUsername == null) {
            blackUsername = username;
        }

        // Delete Game
        games.remove(gameID);

        // Add Updated Game
         GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public void clear(){
        games.clear();
    }

}
