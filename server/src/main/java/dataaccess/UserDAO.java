package dataaccess;

import model.UserData;

import java.util.*;

public interface UserDAO{

    void createUser(String username, String password, String email);

    UserData getUser(String username);

    void clear();

}
