package server;

import model.GameData;
import java.util.*;


public record ListGamesResult(HashMap<String, GameData> games) {
}
