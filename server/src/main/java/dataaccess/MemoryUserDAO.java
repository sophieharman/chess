package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.*;


public class MemoryUserDAO implements UserDAO{

    final private HashMap<String, UserData> userInfo = new HashMap<>();


    public void createUser(String username, String password, String email) {
        // Add User Data: Username, Password, Email
        UserData userData = new UserData(username, password, email);
        userInfo.put(username, userData);
    }

    public UserData getUser(String username) {
        // Search for User in Database
        if(!userInfo.containsKey(username)) {
            return null;
        }
        return userInfo.get(username);
    }

    public void clear() {
        // Clear All Users
        userInfo.clear();
    }


}
