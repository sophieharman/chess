package server;

import model.GameData;
import java.util.*;


public record ListGamesResult(HashMap<Integer, GameData> games) {
}
