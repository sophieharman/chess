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

    public void addGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public Collection<GameData> listGames() {
        // Return a Collection instead of map (for future phases) (games.values())
        return games.values();
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

    public void removeGame(Integer gameID) {
        games.remove(gameID);
    }

    public void clear(){
        games.clear();
    }

}
