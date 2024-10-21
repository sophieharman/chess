package dataaccess;

import java.util.*;

public interface UserDAO{

    void createUser(String username, String password, String email);

    Collection<String> getUser(String username);

    void deleteUser(String username);

    void clear();

}
