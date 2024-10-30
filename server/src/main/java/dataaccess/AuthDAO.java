package dataaccess;

import model.AuthData;
import java.util.*;

public interface AuthDAO{

    String createAuth(String username) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    String getUser(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}
