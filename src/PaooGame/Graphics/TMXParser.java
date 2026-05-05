package PaooGame.Graphics;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class TMXParser {
    public static String loadFileAsString(String path) {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream is = TMXParser.class.getResourceAsStream(path);

            if (is == null) {
                throw new IllegalArgumentException("Fișierul TMX nu a putut fi găsit: " + path);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
            br.close();
        }
        catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static int[][] decodeCSV(String csvData, int width, int height) {
        String cleaned = csvData.trim().replace("\n", "").replace("\r", "");
        String[] values = cleaned.split(",");
        int[][] matrix = new int[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                matrix[x][y] = Integer.parseInt(values[x + y * width]);
            }
        }
        return matrix;
    }
}