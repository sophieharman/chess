package dataaccess;

import model.UserData;

import java.util.*;


public class MemoryUserDAO implements UserDAO{

    final private HashMap<String, UserData> userData = new HashMap<>();


    public void createUser(String username, String password, String email) {
        // Add User Data: Username, Password, Email
//        userInfo.put(username, Arrays.asList(password, email));
    }

    public UserData getUser(String username) {
        // Search for User in Database
        if(!userData.containsKey(username)) {
            return null;
        }
        return userData.get(username);
    }

    public void deleteUser(String username) {
        // Remove Specified User
        userData.remove(username);
    }

    public void clear()
    {
        // Clear All Users
        userData.clear();
    }


}
