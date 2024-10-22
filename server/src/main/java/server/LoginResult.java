package server;

import model.AuthData;

public record LoginResult(String username, String authToken) {
}
