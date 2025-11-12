import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ChessGame extends JPanel {

    private static final int WIDTH = 480, HEIGHT = 518;
    private static final int ROWS = 8, COLS = 8;
    private static final int SQUARE_SIZE = WIDTH / COLS;
    private static final Color WHITE_COLOR = new Color(198, 255, 171);
    private static final Color BROWN_COLOR = new Color(0, 128, 0);
    private static final Color SELECT_COLOR = new Color(255, 255, 0, 128); // Yellow highlight

    private String[][] board;
    private java.util.Map<String, BufferedImage> pieces;
    private boolean whiteTurn = true;

    // Click selection
    private int selectedRow = -1, selectedCol = -1;

    public ChessGame() {
        loadImages();
        initBoard();

        // Add mouse listener for manual moves
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = e.getY() / SQUARE_SIZE;
                int col = e.getX() / SQUARE_SIZE;
                handleClick(row, col);
            }
        });
    }

    private void loadImages() {
        pieces = new java.util.HashMap<>();
        String[] colors = { "w", "b" };
        String[] types = { "pawn", "rook", "knight", "bishop", "queen", "king" };

        for (String color : colors) {
            for (String type : types) {
                String name = color + "_" + type;
                try {
                    BufferedImage img = ImageIO.read(new File(
                            "/media/anurag/Windows/Projects/$ML_PATH/chessGameinJava/public/" + name
                                    + ".png"));
                    Image scaled = img.getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
                    BufferedImage buffered = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = buffered.createGraphics();
                    g2.drawImage(scaled, 0, 0, null);
                    g2.dispose();
                    pieces.put(name, buffered);
                } catch (IOException e) {
                    System.out.println("Image not found: " + name + ".png");
                }
            }
        }
    }

    private void initBoard() {
        board = new String[][] {
                { "b_rook", "b_knight", "b_bishop", "b_queen", "b_king", "b_bishop", "b_knight", "b_rook" },
                { "b_pawn", "b_pawn", "b_pawn", "b_pawn", "b_pawn", "b_pawn", "b_pawn", "b_pawn" },
                { "", "", "", "", "", "", "", "" },
                { "", "", "", "", "", "", "", "" },
                { "", "", "", "", "", "", "", "" },
                { "", "", "", "", "", "", "", "" },
                { "w_pawn", "w_pawn", "w_pawn", "w_pawn", "w_pawn", "w_pawn", "w_pawn", "w_pawn" },
                { "w_rook", "w_knight", "w_bishop", "w_queen", "w_king", "w_bishop", "w_knight", "w_rook" }
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
        drawSelection(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? WHITE_COLOR : BROWN_COLOR);
                g.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawPieces(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String piece = board[row][col];
                if (piece != null && !piece.isEmpty()) {
                    g.drawImage(pieces.get(piece), col * SQUARE_SIZE, row * SQUARE_SIZE, null);
                }
            }
        }
    }

    private void drawSelection(Graphics g) {
        if (selectedRow != -1 && selectedCol != -1) {
            g.setColor(SELECT_COLOR);
            g.fillRect(selectedCol * SQUARE_SIZE, selectedRow * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

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

    private boolean isValidMove(String selectedpiece, int fromRow, int fromCol, int toRow, int toCol) {
        return (ChessLogic.selectedPieceLegalMove(board, selectedpiece, fromRow, fromCol, toRow, toCol));
    }

    private void checkWin(String kingPiece) {
        boolean kingExists = containsPiece(kingPiece);
        boolean inCheck = isKingInCheck(kingPiece);
        boolean hasMoves = hasAnyLegalMove(kingPiece);

        if (!kingExists) {
            // King captured → game over
            if (kingPiece.equals("w_king")) {
                JOptionPane.showMessageDialog(this, "Black wins!");
            } else {
                JOptionPane.showMessageDialog(this, "White wins!");
            }
            System.exit(0);
        }

        if (inCheck && !hasMoves) {
            // Checkmate
            if (kingPiece.equals("w_king")) {
                JOptionPane.showMessageDialog(this, "Black wins!");
            } else {
                JOptionPane.showMessageDialog(this, "White wins!");
            }
            System.exit(0);
        } else if (!inCheck && !hasMoves) {
            // Stalemate
            JOptionPane.showMessageDialog(this, "Draw by stalemate!");
            System.exit(0);
        }
    }

    /**
     * Check if the player with this king has any legal move that removes check.
     */
    private boolean hasAnyLegalMove(String kingPiece) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece != null && !piece.isEmpty() && pieceBelongsTo(piece, kingPiece)) {
                    // Get all legal moves for this piece
                    for (int newR = 0; newR < 8; newR++) {
                        for (int newC = 0; newC < 8; newC++) {
                            if (ChessLogic.selectedPieceLegalMove(board, piece, r, c, newR, newC)) {
                                // Simulate move
                                String captured = board[newR][newC];
                                board[newR][newC] = piece;
                                board[r][c] = "";

                                boolean stillInCheck = isKingInCheck(kingPiece);

                                // Undo move
                                board[r][c] = piece;
                                board[newR][newC] = captured;

                                if (!stillInCheck) {
                                    return true; // Found at least one safe move
                                }
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
        for (String[] row : board) {
            for (String p : row) {
                if (pieceName.equals(p))
                    return true;
            }
        }
        return false;
    }

    private boolean isKingInCheck(String kingName) {
        int kingRow = -1, kingCol = -1;

        // Find king's position
        outerLoop: for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String square = board[r][c];
                if (square != null && !square.isEmpty() && square.equals(kingName)) {
                    System.out.println("king check");
                    kingRow = r;
                    kingCol = c;
                    break outerLoop; // stop searching immediately
                }
            }
        }

        if (kingRow == -1)
            return false; // king not found

        // Check if any opponent piece can attack the king
        String kingColor = kingName.startsWith("w_") ? "w_" : "b_";
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece != null && !piece.isEmpty() && !piece.startsWith(kingColor)) {
                    if (ChessLogic.selectedPieceLegalMove(board, piece, r, c, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Java Chess - Player vs Player");
        ChessGame chessPanel = new ChessGame();
        frame.add(chessPanel);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
