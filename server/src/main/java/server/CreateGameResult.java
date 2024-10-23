package server;

public record CreateGameResult(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
}
