package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the ChessGame application.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void testAppInitialization() {
        // A simple placeholder test to ensure your main class can be created.
        // Replace this later with a test specific to your chess engine!
        ChessGame game = new ChessGame();
        assertTrue(game != null, "ChessGame object should not be null on creation.");
    }
}