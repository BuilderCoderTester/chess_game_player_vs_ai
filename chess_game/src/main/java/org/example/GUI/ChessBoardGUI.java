package org.example.GUI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ChessBoardGUI {
    public static final int WIDTH = 480, HEIGHT = 518;
    public static final int ROWS = 8, COLS = 8;
    public static final int SQUARE_SIZE = WIDTH / COLS;
    public java.util.Map<String, BufferedImage> pieces;
    public String[][] board;
    public int selectedRow = -1, selectedCol = -1;
    public static final Color WHITE_COLOR = new Color(198, 255, 171);
    public static final Color BROWN_COLOR = new Color(0, 128, 0);
    public static final Color SELECT_COLOR = new Color(255, 255, 0, 128); // Yellow highlight

    public ChessBoardGUI(){
        System.out.println("come here ");
        loadImages();
        initBoard();
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

    public void drawBoard(Graphics g) {
        System.out.println("reach point 3");
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? WHITE_COLOR : BROWN_COLOR);
                g.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    public void drawPieces(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String piece = board[row][col];
                if (piece != null && !piece.isEmpty()) {
                    g.drawImage(pieces.get(piece), col * SQUARE_SIZE, row * SQUARE_SIZE, null);
                }
            }
        }
    }

    public void drawSelection(Graphics g) {
        if (selectedRow != -1 && selectedCol != -1) {
            g.setColor(SELECT_COLOR);
            g.fillRect(selectedCol * SQUARE_SIZE, selectedRow * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }
}
