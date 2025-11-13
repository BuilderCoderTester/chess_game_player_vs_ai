package org.example;


//Package import
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import org.example.GUI.ChessBoardGUI;
import org.example.GUI.ChessFEN;

public class ChessGame extends JPanel {

//    local variable declaration
    private static final int WIDTH = 480, HEIGHT = 518;
    private boolean whiteTurn = true;
    private HybridChessAI hybridAI;
    public ChessFEN cfen;
    public ChessBoardGUI boardGui;
    boolean WhiteIsAi , BlackIsAi;

//    defalut constructor
    public ChessGame(){}
//    parametrized constructor
    public ChessGame(boolean WhiteIsAi , boolean BlackIsAi ) {
        this.WhiteIsAi = WhiteIsAi;
        this.BlackIsAi = BlackIsAi;
        this.boardGui = new ChessBoardGUI();
        this.cfen = new ChessFEN(this.boardGui);

        if(WhiteIsAi == false && BlackIsAi == true){
            try {
                GeminiMoveGenerator gemini = new GeminiMoveGenerator();
                System.out.println("the gemini  :" + gemini);
                StockFishEngine stockfish = new StockFishEngine("/media/anurag/1692710A9270F01B/stockfish-ubuntu-x86-64-avx2/stockfish/stockfish-ubuntu-x86-64-avx2");
                System.out.println("the gemini  :" + stockfish);
                hybridAI = new HybridChessAI(gemini, stockfish);
            } catch (Exception e) {
                e.printStackTrace();
            }
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int row = e.getY() / boardGui.SQUARE_SIZE;
                    int col = e.getX() / boardGui.SQUARE_SIZE;
                    System.out.println("mouse clicked");
                    handleClick(row, col);
                }
            });
        } else if (WhiteIsAi == false && BlackIsAi == false) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = e.getY() / boardGui.SQUARE_SIZE;
                int col = e.getX() / boardGui.SQUARE_SIZE;
                System.out.println("mouse clicked");
                handelClickManual(row, col);
            }
        });

        }

    }
    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("heh");
        super.paintComponent(g);
        boardGui.drawBoard(g);
        boardGui.drawPieces(g);
        boardGui.drawSelection(g);

    }
