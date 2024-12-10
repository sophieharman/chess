package ui;

import com.google.gson.Gson;
import model.GameData;

import java.util.*;

public class GameInfo {

    Map<Integer, List<String>> gameInfo = new HashMap<>();

    public void addInfo(Integer gameID, String whiteUser, String blackUser, GameData game) {

        // Remove Outdated Game Info
        gameInfo.remove(gameID);

        // Serialize Game
        String serializedGame = new Gson().toJson(game);

        // Add Game Info
        List<String> info = new ArrayList<String>();
        info.add(whiteUser);
        info.add(blackUser);
        info.add(serializedGame);

        gameInfo.put(gameID, info);
    }

    public String getWhiteUser(Integer gameID) {
        List<String> info = gameInfo.get(gameID);
        return info.getFirst();
    }

    public String getBlackUser(Integer gameID) {
        List<String> info = gameInfo.get(gameID);
        return info.get(1);
    }

    public GameData getGame(Integer gameID) {
        List<String> info = gameInfo.get(gameID);
        return new Gson().fromJson(info.get(2), GameData.class);
    }
}


