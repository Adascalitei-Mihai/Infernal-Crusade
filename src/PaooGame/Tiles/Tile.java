package PaooGame.Tiles;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Tile {
    public static final int TILE_WIDTH = 16; // Adaptat la cerința ta
    public static final int TILE_HEIGHT = 16;
    private static final int NO_TILES = 256;

    public static Tile[] tiles = new Tile[NO_TILES];

    protected BufferedImage img;
    protected final int id;

    public Tile(BufferedImage img, int id) {
        this.img = img;
        this.id = id;
        tiles[id] = this;
    }

    public void Update() {
    }

    public void Draw(Graphics g, int x, int y) {
        g.drawImage(img, x, y, TILE_WIDTH, TILE_HEIGHT, null);
    }

    public boolean isSolid() { return false; }

    public int getId() { return id; }
}