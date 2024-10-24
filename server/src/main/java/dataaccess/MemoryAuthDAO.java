package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{

    final private HashMap<String, AuthData> authInfo = new HashMap<>();

    public String createAuth(String username){
        // Add Authentication Data
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);

        // Add AuthToken to authInfo
        authInfo.put(authToken, authData);
        return authToken;
    }

    public AuthData getAuth(String username){
        // Search for Authentication Data
        return authInfo.get(username);
    }

    public void deleteAuth(String authToken){
        // Remove Specified Authentication Token
        authInfo.remove(authToken);
    }

    public String getUser(String authToken) {
        return getAuth(authToken).username();
    }


    public void clear(){
        // Clear All Users
        authInfo.clear();
    }
}
