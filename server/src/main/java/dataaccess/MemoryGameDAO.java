package dataaccess;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    final private HashMap<String, Collection<String>> games = new HashMap<>();

    public void createGame() {
        System.out.println("Implement");
    }

    public void getGame() {
        System.out.println("Implement");
    }

    public void updateGame() {
        System.out.println("Implement");
    }

    public void listGames() {
        System.out.println("Implement");
    }

    public void deleteGames(){
        System.out.println("Implement");
    }

    public void clear(){
        System.out.println("Implement");
    }

}
