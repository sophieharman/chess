package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
        Collection<ChessMove> moves = pieceMovesCalculator(myPosition);
        return moves;
    }

    public Collection<ChessMove> pieceMovesCalculator(ChessPosition myPosition)
    {
        // Grid Border Positions
        Collection<ChessMove> borders = new ArrayList<ChessMove>();

        for(int i = 1; i <= 8; i++)
        {

            ChessPosition edge1 = new ChessPosition(1, i);
            ChessPosition edge2 = new ChessPosition(i, 1);
            ChessPosition edge3 = new ChessPosition(8, i);
            ChessPosition edge4 = new ChessPosition(i, 8);

            borders.add(edge1);
            borders.add(edge2);
            borders.add(edge3);
            borders.add(edge4);
        }

        // Calculate Bishop Moves
        if (type == PieceType.BISHOP)
        {
            // CODE HERE!!!!
        }

    }

    public boolean blocked(ChessPosition position)
    {
        // Return True if Position is Blocked
        return position == null;
    }

    public Collection<ChessMove> diagonal(ChessBoard board, ChessPosition myPosition, List<List<Integer>> borders)
    {
        Collection<ChessMove> possible_positions = new ArrayList<ChessMove>();

        int curr_a = myPosition.getColumn();
        int curr_b = myPosition.getRow();

        // TOP LEFT DIAGONAL
        int a1 = curr_a;
        int b1 = curr_b;
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

            // Append Position to List of Valid Positions
            possible_positions.add(pos);
        }

        // BOTTOM RIGHT DIAGONAL
        int a2 = curr_a;
        int b2 = curr_b;
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

            // Append Position to List of Valid Positions
            possible_positions.add(pos);
        }

        // TOP RIGHT DIAGONAL
        int a3 = curr_a;
        int b3 = curr_b;
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

            // Append Position to List of Valid Positions
            possible_positions.add(pos);
        }

        // BOTTOM LEFT DIAGONAL
        int a4 = curr_a;
        int b4 = curr_b;
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

            // Append Position to List of Valid Positions
            possible_positions.add(pos);
        }


        return possible_positions;


    }



}
