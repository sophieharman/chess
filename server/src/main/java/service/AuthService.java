package service;

import dataaccess.AuthDAO;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO = null;

    public AuthData createAuth(String username) {
        String token = generateAuthToken();
        AuthData authData = new AuthData(token, username);
        authDAO.createAuth(authData);
        return authData;
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
