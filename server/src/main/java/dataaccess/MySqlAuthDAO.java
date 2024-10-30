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

    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM chess WHERE authToken=?";

        System.out.println("Implement");
    }

    public String getUser(String authToken) {
        var statement = "SELECT username FROM pet WHERE username=?";

        System.out.println("Implement");
        return "";
    }

    public void clear() {
        var statement = "TRUNCATE TABLE auth";

        System.out.println("Implement");
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

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                System.out.println("Implement");
            }
        } catch (SQLException ex) {
            new DataAccessException(ex.getMessage());
        }
    }
}
