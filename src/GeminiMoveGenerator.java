import com.google.ai.generativelanguage.v1.*;
import com.google.ai.generativelanguage.v1.ModelName;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import java.util.List;

public class GeminiMoveGenerator {

    private final GenerativeServiceClient client;

    public GeminiMoveGenerator(GoogleCredentials credentials) throws Exception {
        client = GenerativeServiceClient.create(
                GenerativeServiceSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build());
    }

    public List<String> generateCandidates(String fen) {
        String prompt = """
                You are a chess engine. Given this FEN:
                %s

                Suggest exactly THREE legal candidate moves in UCI format.
                Output only a JSON array like:
                ["e2e4", "g1f3", "d2d4"]
                """.formatted(fen);

        GenerateContentResponse response = client.generateContent(
                GenerateContentRequest.newBuilder()
                        .setModel(ModelName.of("gemini-1.5-flash").toString())
                        .addContents(Content.newBuilder().addParts(Part.newBuilder().setText(prompt)))
                        .build());

        String text = response.getCandidates(0).getContent().getParts(0).getText();
        return List.of(text.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(","));
    }
}
