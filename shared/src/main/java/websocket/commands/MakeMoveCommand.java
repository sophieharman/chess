package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private ChessMove move;
    private ChessGame game;
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessGame game) {
        super(commandType, authToken, gameID);

        this.move = move;
        this.game = game;
    }

    public ChessMove getMove() {
        return this.move;
    }
}
