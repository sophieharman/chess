package server;

import model.GameData;
import java.util.Collection;

public class GameServerFacade {


    private final String gameServerUrl;

    public GameServerFacade(String url) {
        gameServerUrl = url;
    }

    public Integer createGame(String gameName){
        System.out.println("Implement");
        return 0;
    }

//    public Collection<GameData> listGames() {
//        System.out.println("Implement");
//    }

    public void addGame(GameData game) {
        System.out.println("Implement");
    }

//    public GameData getGame(Integer gameID) {
//        System.out.println("Implement");
//    }

    public void removeGame(Integer gameID) {
        System.out.println("Implement");
    }

    public void clear() {
        System.out.println("Implement");
    }


}
