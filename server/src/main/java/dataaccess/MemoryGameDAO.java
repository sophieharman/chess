package dataaccess;

import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO{

    final private HashMap<String, GameData> games = new HashMap<>();

    public Integer createGame(String gameName) {

        // Generate Game ID
        String gameID = "";
        for (int i = 1; i <= 7; i++) {
            Random random = new Random();
            int randomDigit = random.nextInt(10);
            gameID += String.valueOf(randomDigit);
        }

        // Create Game
//        games.put(gameID, Arrays.asList(null, null, gameName));

        return Integer.parseInt(gameID);
    }

    public HashMap<String, GameData> listGames() {
        return games;
    }

    public void joinGame(String playerColor, String username, String authToken, Integer gameID) {

        GameData game = games.get(gameID); // THIS IS PROBABLY AN ISSUE!

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        // Update White/Black Usernames
        if(Objects.equals(playerColor, "WHITE") && whiteUsername == null) {
            whiteUsername = username;
        }
        if(Objects.equals(playerColor, "BLACK") && blackUsername == null) {
            blackUsername = username;
        }
    }

    public void clear(){
        games.clear();
    }

}
