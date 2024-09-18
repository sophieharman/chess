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
    private static boolean captured = false;

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
        Collection<ChessPosition> grid_positions = new HashSet<ChessPosition>();

        for(int i = 1; i <= 8; i++)
        {
            for(int j = 1; j <= 8; j++)
            {
                ChessPosition position = new ChessPosition(i, j);
                grid_positions.add(position);
            }
        }


        // Initialize Moves Collection
        Collection<ChessMove> moves = Collections.emptyList();

        if (type == PieceType.BISHOP)
        {
            moves = diagonal(board, myPosition, grid_positions);
        }
        else if (type == PieceType.KNIGHT)
        {
            System.out.println("Implement");
        }
        else if (type == PieceType.ROOK)
        {
            System.out.println("Implement");
        }
        else if (type == PieceType.PAWN)
        {
            System.out.println("Implement");
        }
        else if(type == PieceType.KING)
        {
            System.out.println("Implement");
        }
        else if (type == PieceType.QUEEN)
        {
            System.out.println("Implement");
        }

        return moves;
    }


    public boolean blocked(ChessBoard board, ChessPosition myPosition)
    {

        ChessPiece piece = board.getPiece(myPosition);

        if(piece == null)
        {
            return false;
        }
        else
        {
            if(piece.getTeamColor() != pieceColor)
            {
               return false;
            }
            return true;
        }

    }

    public Collection<ChessMove> diagonal(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid_positions)
    {

        Collection<ChessMove> possible_moves = new ArrayList<ChessMove>();


        // TOP LEFT DIAGONAL
        int a = myPosition.getColumn();
        int b = myPosition.getRow();
        captured = false;
        while(true)
        {
            // Coordinates for Possible Position
            a--;
            b--;
            ChessPosition pos = new ChessPosition(b, a);


            // Verify Position is within the Board Range and Position is Open
            if(!grid_positions.contains(pos)||blocked(board, pos)||captured)
            {
                break;
            }

            ChessPiece piece = board.getPiece(pos);
            if(piece != null && piece.getTeamColor() != pieceColor)
            {
                captured = true;
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
            if(!grid_positions.contains(pos)||blocked(board, pos)||captured)
            {
                break;
            }

            ChessPiece piece = board.getPiece(pos);
            if(piece != null && piece.getTeamColor() != pieceColor)
            {
                captured = true;
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
            if(!grid_positions.contains(pos)||blocked(board, pos)||captured)
            {
                break;
            }

            ChessPiece piece = board.getPiece(pos);
            if(piece != null && piece.getTeamColor() != pieceColor)
            {
                captured = true;
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
            if(!grid_positions.contains(pos) || blocked(board, pos)||captured)
            {
                break;
            }

            ChessPiece piece = board.getPiece(pos);
            if(piece != null && piece.getTeamColor() != pieceColor)
            {
                captured = true;
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

