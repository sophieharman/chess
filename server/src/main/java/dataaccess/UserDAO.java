package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.util.*;

public interface UserDAO{

    void createUser(String username, String password, String email) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;

}
