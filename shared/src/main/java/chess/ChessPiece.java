package chess;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece
{
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    private boolean isKingCaptured = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        this.pieceColor = pieceColor;
        this.type = type;
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

        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        if(type == PieceType.BISHOP)
        {
            Collection<ChessMove> possible_moves = diagonal(board, myPosition, grid);
            moves.addAll(possible_moves);

        }
        else if(type == PieceType.KING)
        {
            // Straight Moves
            ChessPosition straight1 = new ChessPosition(x, y + 1);
            ChessPosition straight2 = new ChessPosition(x, y - 1);
            ChessPosition straight3 = new ChessPosition(x - 1, y);
            ChessPosition straight4 = new ChessPosition(x + 1, y);
            // Diagonal Moves
            ChessPosition diag1 = new ChessPosition(x + 1, y + 1);
            ChessPosition diag2 = new ChessPosition(x - 1, y - 1);
            ChessPosition diag3 = new ChessPosition(x - 1, y + 1);
            ChessPosition diag4 = new ChessPosition(x + 1, y - 1);

            // Explore Possible Positions
            Collection<ChessMove> new_move = exploreBoard(board, myPosition, straight1, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, straight1, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, straight2, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, straight3, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, straight4, grid);
            moves.addAll(new_move);

            new_move = exploreBoard(board, myPosition, diag1, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, diag2, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, diag3, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, diag4, grid);
            moves.addAll(new_move);
        }
        else if(type == PieceType.KNIGHT)
        {
            // Positions to Explore
            ChessPosition move1 = new ChessPosition(x + 2, y - 1);
            ChessPosition move2 = new ChessPosition(x + 2, y + 1);
            ChessPosition move3 = new ChessPosition(x + 1, y - 2);
            ChessPosition move4 = new ChessPosition(x + 1, y + 2);
            ChessPosition move5 = new ChessPosition(x - 1, y - 2);
            ChessPosition move6 = new ChessPosition(x - 1, y + 2);
            ChessPosition move7 = new ChessPosition(x - 2, y - 1);
            ChessPosition move8 = new ChessPosition(x - 2, y + 1);

            Collection<ChessMove> new_move = exploreBoard(board, myPosition, move1, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, move2, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, move3, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, move4, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, move5, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, move6, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, move7, grid);
            moves.addAll(new_move);
            new_move = exploreBoard(board, myPosition, move8, grid);
            moves.addAll(new_move);

        }
        else if(type == PieceType.PAWN)
        {
            Collection<ChessMove> possible_moves = pawn(board, myPosition, grid);
            moves.addAll(possible_moves);
        }
        else if(type == PieceType.QUEEN)
        {
            Collection<ChessMove> possible_moves = straight(board, myPosition, grid);
            moves.addAll(possible_moves);
            possible_moves = diagonal(board, myPosition, grid);
            moves.addAll(possible_moves);
        }
        else if(type == PieceType.ROOK)
        {
            Collection<ChessMove> possible_moves = straight(board, myPosition, grid);
            moves.addAll(possible_moves);
        }

        return moves;
    }

    public Collection<ChessMove> straight(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid)
    {
        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        while(true)
        {
            x++;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            y++;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            x--;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            y--;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        return moves;
    }


    public Collection<ChessMove> diagonal(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid)
    {
        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        while(true)
        {
            x++;
            y++;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            x--;
            y--;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            x++;
            y--;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            x--;
            y++;
            ChessPosition newPosition = new ChessPosition(x, y);
            // Verify New Position is in Grid
            if(!grid.contains(newPosition))
            {
                break;
            }
            // Verify Position is Not Blocked
            ChessPiece occupant = board.getPiece(newPosition);
            if(occupant != null)
            {
                boolean captured = capture(board, newPosition);
                if(captured)
                {
                    // Add New Move
                    ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                    moves.add(new_move);

                    if(occupant.getPieceType() == PieceType.KING)
                    {
                        isKingCaptured = true;
                    }
                }
                break;
            }
            // Add New Move
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        return moves;
    }

    public boolean capture(ChessBoard board, ChessPosition newPosition)
    {
        ChessPiece occupant = board.getPiece(newPosition);
        if(occupant != null)
        {
            if(occupant.getPieceType() == PieceType.KING)
            {
                isKingCaptured = true;
            }

            return pieceColor != occupant.getTeamColor();
        }
        return false;
    }

    public Collection<ChessMove> exploreBoard(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, Collection<ChessPosition> grid)
    {
        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        if(!grid.contains(newPosition))
        {
            return moves;
        }

        ChessPiece occupant = board.getPiece(newPosition);
        if(occupant == null || pieceColor != occupant.getTeamColor())
        {
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }
        return moves;
    }

    public Collection<ChessMove> pawn(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid)
    {
        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        // Current Position Coordinates
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        if(pieceColor == ChessGame.TeamColor.WHITE)
        {
            // Move 1 Space
            ChessPosition newPosition = new ChessPosition(x + 1, y);
            // Check for Promotion Moves
            Collection<ChessMove> promoMoves = promote(myPosition, newPosition, x);
            moves.addAll(promoMoves);
            if(promoMoves.isEmpty())
            {
                Collection<ChessMove> new_moves = pawnExploreBoard(board,myPosition, newPosition, grid);
                moves.addAll(new_moves);
            }


            // Initial Move
            if(!moves.isEmpty())
            {
                if (x == 2)
                {
                    newPosition = new ChessPosition(x + 2, y);
                    Collection<ChessMove> new_moves = pawnExploreBoard(board, myPosition, newPosition, grid);
                    moves.addAll(new_moves);

                }
            }

            // Check Diagonals
            newPosition = new ChessPosition(x + 1, y + 1);
            Collection<ChessMove> diag1_moves = pawnCapture(board, myPosition, newPosition,grid, x, true);
            newPosition = new ChessPosition(x + 1, y - 1);
            Collection<ChessMove> diag2_moves = pawnCapture(board, myPosition, newPosition,grid, x, true);
            moves.addAll(diag1_moves);
            moves.addAll(diag2_moves);

        }
        else
        {
            // Move 1 Space
            ChessPosition newPosition = new ChessPosition(x - 1, y);
            // Check for Promotion Moves
            Collection<ChessMove> promoMoves = promote(myPosition, newPosition, x);
            moves.addAll(promoMoves);
            if(promoMoves.isEmpty())
            {
                Collection<ChessMove> new_moves = pawnExploreBoard(board,myPosition, newPosition, grid);
                moves.addAll(new_moves);
            }

            // Initial Move
            if(x == 7)
            {
                if(!moves.isEmpty())
                {
                    newPosition = new ChessPosition(x - 2, y);
                    Collection<ChessMove> new_moves = pawnExploreBoard(board, myPosition, newPosition, grid);
                    moves.addAll(new_moves);
                }
            }

            // Check Diagonals
            newPosition = new ChessPosition(x - 1, y + 1);
            Collection<ChessMove> diag1_moves = pawnCapture(board, myPosition, newPosition,grid, x,true);
            newPosition = new ChessPosition(x - 1, y - 1);
            Collection<ChessMove> diag2_moves = pawnCapture(board, myPosition, newPosition,grid, x,true);
            moves.addAll(diag1_moves);
            moves.addAll(diag2_moves);

        }

        moves.removeIf(Objects::isNull);
        return moves;
    }

    public Collection<ChessMove> pawnCapture(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, Collection<ChessPosition> grid, int x, boolean diagonal)
    {
        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        // Verify New Position is in Grid
        if(!grid.contains(newPosition))
        {
            return moves;
        }

        ChessPiece occupant = board.getPiece(newPosition);
        if(occupant == null)
        {
            // Pawns Attack on Diagonals Only
            if(type == PieceType.PAWN)
            {
                return moves;
            }
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }

        // If Occupant is an Opponent -- Add ChessMove
        if(pieceColor != occupant.getTeamColor())
        {
            if(type == PieceType.PAWN)
            {
                // Pawns Attack on Diagonals Only
                if(diagonal)
                {
                    // Check for Promotion Moves
                    Collection<ChessMove> promoMoves = promote(myPosition, newPosition, x);
                    moves.addAll(promoMoves);
                    if(promoMoves.isEmpty())
                    {
                        ChessMove new_move = new ChessMove(myPosition, newPosition, null);
                        moves.add(new_move);
                    }
                }
                return moves;
            }
        }

        return moves;
    }

    public Collection<ChessMove> promote(ChessPosition myPosition, ChessPosition newPosition, int x)
    {
        // List of Possible Promotion Pieces
        List<ChessPiece.PieceType> pieceTypes = new ArrayList<PieceType>();
        pieceTypes.add(PieceType.BISHOP);
        pieceTypes.add(PieceType.KNIGHT);
        pieceTypes.add(PieceType.QUEEN);
        pieceTypes.add(PieceType.ROOK);

        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        if(pieceColor == ChessGame.TeamColor.WHITE)
        {
            if((x + 1) == 8)
            {
                for(ChessPiece.PieceType promoPiece: pieceTypes)
                {
                    ChessMove new_move = new ChessMove(myPosition, newPosition, promoPiece);
                    moves.add(new_move);
                }
            }
        }
        else
        {
            if((x - 1) == 1)
            {
                for(ChessPiece.PieceType promoPiece: pieceTypes)
                {
                    ChessMove new_move = new ChessMove(myPosition, newPosition, promoPiece);
                    moves.add(new_move);
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> pawnExploreBoard(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, Collection<ChessPosition> grid)
    {
        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        if(!grid.contains(newPosition))
        {
            return null;
        }

        ChessPiece occupant = board.getPiece(newPosition);
        if(occupant == null)
        {
            ChessMove new_move = new ChessMove(myPosition, newPosition, null);
            moves.add(new_move);
        }
        else
        {

            return moves;
        }

        return moves;
    }

    public boolean kingCaptured(ChessBoard board, ChessPosition position)
    {
        // Call PieceMoves
        ChessPiece occupant = board.getPiece(position);
        Collection<ChessMove> possibleMoves = occupant.pieceMoves(board, position);

        if(isKingCaptured)
        {
            return true;
        }
        return false;

    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
