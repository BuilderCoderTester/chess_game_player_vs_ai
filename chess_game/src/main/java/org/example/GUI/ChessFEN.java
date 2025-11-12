package org.example.GUI;

public class ChessFEN {
    public ChessBoardGUI boardGui ;
    public ChessFEN(ChessBoardGUI boardGui){
    this.boardGui = boardGui;
    }

public String generateFEN(boolean whiteTurn) {
    StringBuilder fen = new StringBuilder();
    for (int r = 0; r < 8; r++) {
        int emptyCount = 0;
        for (int c = 0; c < 8; c++) {
            String piece = boardGui.board[r][c];
//            System.out.println("after every move the board : ");
//            System.out.println(boardGui.board);
            if (piece == null || piece.isEmpty()) {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    fen.append(emptyCount);
                    emptyCount = 0;
                }
                fen.append(getFENChar(piece));
            }
        }
        if (emptyCount > 0) fen.append(emptyCount);
        if (r < 7) fen.append("/");
    }
    fen.append(whiteTurn ? " w " : " b ");
    fen.append("KQkq - 0 1"); // simplified
    return fen.toString();
}

public char getFENChar(String piece) {
    switch (piece) {
        case "w_pawn": return 'P';
        case "w_rook": return 'R';
        case "w_knight": return 'N';
        case "w_bishop": return 'B';
        case "w_queen": return 'Q';
        case "w_king": return 'K';
        case "b_pawn": return 'p';
        case "b_rook": return 'r';
        case "b_knight": return 'n';
        case "b_bishop": return 'b';
        case "b_queen": return 'q';
        case "b_king": return 'k';
    }
    return '?';
}
}
