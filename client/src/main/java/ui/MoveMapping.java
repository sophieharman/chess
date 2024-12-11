package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MoveMapping {
    final private String start;
    final private String end;
    final private ChessBoard board;
    final private HashMap<Character, Integer> mapping = new HashMap<>();

    public MoveMapping(String start, String end, String playerColor, ChessBoard board) {

        this.start = start;
        this.end = end;
        this.board = board;

        if (Objects.equals(playerColor, "white")) {
            mapping.put('a', 1);
            mapping.put('b', 2);
            mapping.put('c', 3);
            mapping.put('d', 4);
            mapping.put('e', 5);
            mapping.put('f', 6);
            mapping.put('g', 7);
            mapping.put('h', 8);
        }
        else {
            mapping.put('h', 1);
            mapping.put('g', 2);
            mapping.put('f', 3);
            mapping.put('e', 4);
            mapping.put('d', 5);
            mapping.put('c', 6);
            mapping.put('b', 7);
            mapping.put('a', 8);
        }

    }

    public void verifyFormatting(String str) throws ResponseException {

        // Verify Length
        if(str.length() != 2) {
            throw new ResponseException(400, "Expected: <a-h><1-8>");
        }

        // Verify First Character
        if (!mapping.containsKey(str.charAt(0))) {
            throw new ResponseException(400, "Expected: <a-h><1-8>");
        }

        // Verify Second Character is an Integer
        try {
            Integer.valueOf(String.valueOf(str.charAt(1)));
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: Expected: <a-h><1-8>");
        }
    }

    public ChessPosition convertToPosition(String str) throws ResponseException {
        verifyFormatting(str);
        Integer row = Character.getNumericValue(str.charAt(1));
        Integer col = mapping.get(str.charAt(0));
        return new ChessPosition(row, col);
    }

    public boolean inGrid(ChessPosition position) {
        // Valid Grid Positions
        Collection<ChessPosition> grid = new HashSet<ChessPosition>();
        for(int i = 1; i <= 8; i++)
        {
            for(int j = 1; j <= 8; j++)
            {
                ChessPosition pos = new ChessPosition(i, j);
                grid.add(pos);
            }
        }

        if (!grid.contains(position)) {
            return false;
        }
        return true;
    }

    public ChessPiece getPiece(ChessPosition position) throws ResponseException {
        // Verify Position is Valid
        if (!inGrid(position)) {
            throw new ResponseException(400, "Invalid Start Position");
        }

        // Verify Piece Exists
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            throw new ResponseException(400, "No Player Piece on Specified Position");
        }

        // Return Piece
        return piece;
    }

    public ChessMove convertToMove() throws ResponseException {

        // Convert Strings to Positions
        ChessPosition startPos = convertToPosition(this.start);
        ChessPosition endPos = convertToPosition(this.end);

        // Return Move
        ChessPiece piece = getPiece(startPos);
        return new ChessMove(startPos, endPos, null);
    }
}
