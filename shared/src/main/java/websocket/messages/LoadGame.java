package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGame extends ServerMessage{
    private ChessGame game;
    public LoadGame(ServerMessageType type, ChessGame game) {
        super(type);

        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }

}
