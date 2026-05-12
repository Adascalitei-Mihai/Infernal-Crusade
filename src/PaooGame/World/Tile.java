package PaooGame.World;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Clasa care reprezinta o dala
 */
public class Tile {
    public static final int TILE_SIZE = 40;
    public static final int NO_TILES = 6000;
    public static final Tile[] TILES = new Tile[NO_TILES];

    private final boolean solid;
    private final BufferedImage img;
    private final int id;

    /**
     * Constructorul care initializeaza campurile si vectorul de dale {@link #TILES}
     * @param img Imaginea dalei
     * @param id ID-ul specific
     * @param solid Daca este solida
     */
    public Tile(BufferedImage img, int id, boolean solid) {
        this.img = img;
        this.id = id;
        if (id >= 0 && id < NO_TILES) {
            TILES[id] = this;
        }

        this.solid = solid;
    }

    /**
     * Metoda de desenare, afiseaza dala la dimensiunea {@link #TILE_SIZE}
     * @param g Grafica de desenare
     * @param x Pozitia pe Ox
     * @param y Pozitia pe Oy
     */
    public void Draw(Graphics g, int x, int y) {
        g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
    }

    public boolean isSolid() { return solid; }
    public int getId() { return id; }
}