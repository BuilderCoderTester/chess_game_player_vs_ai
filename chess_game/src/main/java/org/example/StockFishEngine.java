package org.example;

import java.io.*;

public class StockFishEngine {
    private Process engine;
    private BufferedReader reader;
    private BufferedWriter writer;

    public StockFishEngine(String pathtoEngine) throws IOException {
        engine = Runtime.getRuntime().exec(pathtoEngine);
        reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(engine.getOutputStream()));

    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public String readResponse(long ms) throws IOException {
        StringBuilder sb = new StringBuilder();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < ms) {
            if (reader.ready()) {
                String line = reader.readLine();
                sb.append(line).append("\n");
            }

        }
        return sb.toString();
    }

    public int evaluateMove(String fen, String move) throws IOException {
        sendCommand("position fen " + fen + " moves " + move);
        sendCommand("go depth 12"); // AI strength
        String response = readResponse(2000);

        for (String line : response.split("\n")) {
            if (line.contains("cp ")) {
                String val = line.substring(line.indexOf("cp ") + 3).split(" ")[0];
                return Integer.parseInt(val); // centipawn score
            }
        }
        return -99999;
    }
}
