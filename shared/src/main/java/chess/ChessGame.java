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
    private ChessBoard boardCloned = new ChessBoard();

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
        whiteTurn = (team == TeamColor.WHITE);
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

        Collection<ChessMove> possibleMoves;
        boolean endangered = false;
        if(occupant != null)
        {
            // Get Team Color
            TeamColor teamColor = occupant.getTeamColor();

            possibleMoves = occupant.pieceMoves(board, startPosition);
            for(ChessMove move: possibleMoves)
            {
                // Copy ChessBoard
                deepBoardCopy();

                // Remove Captured Piece
                ChessPiece checkForOccupant = board.getPiece(move.endPosition);
                if(checkForOccupant != null)
                {
                    boardCloned.removePiece(move.endPosition);
                }
                if(move.promotionPiece != null)
                {
                    occupant = new ChessPiece(teamColor, move.getPromotionPiece());
                }
                boardCloned.addPiece(move.endPosition, occupant);
                boardCloned.removePiece(startPosition);

                // If King is Not Endangered, Add to Valid Moves
                endangered = danger(boardCloned, teamColor);
                if(!endangered)
                {
                    valid.add(move);
                }

                if(endangered && occupant.getPieceType() == ChessPiece.PieceType.KING)
                {
                    Collection<ChessMove> rescueMoves = rescueKing(teamColor);
                    for(ChessMove rescueMove: rescueMoves)
                    {
                        valid.add(rescueMove);
                    }
                }

            }
        }

        return valid;
    }

    public Collection<ChessMove> rescueKing(TeamColor teamColor)
    {
        // Initialize Collection to Store Valid Moves
        Collection<ChessMove> valid = new ArrayList<ChessMove>();

        // Grab all Team Pieces
        Collection<ChessPosition> teamPositions =  oppTeamLocations(boardCloned, teamColor);
        for(ChessPosition position: teamPositions)
        {
            // Get Possible Moves for each Piece
            ChessPiece occupant = board.getPiece(position);
            Collection<ChessMove> possibleMoves = occupant.pieceMoves(board, position);
            for(ChessMove move: possibleMoves)
            {
                // Perform Move on Copy of ChessBoard
                deepBoardCopy();
                // Remove Captured Piece
                ChessPiece checkForOccupant = board.getPiece(move.endPosition);
                if(checkForOccupant != null)
                {
                    boardCloned.removePiece(move.endPosition);
                }
                if(move.promotionPiece != null)
                {
                    occupant = new ChessPiece(teamColor, move.getPromotionPiece());
                }
                boardCloned.addPiece(move.endPosition, occupant);
                boardCloned.removePiece(position);

                // If Move Removes King from Check, Add to Valid Moves
                boolean endangered = danger(boardCloned, teamColor);
                if(!endangered)
                {
                    valid.add(move);
                }

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
            throw new InvalidMoveException("Invalid Move: Chess Rules Violated");
        }

        // Throw Exception if Move is Empty
        if(move.startPosition == null)
        {
            throw new InvalidMoveException("Invalid Move: No Piece Present");
        }

        // Throw Exception if Move is Out of Turn
        ChessPiece currOccupant = board.getPiece(move.startPosition);
        TeamColor currColor = currOccupant.getTeamColor();
        if(!currColor.equals(getTeamTurn()))
        {
            throw new InvalidMoveException("Invalid Move: Out of Turn");
        }


        // Remove Captured Pieces
        ChessPiece occupant = board.getPiece(endPosition);
        if(occupant != null)
        {
            board.removePiece(endPosition);
        }

        // Team Color
        TeamColor teamColor;
        if(whiteTurn)
        {
            teamColor = TeamColor.WHITE;
        }
        else
        {
            teamColor = TeamColor.BLACK;
        }

        // Make Move
        occupant = board.getPiece(startPosition);
        if(move.promotionPiece != null)
        {
            occupant = new ChessPiece(teamColor, move.getPromotionPiece());
        }
        board.addPiece(endPosition, occupant);
        board.removePiece(startPosition);

        // Set Team Turn
        if(whiteTurn)
        {
            teamColor = TeamColor.BLACK;
        }
        else
        {
            teamColor = TeamColor.WHITE;
        }
        setTeamTurn(teamColor);

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        // Determine if King is in Check
        return danger(board, teamColor);
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
        return valid.isEmpty() && danger(board, teamColor);
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
        // Look at the moves of all pieces!!!!
        ChessPosition position = kingLocation(teamColor);
        Collection<ChessMove> valid = validMoves(position);

        // Check if King is in Under Attack and Able to Escape
        return valid.isEmpty() && !danger(board, teamColor);
    }

    public boolean danger(ChessBoard boardCopy, TeamColor teamColor)
    {
        // Grab Positions of All Opponents
        Collection<ChessPosition> teamPositions = oppTeamLocations(boardCopy, teamColor);

        for(ChessPosition position: teamPositions)
        {
            ChessPiece oppOccupant = boardCopy.getPiece(position);
            if (oppOccupant != null)
            {
                if(oppOccupant.kingCaptured(boardCopy, position))
                {
                    return true;
                }
            }

        }
        return false;
    }

    public Collection<ChessPosition> oppTeamLocations(ChessBoard boardCopy, TeamColor oppTeamColor)
    {
        Collection<ChessPosition> teamPositions = new ArrayList<ChessPosition>();

        for(int i = 1; i <= 8; i++)
        {
            for(int j = 1; j <= 8; j++)
            {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = boardCopy.getPiece(position);
                if(piece != null && piece.getTeamColor() != oppTeamColor)
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
                if(piece != null)
                {
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
        }
        return null;
    }

    public void deepBoardCopy()
    {
        ChessBoard boardCopy = new ChessBoard();
        for(int i = 1; i <= 8; i++)
        {
            for(int j = 1; j <= 8; j++)
            {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece oldPiece = board.getPiece(position);
                if(oldPiece != null)
                {
                    ChessPiece piece = new ChessPiece(oldPiece.getTeamColor(), oldPiece.getPieceType());
                    boardCopy.addPiece(position, piece);
                }

            }
        }

        boardCloned = boardCopy;
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
