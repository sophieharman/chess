package server;

import model.GameData;
import java.util.*;


public record ListGamesResult(Collection<GameData> games) {
}
