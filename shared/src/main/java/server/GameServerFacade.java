package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class GameServerFacade {


    private final String gameServerUrl;

    public GameServerFacade(String url) {
        gameServerUrl = url;
    }

//    public Integer createGame(String gameName){
//        var path = "?";
//        return this.makeRequest("POST", path, gameID, createGameResult.class);
//    }

//    public Collection<GameData> listGames() {
//        var path = "?";
//        return this.makeRequest("GET", path, games, listGamesResult.class);
//    }

//    public void addGame(GameData game) {
//        var path = "?";
//        return this.makeRequest(?, path, ?, ?);
//    }

//    public GameData getGame(Integer gameID) {
//        var path = "?";
//        return this.makeRequest("GET", path, game, ?);
//    }

    public void removeGame(Integer gameID) throws ResponseException {
        var path = "?";
        this.makeRequest("DELETE", path, null, null);
    }

    public void clear() throws ResponseException {
        var path = "?";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(gameServerUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
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
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
