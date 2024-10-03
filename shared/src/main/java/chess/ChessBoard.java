package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)
    {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public void removePiece(ChessPosition position)
    {
        squares[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position)
    {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public ChessPiece[][] newBoard()
    {
        for(int i = 0; i < squares.length; i++)
        {
            for(int j = 0; j < squares[i].length; j++)
            {
                squares[i][j] = null;
            }
        }

        return squares;
    }

    public void resetBoard()
    {

        Enum white = ChessGame.TeamColor.WHITE;
        Enum black = ChessGame.TeamColor.BLACK;


        // White Chess Pieces
        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        // Black Chess Pieces
        ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

        // White Pieces
        this.addPiece(new ChessPosition(1, 1), whiteRook);
        this.addPiece(new ChessPosition(1, 2), whiteKnight);
        this.addPiece(new ChessPosition(1, 3), whiteBishop);
        this.addPiece(new ChessPosition(1, 4), whiteQueen);
        this.addPiece(new ChessPosition(1, 5), whiteKing);
        this.addPiece(new ChessPosition(1, 6), whiteBishop);
        this.addPiece(new ChessPosition(1, 7), whiteKnight);
        this.addPiece(new ChessPosition(1, 8), whiteRook);
        for(int i = 1; i <= 8; i++)
        {
            this.addPiece(new ChessPosition(2, i), whitePawn);
        }

        // Black Pieces
        this.addPiece(new ChessPosition(8, 1), blackRook);
        this.addPiece(new ChessPosition(8, 2), blackKnight);
        this.addPiece(new ChessPosition(8, 3), blackBishop);
        this.addPiece(new ChessPosition(8, 4), blackQueen);
        this.addPiece(new ChessPosition(8, 5), blackKing);
        this.addPiece(new ChessPosition(8, 6), blackBishop);
        this.addPiece(new ChessPosition(8, 7), blackKnight);
        this.addPiece(new ChessPosition(8, 8), blackRook);
        for(int i = 1; i <= 8; i++)
        {
            this.addPiece(new ChessPosition(7, i), blackPawn);
        }


    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.deepToString(squares) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
