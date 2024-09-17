package chess;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {

        // Grid Border Positions
        Collection<ChessPosition> borders = new HashSet<ChessPosition>();

        for(int i = 1; i <= 8; i++)
        {

            ChessPosition edge1 = new ChessPosition(0, i);
            ChessPosition edge2 = new ChessPosition(i, 0);
            ChessPosition edge3 = new ChessPosition(9, i);
            ChessPosition edge4 = new ChessPosition(i, 9);

            borders.add(edge1);
            borders.add(edge2);
            borders.add(edge3);
            borders.add(edge4);
        }


        // Calculate Bishop Moves
//        if (type == PieceType.BISHOP)
//        {
        Collection<ChessMove> moves = diagonal(board, myPosition, borders);
//        }

        return moves;
    }


    public boolean blocked(ChessBoard board, ChessPosition position, boolean captured)
    {
        ChessPiece piece = board.getPiece(position);

        if(piece.getTeamColor() != pieceColor) {
            captured=true;
        }
        if(piece == null||piece.getTeamColor() != pieceColor||captured)
        {
            return false;
        }

        // Return True if Position is Not Blocked
        return true;

    }

    public Collection<ChessMove> diagonal(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> borders)
    {
        Collection<ChessMove> possible_moves = new ArrayList<ChessMove>();


        // TOP LEFT DIAGONAL
        int a = myPosition.getColumn();
        int b = myPosition.getRow();
        boolean captured = false;
        while(true)
        {
            // Coordinates for Possible Position
            a--;
            b--;
            ChessPosition pos = new ChessPosition(b, a);


            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(board, pos, captured))
            {
                break;
            }


            // Initialize Possible Move
            ChessMove move = new ChessMove(myPosition, pos, type);
            possible_moves.add(move);


        }

        // BOTTOM RIGHT DIAGONAL
        a = myPosition.getColumn();
        b = myPosition.getRow();
        captured = false;
        while(true)
        {
            a++;
            b++;
            ChessPosition pos = new ChessPosition(b, a);


            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(board, pos, captured))
            {
                break;
            }


            // Initialize Possible Move
            ChessMove move = new ChessMove(myPosition, pos, type);
            possible_moves.add(move);

        }


        // BOTTOM LEFT DIAGONAL
        a = myPosition.getColumn();
        b = myPosition.getRow();
        captured = false;
        while(true)
        {
            a++;
            b--;
            ChessPosition pos = new ChessPosition(b, a);


            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(board, pos, captured))
            {
                break;
            }


            // Initialize Possible Move
            ChessMove move = new ChessMove(myPosition, pos, type);
            possible_moves.add(move);
        }

        // TOP RIGHT DIAGONAL
        a = myPosition.getColumn();
        b = myPosition.getRow();
        captured = false;
        while(true)
        {
            a--;
            b++;
            ChessPosition pos = new ChessPosition(b, a);

            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(board, pos, captured))
            {
                break;
            }


            // Initialize Possible Move
            ChessMove move = new ChessMove(myPosition, pos, type);
            possible_moves.add(move);
        }

        return possible_moves;


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }
}

