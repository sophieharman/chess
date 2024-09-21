package chess;
import java.util.*;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

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
                    Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
                    moves.addAll(additional_moves);
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
                    Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
                    moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> additional_moves = exploreBoard(board, myPosition, grid_positions, null, x ,y);
            moves.addAll(additional_moves);
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
            Collection<ChessMove> promotion_moves = promote(myPosition, pos, piece,x+1);
            moves.addAll(promotion_moves);
            }
            else
            {
                // Check diags
                x = myPosition.getRow();
                y = myPosition.getColumn();
                Collection<ChessMove> diag1 = exploreBoard(board, myPosition, grid_positions, null,x + 1, y + 1);
                x = myPosition.getRow();
                y = myPosition.getColumn();
                Collection<ChessMove> diag2 = exploreBoard(board, myPosition, grid_positions, null, x + 1, y - 1);
                moves.addAll(diag1);
                moves.addAll(diag2);
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
            Collection<ChessMove> diag1 = exploreBoard(board, myPosition, grid_positions, piece, x + 1, y + 1);
            x = myPosition.getRow();
            y = myPosition.getColumn();
            Collection<ChessMove> diag2 = exploreBoard(board, myPosition, grid_positions, piece, x + 1, y - 1);
            moves.addAll(diag1);
            moves.addAll(diag2);

        }
        else
        {
            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessPosition pos = new ChessPosition(x - 1, y);
            ChessPiece new_piece = board.getPiece(pos);
            if(new_piece == null)
            {
                Collection<ChessMove> promotion_moves = promote(myPosition, pos, piece,x-1);
                moves.addAll(promotion_moves);
            }
            else
            {
                // Check diags
                x = myPosition.getRow();
                y = myPosition.getColumn();
                Collection<ChessMove> diag1 = exploreBoard(board, myPosition, grid_positions, null,x - 1, y + 1);
                x = myPosition.getRow();
                y = myPosition.getColumn();
                Collection<ChessMove> diag2 = exploreBoard(board, myPosition, grid_positions, null, x - 1, y - 1);
                moves.addAll(diag1);
                moves.addAll(diag2);
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

            x = myPosition.getRow();
            y = myPosition.getColumn();
            ChessPiece og_piece = board.getPiece(myPosition);

            // Check diags
            x = myPosition.getRow();
            y = myPosition.getColumn();
            Collection<ChessMove> diag1 = exploreBoard(board, myPosition, grid_positions, null,x - 1, y + 1);
            x = myPosition.getRow();
            y = myPosition.getColumn();
            Collection<ChessMove> diag2= exploreBoard(board, myPosition, grid_positions, og_piece,x - 1, y - 1);
            moves.addAll(diag1);
            moves.addAll(diag2);
        }

        moves.removeIf(Objects::isNull);
        return moves;
    }


    public Collection<ChessMove> promote(ChessPosition myPosition, ChessPosition newPosition, ChessPiece piece, int x)
    {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        List<PieceType> pieceTypes = Arrays.asList(
                PieceType.ROOK,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.QUEEN);

        if(piece.getTeamColor() == WHITE)
        {
            if(x == 8)
            {
                for(ChessPiece.PieceType promotionPiece : pieceTypes)
                {
                    ChessMove move = new ChessMove(myPosition, newPosition, promotionPiece);
                    moves.add(move);
                }
                return moves;
            }
            ChessMove move = new ChessMove(myPosition, newPosition,null);
            moves.add(move);
            return moves;

        }
        else
        {
            if(x == 1)
            {
                for(ChessPiece.PieceType promotionPiece : pieceTypes)
                {
                    ChessMove move = new ChessMove(myPosition, newPosition, promotionPiece);
                    moves.add(move);
                }
                return moves;
            }
            ChessMove move = new ChessMove(myPosition, newPosition,null);
            moves.add(move);
            return moves;
        }
    }

    public Collection<ChessMove> exploreBoard(ChessBoard board, ChessPosition myPosition, Collection<ChessPosition> grid_positions, ChessPiece og_piece, int x, int y)
    {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        ChessPosition pos = new ChessPosition(x, y);

        if(grid_positions.contains(pos))
        {

            ChessPiece piece = board.getPiece(pos);

            if (piece != null && piece.getTeamColor() != pieceColor)
            {
                captured = true;
                if (type == PieceType.PAWN)
                {
                    if(x == 1 && pieceColor == BLACK)
                    {
                        Collection<ChessMove> promotion_moves = promote(myPosition, pos, og_piece,x);
                        moves.addAll(promotion_moves);
                        return moves;
                    }

                    Collection<ChessMove> promotion_moves = promote(myPosition, pos, piece,x);
                    moves.addAll(promotion_moves);
                    return moves;
                }
            }
            if (piece != null && piece.getTeamColor() == pieceColor) {
                return moves;
            }

            if (type == PieceType.PAWN)
            {
                return moves;
            }

        }

        // Initialize Possible Move
        ChessMove move = new ChessMove(myPosition, pos, null);
        moves.add(move);
        return moves;
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

