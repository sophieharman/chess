package dataaccess;

import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {

    void createGame();

    HashMap<String, Collection<String>> getGames();

    void updateGame();

    void listGames();

    void deleteGames();

    void clear();
}
