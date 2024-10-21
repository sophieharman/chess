package dataaccess;

import model.AuthData;
import java.util.*;

public interface AuthDAO {

    String createAuth(AuthData authData);

    Collection<String> getAuth(AuthData authToken);

    void deleteAuth(AuthData authToken);

    void clear();
}
