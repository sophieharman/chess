package dataaccess;

import model.UserData;

public class MySqlUserDAO {

    public MySqlAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        System.out.println("Implement!");
    }

    public UserData getUser(String username) throws DataAccessException {
        System.out.println("Implement");
    }

    public void clear() throws DataAccessException {
        System.out.println("Implement");
    }
}
