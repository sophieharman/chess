package dataaccess;

import model.AuthData;
import java.util.*;

public interface AuthDAO {

    void createAuth(String username);

    String getAuth(String username);

    void deleteAuth(AuthData authToken);

    void clear();
}
