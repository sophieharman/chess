package ui;

import java.util.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessPiece.PieceType;
import exception.ResponseException;

import static ui.EscapeSequences.*;

public class BoardDisplay {

    private static String teamColor;
    private static final String EMPTY = " "; // Padded Characters
    private static final int BOARD_SIZE_IN_SQUARES = 8; // Board Dimensions

    public static void main(String teamColor) throws ResponseException {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeader(out);

        drawBoard(out, teamColor);

        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
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

    private static void drawBoard(PrintStream out, String teamColor) {

        boolean colAlt = true;
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; ++i) {
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(" " + (i + 1) + " ");
            colAlt = !colAlt;
            for (int j = 0; j < 8; ++j) {

                if(colAlt) {
                    setLightBrown(out);
                }
                else {
                    setDarkBrown(out);
                }

                out.print(EMPTY.repeat(1));
                printPlayer(out, i, j, teamColor);
                out.print(EMPTY.repeat(1));

                setBlack(out);
                colAlt = !colAlt;
            }
            out.println();
        }

    }

    private static void printPlayer(PrintStream out, int i, int j, String teamColor) {
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

        // Add Pieces for Row
        ChessPosition pos = new ChessPosition(i+1, j+1);
        ChessPiece chessPiece = board.getPiece(pos);

        if (chessPiece == null) {
            out.print(" ");
        }
        else {
            String piece = pieceMapping.get(chessPiece.getPieceType());
            if (Objects.equals(teamColor, "white")) {
                if (i == 6 || i == 7) {
                    out.print(SET_TEXT_COLOR_WHITE);
                }
                else {
                    out.print(SET_TEXT_COLOR_BLACK);
                }
            }
            else {
                out.print(SET_TEXT_COLOR_BLACK);
                if (Objects.equals(piece, "K")) {
                    piece = "Q";
                }
                if (Objects.equals(piece, "Q")) {
                    piece = "K";
                }

            }
            out.print(piece);
        }
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
