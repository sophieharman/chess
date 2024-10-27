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
        List<List<Integer>> straightVectors = Arrays.asList(
                Arrays.asList(0, 1), Arrays.asList(0, -1),
                Arrays.asList(-1, 0), Arrays.asList(1, 0));

        List<List<Integer>> diagonalVectors = Arrays.asList(
                Arrays.asList(1, 1),
                Arrays.asList(-1, 1),
                Arrays.asList(1, -1),
                Arrays.asList(-1, -1));

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

        if(type == PieceType.BISHOP) {
            Collection<ChessMove> possibleMoves = exploreBoard(board, myPosition, diagonalVectors, grid);
            moves.addAll(possibleMoves);
        }
        else if(type == PieceType.KING) {
            // Coordinates of Neighboring Squares
            List<List<Integer>> coords = Arrays.asList(
                    Arrays.asList(0, 1),
                    Arrays.asList(0, -1),
                    Arrays.asList(1, 0),
                    Arrays.asList(-1, 0),
                    Arrays.asList(1, 1),
                    Arrays.asList(-1, -1),
                    Arrays.asList(-1, 1),
                    Arrays.asList(1, -1));

            Collection<ChessMove> possibleMoves = exploreBoard(board, myPosition, coords, grid);
            moves.addAll(possibleMoves);
        }
        else if(type == PieceType.KNIGHT) {
            List<List<Integer>> coords = Arrays.asList(
                    Arrays.asList(2, -1),
                    Arrays.asList(2, 1),
                    Arrays.asList(1, -2),
                    Arrays.asList(1, 2),
                    Arrays.asList(-1, -2),
                    Arrays.asList(-1, 2),
                    Arrays.asList(-2, -1),
                    Arrays.asList(-2, 1));

            Collection<ChessMove> newMoves = exploreBoard(board, myPosition, coords, grid);
            moves.addAll(newMoves);

        }
        else if(type == PieceType.PAWN)
        {
            Collection<ChessMove> possibleMoves = pawn(board, myPosition, grid);
            moves.addAll(possibleMoves);
        }
        else if(type == PieceType.QUEEN)
        {
            Collection<ChessMove> newMoves = exploreBoard(board, myPosition, straightVectors, grid);
            moves.addAll(newMoves);
            newMoves = exploreBoard(board, myPosition, diagonalVectors, grid);
            moves.addAll(newMoves);
        }
        else if(type == PieceType.ROOK)
        {
            Collection<ChessMove> newMoves = exploreBoard(board, myPosition, straightVectors, grid);
            moves.addAll(newMoves);
        }

        return moves;
    }

    public Collection<ChessMove> exploreBoard(ChessBoard board, ChessPosition myPosition, List<List<Integer>> coords, Collection<ChessPosition> grid)
    {
        // Initialize List of Possible Moves
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);


        for(List<Integer> coord: coords) {
            x = myPosition.getRow();
            y = myPosition.getColumn();
            while(true){
                ChessPosition newPosition = new ChessPosition(x + coord.get(0), y + coord.get(1));

                if(!grid.contains(newPosition))
                {
                    break;
                }

                ChessPiece occupant = board.getPiece(newPosition);
                if(occupant == null || pieceColor != occupant.getTeamColor())
                {
                    ChessMove newMoves = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMoves);
                    // Break if Captured
                    if (occupant != null) {
                        break;
                    }

                    if(myPiece.getPieceType() == PieceType.KING || myPiece.getPieceType() == PieceType.KNIGHT)
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }

                // Update x and y
                x = x + coord.get(0);
                y = y + coord.get(1);
            }

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
                Collection<ChessMove> newMoves = pawnExploreBoard(board,myPosition, newPosition, grid);
                moves.addAll(newMoves);
            }


            // Initial Move
            if(!moves.isEmpty())
            {
                if (x == 2)
                {
                    newPosition = new ChessPosition(x + 2, y);
                    Collection<ChessMove> newMoves = pawnExploreBoard(board, myPosition, newPosition, grid);
                    moves.addAll(newMoves);

                }
            }

            // Check Diagonals
            newPosition = new ChessPosition(x + 1, y + 1);
            Collection<ChessMove> diag1Moves = pawnCapture(board, myPosition, newPosition,grid, x, true);
            newPosition = new ChessPosition(x + 1, y - 1);
            Collection<ChessMove> diag2Moves = pawnCapture(board, myPosition, newPosition,grid, x, true);
            moves.addAll(diag1Moves);
            moves.addAll(diag2Moves);

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
                Collection<ChessMove> newMoves = pawnExploreBoard(board,myPosition, newPosition, grid);
                moves.addAll(newMoves);
            }

            // Initial Move
            if(x == 7)
            {
                if(!moves.isEmpty())
                {
                    newPosition = new ChessPosition(x - 2, y);
                    Collection<ChessMove> newMoves = pawnExploreBoard(board, myPosition, newPosition, grid);
                    moves.addAll(newMoves);
                }
            }

            // Check Diagonals
            newPosition = new ChessPosition(x - 1, y + 1);
            Collection<ChessMove> diag1Moves = pawnCapture(board, myPosition, newPosition,grid, x,true);
            newPosition = new ChessPosition(x - 1, y - 1);
            Collection<ChessMove> diag2Moves = pawnCapture(board, myPosition, newPosition,grid, x,true);
            moves.addAll(diag1Moves);
            moves.addAll(diag2Moves);

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
            ChessMove newMoves = new ChessMove(myPosition, newPosition, null);
            moves.add(newMoves);
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
                        ChessMove newMoves = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMoves);
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
                    ChessMove newMoves = new ChessMove(myPosition, newPosition, promoPiece);
                    moves.add(newMoves);
                }
            }
        }
        else
        {
            if((x - 1) == 1)
            {
                for(ChessPiece.PieceType promoPiece: pieceTypes)
                {
                    ChessMove newMoves = new ChessMove(myPosition, newPosition, promoPiece);
                    moves.add(newMoves);
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
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            moves.add(newMove);
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

        // Check if King's Location is in PossibleMoves
        ChessGame.TeamColor teamColor;
        if(occupant.getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        else
        {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        ChessPosition kingLoc = kingLocation(board, teamColor);
        for(ChessMove move: possibleMoves)
        {
            if(move.endPosition.equals(kingLoc))
            {
                return true;
            }
        }

    return false;
    }

    public ChessPosition kingLocation(ChessBoard board, ChessGame.TeamColor teamcolor)
    {
        // Iterate through all Board Positions
        for(int i = 1; i <= 8; i++)
        {
            for(int j = 1; j <= 8; j++)
            {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null)
                {
                    if(piece.getPieceType() == PieceType.KING)
                    {
                        // Return the Position of the King
                        if(piece.getTeamColor() == teamcolor)
                        {
                            return position;
                        }
                    }
                }
            }
        }
        return null;
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
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
