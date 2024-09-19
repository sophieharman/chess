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
        Collection<ChessMove> moves = new ArrayList<ChessMove>();


        // Position
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

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
            moves = straight(board, myPosition, grid_positions);
        }
        else if (type == PieceType.PAWN)
        {
            System.out.println("Implement");
        }
        else if(type == PieceType.KING)
        {
            List<List<Integer>> coords = Arrays.asList(
                    Arrays.asList(x+1,y),
                    Arrays.asList(x-1,y),
                    Arrays.asList(x,y+1),
                    Arrays.asList(x,y-1),
                    Arrays.asList(x+1,y+1),
                    Arrays.asList(x-1,y-1),
                    Arrays.asList(x+1,y-1),
                    Arrays.asList(x-1,y+1));

            for (List<Integer> coord : coords)
            {
                x = coord.get(0);
                y = coord.get(1);
                ChessMove move = exploreBoard(board, myPosition, x ,y);
                moves.add(move);
            }

            moves.removeIf(Objects::isNull);
            return moves;
        }
        else if (type == PieceType.QUEEN)
        {
            moves = diagonal(board, myPosition, grid_positions);
            Collection<ChessMove> moves2 = straight(board, myPosition, grid_positions);
            moves.addAll(moves2);
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
        return piece.getTeamColor() == pieceColor;
    }

    public Collection<ChessMove> straight(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid_positions)
    {
        Collection<ChessMove> possible_moves = new ArrayList<ChessMove>();

        // UP
        int a = myPosition.getColumn();
        int b = myPosition.getRow();
        captured = false;
        while(true)
        {
            // Coordinates for Possible Position
            a++;
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

        // DOWN
        a = myPosition.getColumn();
        b = myPosition.getRow();
        captured = false;
        while(true)
        {
            // Coordinates for Possible Position
            a--;
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

        // LEFT
        a = myPosition.getColumn();
        b = myPosition.getRow();
        captured = false;
        while(true)
        {
            // Coordinates for Possible Position
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

        // RIGHT
        a = myPosition.getColumn();
        b = myPosition.getRow();
        captured = false;
        while(true)
        {
            // Coordinates for Possible Position
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


        return possible_moves;
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

    public ChessMove exploreBoard(ChessBoard board, ChessPosition myPosition, int x, int y)
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

        ChessPosition pos = new ChessPosition(x, y);

        // Verify Position is within the Board Range and Position is Open
        if (!grid_positions.contains(pos) || blocked(board, pos))
        {
            return null;
        }

        ChessPiece piece = board.getPiece(pos);
        if (piece != null && piece.getTeamColor() != pieceColor)
        {
            captured = true;
        }

        // Initialize Possible Move
        return new ChessMove(myPosition, pos, type);
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

