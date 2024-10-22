package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{

    final private HashMap<String, String> authInfo = new HashMap<>();

    public void createAuth(String username){
        // Add Authentication Data
        authInfo.put(username, UUID.randomUUID().toString());
    }

    public String getAuth(String username){
        // Search for Authentication Data
        return authInfo.get(username);
    }

    public void deleteAuth(String authToken){
        // Remove Specified Authentication Token
        authInfo.remove(authToken);
    }

    public void clear(){
        // Clear All Users
        authInfo.clear();
    }
}
