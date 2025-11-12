package org.example;

import java.util.HashMap;
import java.util.Map;

public class ChessLogic {

    private static Map<String, int[]> eliminatedPieces = new HashMap<>();

    public static void eliminatedPiece(int row, int col, String target) {
        eliminatedPieces.put(target, new int[] { row, col });
        // Removed debug output for cleaner code
    }

    // Helper method to check if destination square is valid (empty or contains opponent piece)
    private static boolean isValidDestination(String[][] board, String movingPiece, int toRow, int toCol) {
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false; // Out of bounds
        }
        
        String targetSquare = board[toRow][toCol];
        if (targetSquare.equals("")) {
            return true; // Empty square
        }
        
        // Check if it's an opponent's piece
        String movingColor = movingPiece.substring(0, 1);
        String targetColor = targetSquare.substring(0, 1);
        return !movingColor.equals(targetColor);
    }

    // ---------------- BLACK PAWN ----------------
    public static boolean blackPawnLegalMove(String[][] board, int row, int col, int toRow, int toCol) {
        // Move forward one square
        if (toCol == col && toRow == row + 1 && board[toRow][toCol].equals("")) {
            return true;
        }
        // Move forward two squares from starting position
        if (toCol == col && toRow == row + 2 && row == 1 &&
                board[row + 1][col].equals("") && board[toRow][toCol].equals("")) {
            return true;
        }
        // Capture diagonally
        if (Math.abs(toCol - col) == 1 && toRow == row + 1 && 
            !board[toRow][toCol].equals("") && board[toRow][toCol].startsWith("w")) {
            return true;
        }
        return false;
    }

    // ---------------- WHITE PAWN ----------------
    public static boolean whitePawnLegalMove(String[][] board, int row, int col, int toRow, int toCol) {
        // Move forward by 1 (must be empty)
        if (toCol == col && toRow == row - 1 && board[toRow][toCol].equals("")) {
            return true;
        }

        // Move forward by 2 from starting rank (must be empty path)
        if (toCol == col && toRow == row - 2 && row == 6
                && board[row - 1][col].equals("") && board[toRow][toCol].equals("")) {
            return true;
        }

        // Capture diagonally (must contain opponent piece)
        if (Math.abs(toCol - col) == 1 && toRow == row - 1
                && !board[toRow][toCol].equals("") && board[toRow][toCol].startsWith("b")) {
            return true;
        }

        return false;
    }

    // ---------------- KNIGHT ----------------
    public static boolean isKnightMoveValid(String[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(toRow - fromRow);
        int dc = Math.abs(toCol - fromCol);

        // Must be L-shape: (2,1) or (1,2)
        if (!((dr == 2 && dc == 1) || (dr == 1 && dc == 2))) {
            return false;
        }

        // Check if destination is valid (empty or opponent piece)
        String fromPiece = board[fromRow][fromCol];
        return isValidDestination(board, fromPiece, toRow, toCol);
    }

    // ---------------- BISHOP ----------------
    public static boolean isBishopMoveValid(String[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        // 1. Bishop must move diagonally
        if (Math.abs(toRow - fromRow) != Math.abs(toCol - fromCol)) {
            return false;
        }

        // 2. Check if destination is valid
        String fromPiece = board[fromRow][fromCol];
        if (!isValidDestination(board, fromPiece, toRow, toCol)) {
            return false;
        }

        // 3. Determine direction of movement
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;

        // 4. Check every square along the path (excluding the destination)
        int r = fromRow + rowStep;
        int c = fromCol + colStep;
        while (r != toRow || c != toCol) {
            if (!board[r][c].equals("")) {
                return false; // Path blocked
            }
            r += rowStep;
            c += colStep;
        }

        return true;
    }

    // ---------------- ROOK ----------------
    public static boolean isRookMoveValid(String[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        // Must move horizontally or vertically
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }

        // Check if destination is valid
        String fromPiece = board[fromRow][fromCol];
        if (!isValidDestination(board, fromPiece, toRow, toCol)) {
            return false;
        }

        // Check path is clear
        int rowStep = (toRow == fromRow) ? 0 : (toRow > fromRow ? 1 : -1);
        int colStep = (toCol == fromCol) ? 0 : (toCol > fromCol ? 1 : -1);

        int r = fromRow + rowStep;
        int c = fromCol + colStep;
        while (r != toRow || c != toCol) {
            if (!board[r][c].equals("")) {
                return false;
            }
            r += rowStep;
            c += colStep;
        }

        return true;
    }

    // ---------------- QUEEN ----------------
    public static boolean isQueenMoveValid(String[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        return isBishopMoveValid(board, fromRow, fromCol, toRow, toCol) ||
               isRookMoveValid(board, fromRow, fromCol, toRow, toCol);
    }

    // ---------------- KING ----------------
    public static boolean isKingMoveValid(String[][] board, int fromRow, int fromCol, int toRow, int toCol, String piece) {
        // King can move one square in any direction
        if (Math.abs(toRow - fromRow) > 1 || Math.abs(toCol - fromCol) > 1) {
            return false;
        }

        // Can't move to same square
        if (fromRow == toRow && fromCol == toCol) {
            return false;
        }

        // Check if destination is valid
        return isValidDestination(board, piece, toRow, toCol);
    }

    public static boolean selectedPieceLegalMove(String[][] board, String piece, int fromRow, int fromCol, int toRow, int toCol) {
        // Bounds checking
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false;
        }

        switch (piece) {
            case "b_pawn":
                return blackPawnLegalMove(board, fromRow, fromCol, toRow, toCol);

            case "w_pawn":
                return whitePawnLegalMove(board, fromRow, fromCol, toRow, toCol);

            case "b_knight":
            case "w_knight":
                return isKnightMoveValid(board, fromRow, fromCol, toRow, toCol);
                
            case "b_bishop":
            case "w_bishop":
                return isBishopMoveValid(board, fromRow, fromCol, toRow, toCol);

            case "b_rook":
            case "w_rook":
                return isRookMoveValid(board, fromRow, fromCol, toRow, toCol);
                
            case "b_queen":
            case "w_queen":
                return isQueenMoveValid(board, fromRow, fromCol, toRow, toCol);
                
            case "b_king":
            case "w_king":
                return isKingMoveValid(board, fromRow, fromCol, toRow, toCol, piece);
                
            default:
                return false;
        }
    }
}