private void handelClickManual(int row , int col){
        if(boardGui.selectedRow == -1){
            String piece = boardGui.board[row][col];
            if(!piece.isEmpty() && ((whiteTurn && piece.startsWith("w")) || (!whiteTurn && piece.startsWith("b")))){
                boardGui.selectedRow = row;
                boardGui.selectedCol = col;
                repaint();
            }
        }
        else {
            String selectedPiece = boardGui.board[boardGui.selectedRow][boardGui.selectedCol];
            if (isValidMove(selectedPiece, boardGui.selectedRow, boardGui.selectedCol, row, col)) {
                String finalPiece = PawnPromotion.handlePawnPromotion(selectedPiece, row, col, this);
                boardGui.board[row][col] = finalPiece;
                boardGui.board[boardGui.selectedRow][boardGui.selectedCol] = "";
                whiteTurn = !whiteTurn;
                printBoard();
                checkWin(selectedPiece.startsWith("w") ? "b_king" : "w_king");
            }
            boardGui.selectedRow = -1;
            boardGui.selectedCol = -1;
            repaint();
        }
}


    private void handleClick(int row, int col) {
        if ((whiteTurn && WhiteIsAi) || (!whiteTurn && BlackIsAi)) {
            System.out.println("It's AI's turn — please wait.");
            return;
        }
        if (boardGui.selectedRow == -1) {
            String piece = boardGui.board[row][col];
            if (!piece.isEmpty() && ((whiteTurn && piece.startsWith("w")) || (!whiteTurn && piece.startsWith("b")))) {
                boardGui.selectedRow = row;
                boardGui.selectedCol = col;
                repaint();
            }
        } else {
            String selectedPiece = boardGui.board[boardGui.selectedRow][boardGui.selectedCol];
            if (isValidMove(selectedPiece, boardGui.selectedRow, boardGui.selectedCol, row, col)) {
                String finalPiece = PawnPromotion.handlePawnPromotion(selectedPiece, row, col, this);
                boardGui.board[row][col] = finalPiece;
                boardGui.board[boardGui.selectedRow][boardGui.selectedCol] = "";
                whiteTurn = !whiteTurn;
                printBoard();
                checkWin(selectedPiece.startsWith("w") ? "b_king" : "w_king");
                if (!whiteTurn){
                handleAIMove();
                }
            }
            boardGui.selectedRow = -1;
            boardGui.selectedCol = -1;
            repaint();
        }
    }
    private void printBoard() {
        System.out.println("\nCurrent board:");
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = boardGui.board[r][c];
                if (piece == null || piece.isEmpty()) {
                    System.out.print(". ");
                } else {
                    String shortPiece = piece.replace("w_", "w").replace("b_", "b");
                    System.out.print(shortPiece.substring(0, 2) + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean isValidMove(String selectedpiece, int fromRow, int fromCol, int toRow, int toCol) {
        return ChessLogic.selectedPieceLegalMove(boardGui.board, selectedpiece, fromRow, fromCol, toRow, toCol);
    }
    private void handleAIMove() {
        new Thread(() -> {
            try {
                String fen = cfen.generateFEN(whiteTurn);
                System.out.println("the FEN is : " + fen);
                List<String> candidates = Collections.singletonList(hybridAI.getBestMove(fen)); // adapt name
                String chosen = null;
                if (candidates != null) {
                    for (String aiMove : candidates) {
                        aiMove = aiMove.trim();
                        if (!isValidUCIMove(aiMove)) continue;

                        int fromCol = fileToCol(aiMove.charAt(0));
                        int fromRow = rankToRow(aiMove.charAt(1));
                        int toCol   = fileToCol(aiMove.charAt(2));
                        int toRow   = rankToRow(aiMove.charAt(3));

                        // Ensure there is actually a piece at from-square
                        String piece = boardGui.board[fromRow][fromCol];
                        if (piece == null || piece.isEmpty()) continue;

                        // Use your ChessLogic to verify legal move
                        if (isValidMove(piece, fromRow, fromCol, toRow, toCol)) {
                            chosen = aiMove;
                            break;
                        } else {
                            System.out.println("Discarding illegal candidate: " + aiMove);
                        }
                    }
                }

                // Fallback: ask Stockfish for a legal move if Gemini candidates don't work
                if (chosen == null) {
                    System.out.println("No valid Gemini move found, using Stockfish fallback.");
//                    chosen = hybridAI.getStockfishMove(fen); // implement this method to call stockfish directly
                }

                if (chosen == null || !isValidUCIMove(chosen)) {
                    System.err.println("No legal move found for AI. Skipping move.");
                    whiteTurn = true; // safe fallback to let human move or handle properly
                    return;
                }

                // Apply the chosen move
                int fromCol = fileToCol(chosen.charAt(0));
                int fromRow = rankToRow(chosen.charAt(1));
                int toCol   = fileToCol(chosen.charAt(2));
                int toRow   = rankToRow(chosen.charAt(3));

                String piece = boardGui.board[fromRow][fromCol];
                boardGui.board[toRow][toCol] = piece; // capture if existed — that's fine
                boardGui.board[fromRow][fromCol] = "";

                System.out.println("AI played: " + chosen + " piece: " + piece);
                printBoard(); // debug
                whiteTurn = true;
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private boolean isValidUCIMove(String move) {
        return move != null && move.matches("^[a-h][1-8][a-h][1-8][qrbn]?$");
    }

    private int fileToCol(char file) { // 'a' -> 0
        return file - 'a';
    }
    // convert rank char to your board row index (assuming board[0] is rank 8)
    private int rankToRow(char rankChar) {
        int rank = Character.getNumericValue(rankChar); // '1'..'8' -> 1..8
        return 8 - rank; // rank 8 -> row 0, rank 1 -> row 7
    }


    // -------------------- Win / Check logic --------------------
    private void checkWin(String kingPiece) {
        boolean kingExists = containsPiece(kingPiece);
        boolean inCheck = isKingInCheck(kingPiece);
        boolean hasMoves = hasAnyLegalMove(kingPiece);

        if (!kingExists) {
            JOptionPane.showMessageDialog(this, kingPiece.startsWith("w_") ? "Black wins!" : "White wins!");
            System.exit(0);
        }

        if (inCheck && !hasMoves) {
            JOptionPane.showMessageDialog(this, kingPiece.startsWith("w_") ? "Black wins!" : "White wins!");
            System.exit(0);
        } else if (!inCheck && !hasMoves) {
            JOptionPane.showMessageDialog(this, "Draw by stalemate!");
            System.exit(0);
        }
    }

    private boolean hasAnyLegalMove(String kingPiece) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = boardGui.board[r][c];
                if (piece != null && !piece.isEmpty() && pieceBelongsTo(piece, kingPiece)) {
                    for (int newR = 0; newR < 8; newR++) {
                        for (int newC = 0; newC < 8; newC++) {
                            if (ChessLogic.selectedPieceLegalMove(boardGui.board, piece, r, c, newR, newC)) {
                                String captured = boardGui.board[newR][newC];
                                boardGui.board[newR][newC] = piece;
                                boardGui.board[r][c] = "";
                                boolean stillInCheck = isKingInCheck(kingPiece);
                                boardGui.board[r][c] = piece;
                                boardGui.board[newR][newC] = captured;
                                if (!stillInCheck) return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean pieceBelongsTo(String piece, String kingPiece) {
        return (kingPiece.startsWith("w_") && piece.startsWith("w_")) ||
                (kingPiece.startsWith("b_") && piece.startsWith("b_"));
    }

    private boolean containsPiece(String pieceName) {
        for (String[] row : boardGui.board)
            for (String p : row)
                if (pieceName.equals(p)) return true;
        return false;
    }

    private boolean isKingInCheck(String kingName) {
        int kingRow = -1, kingCol = -1;
        outerLoop: for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (kingName.equals(boardGui.board[r][c])) {
                    kingRow = r;
                    kingCol = c;
                    break outerLoop;
                }
            }
        }
        if (kingRow == -1) return false;
        String kingColor = kingName.startsWith("w_") ? "w_" : "b_";
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (boardGui.board[r][c] != null && !boardGui.board[r][c].isEmpty() && !boardGui.board[r][c].startsWith(kingColor))
                    if (ChessLogic.selectedPieceLegalMove(boardGui.board, boardGui.board[r][c], r, c, kingRow, kingCol))
                        return true;
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java Chess - Player vs AI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // Create the player selection panel
            JPanel selectionPanel = new JPanel();
            selectionPanel.setLayout(new GridLayout(3, 2, 10, 10));
            selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            String[] options = {"Manual", "AI"};
            JComboBox<String> whiteDropdown = new JComboBox<>(options);
            JComboBox<String> blackDropdown = new JComboBox<>(options);
            JButton startButton = new JButton("Start Game");

            selectionPanel.add(new JLabel("White Player:"));
            selectionPanel.add(whiteDropdown);
            selectionPanel.add(new JLabel("Black Player:"));
            selectionPanel.add(blackDropdown);
            selectionPanel.add(new JLabel());
            selectionPanel.add(startButton);

            frame.add(selectionPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // When Start Game is clicked
            startButton.addActionListener(e -> {
                String whiteSelection = (String) whiteDropdown.getSelectedItem();
                String blackSelection = (String) blackDropdown.getSelectedItem();

                if (whiteSelection.equals("AI") && blackSelection.equals("AI")) {
                    System.out.println("Both sides are AI");
                    frame.getContentPane().removeAll();
                    ChessGame chessPanel = new ChessGame(true ,true);
                    frame.add(chessPanel);
                    frame.setSize(480, 518);
                    frame.revalidate();
                    frame.repaint();
                }
                else if (whiteSelection.equals("Manual") && blackSelection.equals("AI")) {
                    System.out.println("White is Manual, Black is AI");
                    frame.getContentPane().removeAll();
                    ChessGame chessPanel = new ChessGame(false ,true);
                    frame.add(chessPanel);
                    frame.setSize(480, 518);
                    frame.revalidate();
                    frame.repaint();
                }
                else if (whiteSelection.equals("AI") && blackSelection.equals("Manual")) {
                    System.out.println("White is AI, Black is Manual");
                    frame.getContentPane().removeAll();
                    ChessGame chessPanel = new ChessGame(true ,false);
                    frame.add(chessPanel);
                    frame.setSize(480, 518);
                    frame.revalidate();
                    frame.repaint();
                }
                else if (whiteSelection.equals("Manual") && blackSelection.equals("Manual")) {
                    System.out.println("Both sides are Manual");
                    frame.getContentPane().removeAll();
                    ChessGame chessPanel = new ChessGame(false ,false);
                    frame.add(chessPanel);
                    frame.setSize(480, 518);
                    frame.revalidate();
                    frame.repaint();
                }
                else {
                    System.out.println("Please choose valid options.");
                }

                System.out.println("the value of whiteAi is : " + whiteSelection);
                System.out.println("the value of blackAi is : " + blackSelection);


            });
        });
    }

}
