package dataaccess;

public interface UserDAO{

    void createUser(String username, String password, String email);

    Object getUser(String username);

    void deleteUser(String username);

    void clear();

}
