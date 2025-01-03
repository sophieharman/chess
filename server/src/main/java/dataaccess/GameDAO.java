package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {

    Integer createGame(String gameName) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void addGame(GameData game) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    void removeGame(Integer gameID) throws DataAccessException;

    void clear() throws DataAccessException;
}
