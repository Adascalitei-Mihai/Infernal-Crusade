package PaooGame.Graphics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clasa care interpreteaza fisierele TMX (hartile create prin TiledMapEditor)
 * @see javax.xml.parsers
 * @see org.w3c.dom
 */
public class TMXParser {
    /**
     * Clasa interna care retine informatiile legate de un tileset
     */
    public static class TilesetData {
        public int firstGid;
        public String imagePath;
        public int tileWidth;
        public int tileHeight;
        public int columns;
        public int tileCount;
    }

    /**
     * Metoda  care citeste un fisier si construieste un Document XML
     * @param path Calea fisierului
     * @return Documentul DOM
     * @throws Exception Daca fisierul nu este gasit sau nu este un XML valid
     */
    private static Document buildXMLDocument(String path) throws Exception {
        try (InputStream is = TMXParser.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("[EROARE]: Fisierul nu a putut fi gasit: " + path);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            return doc;
        }
    }

    /**
     * Metoda care interpreteaza straturile fisierului <br>
     * 1. Initializeaza parserul XML folosind {@link #buildXMLDocument(String)} <br>
     * 2. Afla dimensiunile hartii <br>
     * 3. Extrage din straturile dorite matricea CSV a ID-urilor dalelor <br>
     * 4. Le interpreteaza (folosind {@link #decodeCSV(String, int, int)}) si le retine
     * @param path Calea fisierului
     * @param targetLayers Straturile dorite
     * @return Matrice de ID-uri ale straturilor cerute
     */
    public static Map<String, int[][]> parseTMXLayers(String path, List<String> targetLayers) {
        Map<String, int[][]> layersData = new HashMap<>();

        try {
            Document doc = buildXMLDocument(path); // 1

            Element mapElement = doc.getDocumentElement();
            int mapWidth = Integer.parseInt(mapElement.getAttribute("logicWidth"));
            int mapHeight = Integer.parseInt(mapElement.getAttribute("logicHeight")); // 2

            NodeList layerNodes = doc.getElementsByTagName("layer");

            for (int i = 0; i < layerNodes.getLength(); i++) {
                Element layerElement = (Element) layerNodes.item(i);
                String layerName = layerElement.getAttribute("name");

                if (targetLayers.contains(layerName)) {
                    NodeList dataNodes = layerElement.getElementsByTagName("data");
                    if (dataNodes.getLength() > 0) {
                        Element dataElement = (Element) dataNodes.item(0);
                        String encoding = dataElement.getAttribute("encoding");

                        if ("csv".equals(encoding)) {
                            String csvContent = dataElement.getTextContent(); // 3
                            int[][] matrix = decodeCSV(csvContent, mapWidth, mapHeight); // 4
                            layersData.put(layerName, matrix);
                        } else {
                            System.err.println("[EROARE]: Layerul '" + layerName + "' nu foloseste encoding CSV!");
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("[EROARE]: Parsare TMX Layers pentru: " + path);
        }

        return layersData;
    }

    /**
     * Metoda care interpreteaza string-ul CSV intr-o matrice 2D
     * @param csvData Datele extrase
     * @param width Latimea hartii
     * @param height Inaltimea hartii
     * @return Matricea de ID-uri
     */
    private static int[][] decodeCSV(String csvData, int width, int height) {
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
                        matrix[x][y] = 0;
                    }
                } else {
                    matrix[x][y] = 0;
                }
                index++;
            }
        }
        return matrix;
    }

    /**
     * Metoda care interpreteaza seturile de dale ale fisierului <br>
     * 1. Initializeaza parserul XML folosind {@link #buildXMLDocument(String)} <br>
     * 2. Gaseste calea directorului sursa pentru a localiza fisierele TSX <br>
     * 3. Citeste si stocheaza datele fiecarui tileset din fisierul TSX aferent
     * @param path Calea fisierului TMX
     * @return Lista cu datele despre seturile de dale (TilesetData)
     */
    public static List<TilesetData> parseTMXTilesets(String path) {
        List<TilesetData> tilesets = new ArrayList<>();

        try {
            Document doc = buildXMLDocument(path); // 1

            String dirPath = path.substring(0, path.lastIndexOf('/') + 1); // 2
            NodeList tsNodes = doc.getElementsByTagName("tileset");

            for (int i = 0; i < tsNodes.getLength(); i++) {
                Element tsElem = (Element) tsNodes.item(i);
                int firstGid = Integer.parseInt(tsElem.getAttribute("firstgid"));
                String sourceTsx = tsElem.getAttribute("source");

                String tsxPath = dirPath + sourceTsx;

                try {
                    Document tsxDoc = buildXMLDocument(tsxPath);
                    Element root = tsxDoc.getDocumentElement();

                    TilesetData data = new TilesetData();
                    data.firstGid = firstGid;
                    data.tileWidth = Integer.parseInt(root.getAttribute("tilewidth"));
                    data.tileHeight = Integer.parseInt(root.getAttribute("tileheight"));
                    data.tileCount = Integer.parseInt(root.getAttribute("tilecount"));
                    data.columns = Integer.parseInt(root.getAttribute("columns"));

                    NodeList imageNodes = root.getElementsByTagName("image");
                    if (imageNodes.getLength() > 0) {
                        Element imageElem = (Element) imageNodes.item(0);
                        String imageSource = imageElem.getAttribute("source");

                        String fileName = imageSource.substring(imageSource.lastIndexOf('/') + 1);
                        data.imagePath = dirPath + fileName;
                    }
                    tilesets.add(data); // 3

                } catch (Exception e) {
                    System.err.println("[EROARE]: Parsare TSX pentru: " + tsxPath);
                }
            }
        } catch (Exception e) {
            System.err.println("[EROARE]: Parsare TMX Tilesets pentru: " + path);
            e.printStackTrace();
        }

        return tilesets;
    }
}