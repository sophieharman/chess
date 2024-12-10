package ui;

import java.util.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessPiece.PieceType;
import exception.ResponseException;

import static ui.EscapeSequences.*;

public class BoardDisplay {

    private boolean white;
    private static String teamColor;
    private static final String EMPTY = " "; // Padded Characters
    private static final int BOARD_SIZE_IN_SQUARES = 8; // Board Dimensions
    public ChessBoard board = new ChessBoard();


    public void main(ChessBoard newBoard, String teamColor) throws ResponseException {

        // Update Board
        board = newBoard;

        if (Objects.equals(teamColor, "white")) {
            this.white = true;
        }
        else{
            this.white = false;
        }


        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeader(out, teamColor);

        drawBoard(out, teamColor);

        drawHeader(out, teamColor);

        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
    }

    private static void drawHeader(PrintStream out, String teamColor) {
        String whiteHeader = "    a  b  c  d  e  f  g  h ";
        String blackHeader = "    h  g  f  e  d  c  b  a ";

        if (Objects.equals(teamColor, "white")) {
            printHeaderText(out, whiteHeader);
        }
        else {
            printHeaderText(out, blackHeader);
        }
        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(player);

        setBlack(out);
    }

    private void drawBoard(PrintStream out, String teamColor) {
        int sideHeader;
        if (Objects.equals(teamColor, "white")) {
            sideHeader = 8;
        }
        else {
            sideHeader = 1;
        }

        int start = white ? 1 : 8;
        int stop = white ? 9 : 0;
        int step = white ? 1 : -1;
        white = !white;

        boolean colAlt = false;
        for (int i = start; i != stop; i += step) {
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(" " + (sideHeader) + " ");
            colAlt = !colAlt;
            for (int j = start; j != stop; j += step) {

                if(colAlt) {
                    setLightBrown(out);
                }
                else {
                    setDarkBrown(out);
                }

                out.print(EMPTY.repeat(1));
                printPlayer(out, i, j);
                out.print(EMPTY.repeat(1));

                setBlack(out);
                colAlt = !colAlt;
            }
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(" " + (sideHeader) + " ");
            sideHeader -= step;

            out.println();
        }

    }

    private void printPlayer(PrintStream out, int i, int j) {

        // Piece Mappings
        Map<PieceType, String> pieceMapping = new HashMap<>();
        pieceMapping.put(PieceType.KING, "K");
        pieceMapping.put(PieceType.QUEEN, "Q");
        pieceMapping.put(PieceType.ROOK, "R");
        pieceMapping.put(PieceType.BISHOP, "B");
        pieceMapping.put(PieceType.KNIGHT, "N");
        pieceMapping.put(PieceType.PAWN, "P");

        // Add Pieces for Row
        ChessPosition pos = new ChessPosition(i, j);
        ChessPiece chessPiece = board.getPiece(pos);


        if (chessPiece == null) {
            out.print(" ");
        }
        else {
            String piece = pieceMapping.get(chessPiece.getPieceType());
            ChessGame.TeamColor pieceColor = chessPiece.getTeamColor();
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                out.print(SET_TEXT_COLOR_BLACK); //CHANGED THIS
            }
            else {
                out.print(SET_TEXT_COLOR_WHITE); // CHANGED THIS
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
