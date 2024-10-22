package dataaccess;

import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {

    String createGame(String gameName);

    HashMap<String, Collection<String>> getGames();

    void updateGame();

    void clear();
}
