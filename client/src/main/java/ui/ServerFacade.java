package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(UserData userInfo) throws ResponseException {
        var path = "/user";
        RegisterResult result = this.makeRequest("POST", path, userInfo, null, RegisterResult.class);
        return result;
    }

    public LoginResult login(UserData userInfo, String authToken) throws ResponseException {
        var path = "/session";
        LoginResult result = this.makeRequest("POST", path, userInfo, authToken, LoginResult.class);
        return result;
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken, LogoutResult.class);
    }

    public CreateGameResult createGame(String gameName, String authToken) throws ResponseException {
        var path = "/game";
        GameData game = new GameData(-1, null, null, gameName, null);
        CreateGameResult result = this.makeRequest("POST", path, game, authToken, CreateGameResult.class);
        return result;
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        ListGamesResult gameList = this.makeRequest("GET", path, null, authToken, ListGamesResult.class);
        return gameList;
    }

    public void joinGame(String playerColor, String authToken, Integer gameID) throws ResponseException {
        var path = "/game";
        JoinGameData newGame = new JoinGameData(playerColor, gameID);
        this.makeRequest("PUT", path, newGame, authToken, JoinGameResult.class);
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken!=null) {
                http.setRequestProperty("Authorization", authToken);
            }
            http.setDoOutput(true);

            if (request != null) {
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "Error: Bad Request"); // EDIT THIS (Get the Message)
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            if (responseClass != null) {
                response = new Gson().fromJson(reader, responseClass);
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
