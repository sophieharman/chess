package server;

import java.util.Collection;
import java.util.HashMap;

public record ListGamesResult(HashMap<String, Collection<String>> games) {
}
