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

    // --- CLASĂ INTERNĂ PENTRU A STOCA DATELE TILESET-ULUI ---
    public static class TilesetData {
        public int firstGid;
        public String imagePath;
        public int tileWidth;
        public int tileHeight;
        public int columns;
        public int tileCount;
    }

    /**
     * Parsează fișierul TMX și fișierele TSX asociate pentru a extrage automat
     * datele necesare inițializării SpriteSheet-urilor (firstGid, coloane, count etc).
     */
    public static java.util.List<TilesetData> parseTilesets(String tmxPath) {
        java.util.List<TilesetData> tilesets = new java.util.ArrayList<>();

        try (InputStream is = TMXParser.class.getResourceAsStream(tmxPath)) {
            if (is == null) throw new IllegalArgumentException("TMX invalid: " + tmxPath);

            // Găsim folderul în care se află TMX-ul (ex: "/textures/Maps/L2/")
            String dirPath = tmxPath.substring(0, tmxPath.lastIndexOf('/') + 1);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            // Extragem toate tagurile <tileset>
            NodeList tsNodes = doc.getElementsByTagName("tileset");

            for (int i = 0; i < tsNodes.getLength(); i++) {
                Element tsElem = (Element) tsNodes.item(i);
                int firstGid = Integer.parseInt(tsElem.getAttribute("firstgid"));
                String sourceTsx = tsElem.getAttribute("source");

                // Calea completă către fișierul .tsx
                String tsxPath = dirPath + sourceTsx;

                try (InputStream tsxIs = TMXParser.class.getResourceAsStream(tsxPath)) {
                    if (tsxIs != null) {
                        Document tsxDoc = builder.parse(tsxIs);
                        tsxDoc.getDocumentElement().normalize();
                        Element root = tsxDoc.getDocumentElement();

                        TilesetData data = new TilesetData();
                        data.firstGid = firstGid;
                        data.tileWidth = Integer.parseInt(root.getAttribute("tilewidth"));
                        data.tileHeight = Integer.parseInt(root.getAttribute("tileheight"));
                        data.tileCount = Integer.parseInt(root.getAttribute("tilecount"));
                        data.columns = Integer.parseInt(root.getAttribute("columns"));

                        // Extragem numele imaginii din .tsx
                        NodeList imageNodes = root.getElementsByTagName("image");
                        if (imageNodes.getLength() > 0) {
                            Element imageElem = (Element) imageNodes.item(0);
                            String imageSource = imageElem.getAttribute("source");

                            // Curățăm calea (în caz că Tiled a pus '../' sau directoare relative)
                            // și păstrăm doar numele fișierului png pentru a-l lipi de dirPath.
                            String fileName = imageSource.substring(imageSource.lastIndexOf('/') + 1);
                            data.imagePath = dirPath + fileName;
                        }

                        tilesets.add(data);
                    }
                } catch (Exception ex) {
                    System.err.println("Nu s-a putut parsa fișierul TSX: " + tsxPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tilesets;
    }
}