package PaooGame.Graphics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMXParser {

    /**
     * Parsează fișierul TMX și returnează un Map care asociază numele fiecărui layer dorit cu matricea sa 2D.
     *
     * @param path Calea către fișierul .tmx în resurse.
     * @param targetLayers Lista cu numele layerelor pe care dorim să le extragem.
     * @return Map<String, int[][]> conținând doar matricele layerelor specificate.
     */
    public static Map<String, int[][]> parseTMXLayers(String path, List<String> targetLayers) {
        Map<String, int[][]> layersData = new HashMap<>();

        try (InputStream is = TMXParser.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Fișierul TMX nu a putut fi găsit: " + path);
            }

            // Inițializăm parserul XML (DOM)
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            // Extragem lățimea și înălțimea globală a hărții (în număr de tile-uri)
            Element mapElement = doc.getDocumentElement();
            int mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            int mapHeight = Integer.parseInt(mapElement.getAttribute("height"));

            // Obținem toate tag-urile <layer> (chiar dacă sunt în interiorul unui <group>)
            NodeList layerNodes = doc.getElementsByTagName("layer");

            for (int i = 0; i < layerNodes.getLength(); i++) {
                Element layerElement = (Element) layerNodes.item(i);
                String layerName = layerElement.getAttribute("name");

                // Verificăm dacă numele layer-ului se află în lista de layere dorite
                if (targetLayers.contains(layerName)) {
                    // Căutăm tag-ul <data> din interiorul acestui layer
                    NodeList dataNodes = layerElement.getElementsByTagName("data");
                    if (dataNodes.getLength() > 0) {
                        Element dataElement = (Element) dataNodes.item(0);
                        String encoding = dataElement.getAttribute("encoding");

                        if ("csv".equals(encoding)) {
                            String csvContent = dataElement.getTextContent();
                            int[][] matrix = decodeCSV(csvContent, mapWidth, mapHeight);
                            layersData.put(layerName, matrix);
                        } else {
                            System.err.println("Avertisment: Layerul '" + layerName + "' nu folosește encoding CSV!");
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Eroare la parsarea fișierului TMX: " + path);
            e.printStackTrace();
        }

        return layersData;
    }

    /**
     * Decodează string-ul CSV într-o matrice 2D de tip [x][y].
     */
    private static int[][] decodeCSV(String csvData, int width, int height) {
        // Curățăm string-ul de spații și linii noi
        String cleaned = csvData.trim().replace("\n", "").replace("\r", "");
        String[] values = cleaned.split(",");
        int[][] matrix = new int[width][height];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (index < values.length) {
                    try {
                        matrix[x][y] = Integer.parseInt(values[index].trim());
                    } catch (NumberFormatException e) {
                        matrix[x][y] = 0; // Fallback pentru tile gol dacă e eroare
                    }
                } else {
                    matrix[x][y] = 0;
                }
                index++;
            }
        }
        return matrix;
    }
}