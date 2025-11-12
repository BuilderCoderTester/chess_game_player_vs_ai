package org.example;

import org.example.GeminiMoveGenerator;

import java.util.*;

public class HybridChessAI {

    private GeminiMoveGenerator gemini;
    private StockFishEngine stockfish;

    public HybridChessAI(GeminiMoveGenerator gemini, StockFishEngine stockfish) {
        this.gemini = gemini;
        this.stockfish = stockfish;
    }

    public String getBestMove(String fen) throws Exception {
        // 1. Ask Gemini for candidate moves
        List<String> candidates = gemini.generateCandidates(fen);

        int bestScore = Integer.MIN_VALUE;
        String bestMove = null;

        // 2. Evaluate each candidate using Stockfish
        for (String move : candidates) {
            int score = stockfish.evaluateMove(fen, move);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }
}
