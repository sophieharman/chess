package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.JoinGameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.SQLException;


public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException{
        configureDatabase();
    }

    public Integer createGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement)) {

                // Generate Game ID
                Random random = new Random();
                Integer gameID = random.nextInt(100_000_000, 1_000_000_000);

                // New Game Data
                ps.setInt(1, gameID);
                ps.setString(2, null);
                ps.setString(3, null);
                ps.setString(4, gameName);
                ps.setString(5, null);
                ps.executeUpdate();
                return gameID;
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public HashMap<Integer, GameData> listGames() throws DataAccessException {
        var allGames = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Implement!!!");
//                        allGames.add(readPet(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void addGame(GameData gameInfo) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {

                // Add Game Data
                ps.setInt(1, gameInfo.gameID());
                ps.setString(2, gameInfo.whiteUsername());
                ps.setString(3, gameInfo.blackUsername());
                ps.setString(4, gameInfo.gameName());
                // Serialize Chess Game
                ps.setString(5, new Gson().toJson(gameInfo.game()));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM game WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(gameID, rs.getString("whiteUsername"),
                                rs.getString("blackUsername"), rs.getString("gameName"), game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void removeGame(Integer gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE game";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` ChessGame NOT NULL,
              PRIMARY KEY (`gameID`),
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                System.out.println("Implement");
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }



}
