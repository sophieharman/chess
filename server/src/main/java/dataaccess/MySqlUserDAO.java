package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.UUID;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, hashedPassword(username, password));
                ps.setString(3, email);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (verifyUser(username, rs.getString("password"))) {
                            return new UserData(username, rs.getString("password"), rs.getString("email"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public String hashedPassword(String username, String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        // Read Hashed Password from Database
        UserData userInfo = getUser(username);
        String hashedPassword = userInfo.password();
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE user";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }


}
