package dataaccess;

import java.util.*;


public class MemoryUserDAO implements UserDAO{

    final private HashMap<String, Collection<String>> userInfo = new HashMap<>();


    public void createUser(String username, String password, String email) {
        // Add User Data: Username, Password, Email
        userInfo.put(username, Arrays.asList(password, email));
    }

    public Collection<String> getUser(String username) {
        // Search for User in Database
        if(!userInfo.containsKey(username)) {
            return null;
        }
        return userInfo.get(username);
    }

    public void deleteUser(String username) {
        // Remove Specified User
        userInfo.remove(username);
    }

    public void clear()
    {
        // Clear All Users
        userInfo.clear();
    }


}
