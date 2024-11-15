package ui;

import java.util.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessPiece.PieceType;

import static ui.EscapeSequences.*;

public class BoardDisplay {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    // Padded characters.
    private static final String EMPTY = "   ";


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeader(out);

        drawBoard(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeader(PrintStream out) {
        String headers = "    a  b  c  d  e  f  g  h ";
        printHeaderText(out, headers);
        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(player);

        setBlack(out);
    }

    private static void drawBoard(PrintStream out) {

        boolean colAlt = true;
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(" " + (boardRow + 1) + " ");
            drawRowOfSquares(out, colAlt);
            colAlt = !colAlt;
        }
    }

    private static void drawRowOfSquares(PrintStream out, boolean colAlt) {

        for (int squareRow = 0; squareRow < 8; ++squareRow) {

            if(colAlt) {
                setLightBrown(out);
            }
            else {
                setDarkBrown(out);
            }

            out.print(EMPTY.repeat(1));

            setBlack(out);
            colAlt = !colAlt;
        }
        out.println();
    }

    public List<String> printPieces() {

        // New Chess Board
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        // Piece Mappings
        Map<PieceType, String> pieceMapping = new HashMap<>();
        pieceMapping.put(PieceType.KING, "K");
        pieceMapping.put(PieceType.QUEEN, "Q");
        pieceMapping.put(PieceType.ROOK, "R");
        pieceMapping.put(PieceType.BISHOP, "B");
        pieceMapping.put(PieceType.KNIGHT, "N");
        pieceMapping.put(PieceType.PAWN, "P");

        // Iterate through Board
        List<String> rows = new ArrayList<>();
        for(int i = 1; i <= 8; i++)
        {
            List<String> row = new ArrayList<>();
            for(int j = 1; j <= 8; j++)
            {
                // Add Pieces for Row
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece chessPiece = board.getPiece(pos);
                String piece = pieceMapping.get(chessPiece);
                row.add(piece);
            }
            String rowPieces = String.join(" ", row);
            rows.add(rowPieces);
        }
    return rows;
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkBrown(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_BROWN);
        out.print(SET_TEXT_COLOR_DARK_BROWN);
    }

    private static void setLightBrown(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_BROWN);
        out.print(SET_TEXT_COLOR_LIGHT_BROWN);
    }

}
