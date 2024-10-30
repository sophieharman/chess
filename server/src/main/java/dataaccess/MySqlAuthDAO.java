package dataaccess;


import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    public String createAuth(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO chess (authToken, username) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                String authToken = UUID.randomUUID().toString();
                ps.setString(1, authToken);
                ps.setString(2, username);
                ps.executeUpdate();
                return authToken;
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM chess WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String getUser(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM chess WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(2, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Implement");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE auth";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
            )
            """
    };

    private void configureDatabase() throws DataAccessException{
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