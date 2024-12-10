package model;

public record CreateGameResult(Integer gameID, String whiteUsername, String blackUsername, String gameName, GameData game) {
}
