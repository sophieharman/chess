package dataaccess;

import java.util.*;


public class MemoryUserDAO implements UserDAO{

    final private HashMap<String, Collection<String>> userInfo = new HashMap<>();


    public void addUser(String username, String password, String email) {
        // Add User Data: Username, Password, Email
        userInfo.put(username, Arrays.asList(password, email));
    }

    public Object getUser(String username) {
        // Search for User in Database
        return userInfo.get(username);
    }

    public Collection<String> getUserData(String username){
        return userInfo.get(username);
    }
}
