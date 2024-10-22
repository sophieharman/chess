package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{

    final private HashMap<String, Collection<String>> authInfo = new HashMap<>();

    public String createAuth(AuthData authData){
        // Insert String
        return UUID.randomUUID().toString();
    }

    public Collection<String> getAuth(AuthData authToken){
        // Search for Authentication Data in Database
        return authInfo.get(authToken);
    }

    public void deleteAuth(AuthData authToken){
        // Remove Specified Authentication Token
        authInfo.remove(authToken);
    }

    public void clear(){
        // Clear All Users
        authInfo.clear();
    }
}
