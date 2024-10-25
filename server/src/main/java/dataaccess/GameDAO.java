package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {

    Integer createGame(String gameName);

    HashMap<Integer, GameData> listGames();

    void addGame(GameData game);

    GameData getGame(Integer gameID);

    void removeGame(Integer gameID);

    void clear();
}
