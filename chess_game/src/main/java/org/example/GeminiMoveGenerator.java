package org.example;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class GeminiMoveGenerator {

    private static final Map<String, Object> ARRAY_OF_STRINGS_SCHEMA = Map.of(
            "type", "array",
            "items", Map.of("type", "string")
    );

    private final Client client;
    private final String modelName;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<ArrayList<String>>() {}.getType();

    public GeminiMoveGenerator() {
        Dotenv dotenv = Dotenv.configure()
                .directory("/media/anurag/Windows/Projects/$ML_PATH/chessGameinJava/chess_game/.env")
                .load();
        String apiKey = dotenv.get("GOOGLE_API_KEY");
        System.out.println(apiKey);
        this.client = Client.builder().apiKey(apiKey).build();
        System.out.println("has come here ");
        this.modelName = "gemini-2.5-flash";
        System.out.println("GeminiMoveGenerator initialized.");
    }

    public List<String> generateCandidates(String fen) throws IOException {

        String[] parts = fen.split(" ");
        String turn = parts.length > 1 ? parts[1] : "w";
        String player = turn.equals("w") ? "White" : "Black";
        System.out.println("the turn is for "+player);
                String prompt = """
        You are a chess engine. Given this FEN:
        %s
        
        It's %s to move.
        Suggest exactly FIVE *legal* candidate moves for %s in UCI format (like e2e4, g8f6, etc).
        Return ONLY a JSON array of strings.
        """.formatted(fen, player, player);
        Content content = Content.fromParts(
                Part.fromText(prompt)
        );

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .temperature(turn.equals("w") ? 0.2F : 0.4F)
                        .responseMimeType("application/json")
                        .responseJsonSchema(ARRAY_OF_STRINGS_SCHEMA)
                        .candidateCount(1)
                        .build();

        System.out.println("Calling Gemini API...");

        GenerateContentResponse response =
                client.models.generateContent(
                        modelName,             // String
                        List.of(content),      // List<Content>
                        config                 // GenerateContentConfig
                );
        String json = Objects.requireNonNull(response.text()).trim();

        try {
            List<String> moves = gson.fromJson(json, listType);
            if (moves == null) return Collections.emptyList();
            moves.removeIf(m -> !m.matches("^[a-h][1-8][a-h][1-8][qrbn]?$"));
            return moves;
        } catch (Exception e) {
            System.err.println("JSON parse error: " + json);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void close() {
        client.close();
    }
}
