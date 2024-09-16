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
        // Calculate Possible Moves
        Collection<ChessMove> moves = pieceMovesCalculator(board, myPosition);
        return moves;
    }

    public Collection<ChessMove> pieceMovesCalculator(ChessBoard board, ChessPosition myPosition)
    {
        // Grid Border Positions
        Collection<ChessPosition> borders = new HashSet<ChessPosition>();

        for(int i = 0; i <= 9; i++)
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

    public boolean blocked(ChessPosition position)
    {
        // Return True if Position is Blocked
        return position == null;
    }

    public Collection<ChessMove> diagonal(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> borders)
    {
        Collection<ChessMove> possible_positions = new ArrayList<ChessMove>();


        // TOP LEFT DIAGONAL
        int a1 = myPosition.getColumn();
        int b1 = myPosition.getRow();
        while(true)
        {
            // Coordinates for Possible Position
            a1--;
            b1--;
            ChessPosition pos = new ChessPosition(a1, b1);

            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(pos))
            {
                break;
            }

            // Call ChessMove Class
            ChessMove move = new ChessMove(myPosition, pos, type);
            // Append Position to List of Valid Positions
            possible_positions.add(move);

        }

        // BOTTOM RIGHT DIAGONAL
        int a2 = myPosition.getColumn();
        int b2 = myPosition.getRow();
        while(true)
        {
            a2++;
            b2++;
            ChessPosition pos = new ChessPosition(a2, b2);

            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(pos))
            {
                break;
            }

            // Call ChessMove Class
            ChessMove move = new ChessMove(myPosition, pos, type);
            // Append Position to List of Valid Positions
            possible_positions.add(move);
        }

        // TOP RIGHT DIAGONAL
        int a3 = myPosition.getColumn();
        int b3 = myPosition.getRow();
        while(true)
        {
            a3++;
            b3--;
            ChessPosition pos = new ChessPosition(a3, b3);

            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(pos))
            {
                break;
            }

            // Call ChessMove Class
            ChessMove move = new ChessMove(myPosition, pos, type);
            // Append Position to List of Valid Positions
            possible_positions.add(move);
        }

        // BOTTOM LEFT DIAGONAL
        int a4 = myPosition.getColumn();
        int b4 = myPosition.getRow();
        while(true)
        {
            a4--;
            b4++;
            ChessPosition pos = new ChessPosition(a3, b3);

            // Verify Position is within the Board Range and Position is Open
            if(borders.contains(pos) || blocked(pos))
            {
                break;
            }

            // Call ChessMove Class
            ChessMove move = new ChessMove(myPosition, pos, type);
            // Append Position to List of Valid Positions
            possible_positions.add(move);
        }

        return possible_positions;


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

