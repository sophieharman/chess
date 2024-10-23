package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {

    Integer createGame(String gameName);

    HashMap<String, GameData> listGames();

    void joinGame(String playerColor, String authToken, Integer gameID);

    void clear();
}
