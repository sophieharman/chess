package ui;

import java.util.*;

public class GameIDs {

    Map<Integer, Integer> userConvert = new HashMap<>();

    public void addID(Integer gameID, Integer userID) {
        // Add GameID Data to HashMap
        userConvert.put(gameID, userID);
    }

    public Integer getUserGameID(Integer primaryID) {
        return userConvert.get(primaryID);
    }

    public Integer getPrimaryGameID(Integer userID) {
        for (Map.Entry<Integer, Integer> ids: userConvert.entrySet()) {
            if (Objects.equals(ids.getValue(), userID)) {
                return ids.getKey();
            }
        }
        return null;
    }
}
