package dataaccess;

import model.AuthData;
import java.util.*;

public interface AuthDAO {

    String createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    String getUser(String authToken);

    void clear();
}
