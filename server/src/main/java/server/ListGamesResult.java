package server;

import model.GameData;
import java.util.*;


public record ListGamesResult(List<GameData> games) {
}
