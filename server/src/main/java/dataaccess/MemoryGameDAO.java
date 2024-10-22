package dataaccess;

import java.util.*;

public class MemoryGameDAO implements GameDAO{

    final private HashMap<String, String> games = new HashMap<>();

    public String createGame(String gameName) {

        // Generate Game ID
        String gameID = generateGameID();

        // Create Game
        games.put(gameName, gameID);

        return gameID;
    }

    public String generateGameID() {
        String gameID = "";
        for (int i = 1; i <= 7; i++) {
            Random random = new Random();
            int randomDigit = random.nextInt(10);
            gameID += String.valueOf(randomDigit);
        }
        return gameID;
    }

    public HashMap<String, String> getGames() {
        return games;
    }

    public void updateGame() {
        System.out.println("Implement");
    }

    public void clear(){
        games.clear();
    }

}
