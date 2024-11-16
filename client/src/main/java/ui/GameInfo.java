package ui;

import java.util.*;

public class GameInfo {

    Map<Integer, List<String>> gameInfo = new HashMap<>();

    public void addInfo(Integer gameID, String whiteUser, String blackUser) {

        // Remove Outdated Game Info
        gameInfo.remove(gameID);

        // Add Game Info
        List<String> info = new ArrayList<String>();
        info.add(whiteUser);
        info.add(blackUser);

        gameInfo.put(gameID, info);
    }

    public void updateInfo(Integer gameID, String whiteUser, String blackUser) {
        List<String> info = gameInfo.get(gameID);
        info.set(0, whiteUser);
        info.set(1, blackUser);
    }

    public String getWhiteUser(Integer gameID) {
        List<String> info = gameInfo.get(gameID);
        return info.getFirst();
    }

    public String getBlackUser(Integer gameID) {
        List<String> info = gameInfo.get(gameID);
        return info.get(1);
    }
}


