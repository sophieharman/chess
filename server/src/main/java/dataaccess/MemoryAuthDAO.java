package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{

    final private HashMap<String, AuthData> authInfo = new HashMap<>();

    public void createAuth(String username){
        // Add Authentication Data
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
    }

    public String getAuth(String username){
        // Search for Authentication Data
        AuthData authData = authInfo.get(username);
        return authData.authToken();
    }

    public void deleteAuth(String authToken){
        // Remove Specified Authentication Token
        authInfo.remove(authToken);
    }

    public String getUser(String authToken) {
        for(String key: authInfo.keySet()) {
            AuthData authData = authInfo.get(key);
            if(Objects.equals(authData.authToken(), authToken)) {
                return key;
            }
        }
        return null; // Maybe change this?
    }


    public void clear(){
        // Clear All Users
        authInfo.clear();
    }
}
