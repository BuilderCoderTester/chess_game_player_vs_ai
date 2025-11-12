package org.example;

import javax.swing.JOptionPane;
import java.awt.Component;

public class PawnPromotion {

    /**
     * Handles pawn promotion when a pawn reaches the opposite end of the board
     * @param piece The piece being moved (should be a pawn)
     * @param toRow The destination row
     * @param toCol The destination column
     * @param parent The parent component for the dialog (usually your ChessGame panel)
     * @return The promoted piece name, or the original piece if no promotion needed
     */
    public static String handlePawnPromotion(String piece, int toRow, int toCol, Component parent) {
        // Check if it's a pawn that reached the promotion rank
        if (piece.endsWith("_pawn")) {
            boolean isWhitePawn = piece.startsWith("w_");
            boolean isBlackPawn = piece.startsWith("b_");
            
            // White pawns promote on row 0, black pawns promote on row 7
            if ((isWhitePawn && toRow == 0) || (isBlackPawn && toRow == 7)) {
                return showPromotionDialog(piece, parent);
            }
        }
        
        return piece; // No promotion needed
    }

    /**
     * Shows a dialog for the player to choose promotion piece
     * @param pawnPiece The pawn piece being promoted
     * @param parent The parent component for the dialog
     * @return The chosen promotion piece
     */
    public static String showPromotionDialog(String pawnPiece, Component parent) {
        String color = pawnPiece.startsWith("w_") ? "w_" : "b_";
        String colorName = pawnPiece.startsWith("w_") ? "White" : "Black";
        
        // Create promotion options
        String[] options = {
            "Queen",
            "Rook", 
            "Bishop",
            "Knight"
        };
        
        // Show dialog
        int choice = JOptionPane.showOptionDialog(
            parent,  // Fixed: now uses the parent parameter instead of 'this'
            colorName + " pawn promotion!\nChoose your piece:",
            "Pawn Promotion",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0] // Default to Queen
        );
        
        // Return the chosen piece (default to queen if dialog is closed)
        switch (choice) {
            case 0: return color + "queen";
            case 1: return color + "rook";
            case 2: return color + "bishop";
            case 3: return color + "knight";
            default: return color + "queen"; // Default to queen
        }
    }

    /**
     * Method to check if a move results in pawn promotion
     * Useful for AI or move validation
     */
    public static boolean isPromotionMove(String piece, int fromRow, int fromCol, int toRow, int toCol) {
        if (!piece.endsWith("_pawn")) {
            return false;
        }
        
        boolean isWhitePawn = piece.startsWith("w_");
        boolean isBlackPawn = piece.startsWith("b_");
        
        return (isWhitePawn && toRow == 0) || (isBlackPawn && toRow == 7);
    }
}

// Updated handleClick method for your ChessGame class:
// Replace your existing handleClick method with this version

/*
private void handleClick(int row, int col) {
    if (selectedRow == -1) {
        // First click: select piece
        String piece = board[row][col];
        if (!piece.isEmpty() && ((whiteTurn && piece.startsWith("w")) || (!whiteTurn && piece.startsWith("b")))) {
            selectedRow = row;
            selectedCol = col;
            repaint();
        }
    } else {
        // Second click: try move
        String selectedPiece = board[selectedRow][selectedCol];
        if (isValidMove(selectedPiece, selectedRow, selectedCol, row, col)) {
            // Handle pawn promotion before placing the piece
            String finalPiece = PawnPromotion.handlePawnPromotion(selectedPiece, row, col, this);
            
            // Make the move
            board[row][col] = finalPiece;
            board[selectedRow][selectedCol] = "";
            whiteTurn = !whiteTurn;
            
            // Check for game end
            if (selectedPiece.startsWith("w")) {
                checkWin("b_king");
            } else {
                checkWin("w_king");
            }
        }
        selectedRow = -1;
        selectedCol = -1;
        repaint();
    }
}
*/