package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame
{
    private boolean whiteTurn = true;
    private ChessBoard board = new ChessBoard();

    public ChessGame()
    {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn()
    {
        if(whiteTurn)
        {
            return TeamColor.WHITE;
        }
        return TeamColor.BLACK;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team)
    {
        if(team == TeamColor.WHITE)
        {
            boolean whiteTurn = true;
        }
        else
        {
            boolean whiteTurn = false;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor
    {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition)
    {
        // Initialize Collection to Store Valid Moves
        Collection<ChessMove> valid = new ArrayList<ChessMove>();

        // Get PieceType at Start Position
        ChessPiece occupant = board.getPiece(startPosition);

        // Find Possible Move Positions (Without Accounting for Game Logic)
        Collection<ChessMove> possibleMoves = occupant.pieceMoves(board, startPosition);


        for(ChessMove move: possibleMoves)
        {
            // Verify that the King is Not Endangered
            boolean endangered = danger(occupant.getTeamColor());
            if(!endangered)
            {
                valid.add(move);
            }
        }

        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException
    {
        // Extract Start Position and End Position
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        // Determine Valid Moves
        Collection<ChessMove> valid = validMoves(startPosition);

        // Throw Exception if Move is Invalid
        if(!valid.contains(move))
        {
            throw new InvalidMoveException("Invalid Move");
        }

        // Make Move
        ChessPiece piece = board.getPiece(startPosition);
        board.addPiece(endPosition, piece);
        board.removePiece(startPosition);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        // Determine King Position and Valid Moves
        ChessPosition position = kingLocation(teamColor);
        Collection<ChessMove> valid = validMoves(position);

        // Check if King is in Under Attack and Able to Escape
        return !valid.isEmpty() && danger(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor)
    {
        // Determine King Position and Valid Moves
        ChessPosition position = kingLocation(teamColor);
        Collection<ChessMove> valid = validMoves(position);

        // Check if King is in Under Attack and Able to Escape
        return valid.isEmpty() && danger(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor)
    {
        // Determine King Position and Valid Moves
        ChessPosition position = kingLocation(teamColor);
        Collection<ChessMove> valid = validMoves(position);

        // Check if King is in Under Attack and Able to Escape
        return valid.isEmpty() && !danger(teamColor);
    }

    public boolean danger(TeamColor teamColor)
    {
        TeamColor oppColor;
        if(teamColor == TeamColor.WHITE)
        {
            oppColor = TeamColor.BLACK;
        }
        else
        {
            oppColor = TeamColor.WHITE;
        }
        // Grab Positions of All Opponents

        Collection<ChessPosition> teamPositions = oppTeamLocations(oppColor);

        for(ChessPosition position: teamPositions)
        {
            ChessPiece occupant = board.getPiece(position);
            if(occupant.kingCaptured(board, position))
            {
                return true;
            }
        }
        return false;
    }

    public Collection<ChessPosition> oppTeamLocations(TeamColor teamColor)
    {
        Collection<ChessPosition> teamPositions = new ArrayList<ChessPosition>();

        for(int i = 1; i <= 8; i++)
        {
            for(int j = 1; j <= 8; j++)
            {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() != teamColor)
                {
                    teamPositions.add(position);
                }
            }
        }
        return teamPositions;
    }

    public ChessPosition kingLocation(TeamColor teamcolor)
    {
        // Iterate through all Board Positions
        for(int i = 1; i <= 8; i++)
        {
            for(int j = 1; j <= 8; j++)
            {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece.getPieceType() == ChessPiece.PieceType.KING)
                {
                    // Return the Position of the King
                      if(piece.getTeamColor() == teamcolor)
                      {
                          return position;
                      }
                }

            }
        }
        return null;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board)
    {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard()
    {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{}";
    }
}
