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
            List<List<Integer>> coords = Arrays.asList(
                    Arrays.asList(x+2,y+1),
                    Arrays.asList(x+2,y-1),
                    Arrays.asList(x-2,y+1),
                    Arrays.asList(x-2,y-1),
                    Arrays.asList(x+1,y+2),
                    Arrays.asList(x-1,y+2),
                    Arrays.asList(x+1,y-2),
                    Arrays.asList(x-1,y-2));

            for (List<Integer> coord : coords)
            {
                x = coord.get(0);
                y = coord.get(1);
                captured=false;
                ChessPosition pos = new ChessPosition(x, y);
                if(grid_positions.contains(pos))
                {
                    ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
                    moves.add(move);
                }

            }

            moves.removeIf(Objects::isNull);
        }
        else if (type == PieceType.ROOK)
        {
            moves = straight(board, myPosition, grid_positions);
        }
        else if (type == PieceType.PAWN)
        {
            moves = pawn(board, myPosition, grid_positions);
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
                captured=false;
                ChessPosition pos = new ChessPosition(x, y);
                if(grid_positions.contains(pos))
                {
                    ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
                    moves.add(move);
                }

            }

            moves.removeIf(Objects::isNull);
        }
        else if (type == PieceType.QUEEN)
        {
            moves = diagonal(board, myPosition, grid_positions);
            Collection<ChessMove> moves2 = straight(board, myPosition, grid_positions);
            moves.addAll(moves2);
        }

        return moves;
    }


    public boolean blocked(ChessBoard board, ChessPosition position)
    {

        ChessPiece piece = board.getPiece(position);

        if(piece == null)
        {
            return false;
        }
        return piece.getTeamColor() == pieceColor;
    }

    public Collection<ChessMove> straight(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid_positions)
    {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        // SOMETHING
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        captured = false;
        while(true)
        {
            // Coordinates for Possible Position
            x--;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions,  x ,y);
            moves.add(move);
        }

        // SOMETHING
        x = myPosition.getRow();
        y = myPosition.getColumn();
        captured = false;
        while(true)
        {
            x++;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
            moves.add(move);
        }


        // SOMETHING
        x = myPosition.getRow();
        y = myPosition.getColumn();
        captured = false;
        while(true)
        {
            y--;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions,  x ,y);
            moves.add(move);
        }

        // SOMETHING
        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            y++;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
            moves.add(move);
        }

        return moves;
    }

    public Collection<ChessMove> diagonal(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid_positions)
    {

        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        // TOP LEFT DIAGONAL
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        captured = false;
        while(true)
        {
            // Coordinates for Possible Position
            x--;
            y--;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
            moves.add(move);
        }

        // BOTTOM RIGHT DIAGONAL
        x = myPosition.getRow();
        y = myPosition.getColumn();
        captured = false;
        while(true)
        {
            x++;
            y++;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
            moves.add(move);
        }




        // BOTTOM LEFT DIAGONAL
        x = myPosition.getRow();
        y = myPosition.getColumn();
        captured = false;
        while(true)
        {
            x++;
            y--;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
            moves.add(move);
        }

        // TOP RIGHT DIAGONAL
        x = myPosition.getRow();
        y = myPosition.getColumn();
        while(true)
        {
            x--;
            y++;
            // Verify Position is within the Board Range and Position is Open
            ChessPosition pos = new ChessPosition(x, y);
            if (!grid_positions.contains(pos) || blocked(board, pos) || captured) {break;}
            ChessMove move = exploreBoard(board, myPosition, grid_positions, x ,y);
            moves.add(move);
        }

        return moves;
    }

    public Collection<ChessMove> pawn(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid_positions)
    {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        ChessPiece piece = board.getPiece(myPosition);

        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessPosition pos = new ChessPosition(x + 1, y);
            ChessPiece new_piece = board.getPiece(pos);
            if(new_piece == null)
            {
                ChessMove move = new ChessMove(myPosition, pos, null);
                moves.add(move);
            }
            else
            {
                // Check diags
                x = myPosition.getRow();
                y = myPosition.getColumn();
                ChessMove diag1 = exploreBoard(board, myPosition, grid_positions, x + 1, y + 1);
                x = myPosition.getRow();
                y = myPosition.getColumn();
                ChessMove diag2 = exploreBoard(board, myPosition, grid_positions, x + 1, y - 1);
                moves.add(diag1);
                moves.add(diag2);
                moves.removeIf(Objects::isNull);
                return moves;
            }
            // 1st move
            x = myPosition.getRow();
            y = myPosition.getColumn();

            if(myPosition.getRow() == 2)
            {

                x += 2;
                pos = new ChessPosition(x, y);
                piece = board.getPiece(pos);
                if(piece == null)
                {
                    ChessMove move = new ChessMove(myPosition, pos, null);
                    moves.add(move);
                }
            }

            // Check diags
            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessMove diag1 = exploreBoard(board, myPosition, grid_positions, x + 1, y + 1);
            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessMove diag2 = exploreBoard(board, myPosition, grid_positions, x + 1, y - 1);
            moves.add(diag1);
            moves.add(diag2);

        }
        else
        {
            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessPosition pos = new ChessPosition(x - 1, y);
            ChessPiece new_piece = board.getPiece(pos);
            if(new_piece == null)
            {
                ChessMove move = new ChessMove(myPosition, pos, null);
                moves.add(move);
            }
            else
            {
                // Check diags
                x = myPosition.getRow();
                y = myPosition.getColumn();
                ChessMove diag1 = exploreBoard(board, myPosition, grid_positions, x - 1, y + 1);
                x = myPosition.getRow();
                y = myPosition.getColumn();
                ChessMove diag2 = exploreBoard(board, myPosition, grid_positions, x - 1, y - 1);
                moves.add(diag1);
                moves.add(diag2);
                moves.removeIf(Objects::isNull);
                return moves;
            }

            // 1st move
            if(myPosition.getRow() == 7)
            {
                x = myPosition.getRow();
                y = myPosition.getColumn();

                x -= 2;
                pos = new ChessPosition(x, y);
                piece = board.getPiece(pos);
                if(piece == null)
                {
                    ChessMove move = new ChessMove(myPosition, pos, null);
                    moves.add(move);
                }
            }



            // Check diags
            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessMove diag1 = exploreBoard(board, myPosition, grid_positions, x - 1, y + 1);
            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessMove diag2 = exploreBoard(board, myPosition, grid_positions, x - 1, y - 1);
            moves.add(diag1);
            moves.add(diag2);
        }

        moves.removeIf(Objects::isNull);
        return moves;
    }

    public ChessMove exploreBoard(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid_positions, int x, int y)
    {

        ChessPosition pos = new ChessPosition(x, y);

        if(grid_positions.contains(pos)) {

            ChessPiece piece = board.getPiece(pos);

            if (piece != null && piece.getTeamColor() != pieceColor) {
                captured = true;
                if (type == PieceType.PAWN) {
                    return new ChessMove(myPosition, pos, null);
                }
            }
            if (piece != null && piece.getTeamColor() == pieceColor) {
                return null;
            }

            if (type == PieceType.PAWN) {
                return null;
            }

        }

        // Initialize Possible Move
        return new ChessMove(myPosition, pos, null);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", pieceColor=" + pieceColor +
                '}';
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

