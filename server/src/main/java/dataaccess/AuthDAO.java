package dataaccess;

import model.AuthData;
import java.util.*;

public interface AuthDAO {

    String createAuth(String username);

    void deleteAuth(String authToken);

    String getUser(String authToken);

    void clear();
}
