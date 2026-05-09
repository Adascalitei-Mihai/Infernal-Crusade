package PaooGame.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    public static final int TILE_SIZE = 40;
    public static final int NO_TILES = 6000;
    public static Tile[] tiles = new Tile[NO_TILES];

    private final boolean solid;
    protected BufferedImage img;
    protected final int id;

    public Tile(BufferedImage img, int id, boolean solid) {
        this.img = img;
        this.id = id;
        if (id >= 0 && id < NO_TILES) {
            tiles[id] = this;
        }

        this.solid = solid;
    }

    public void Update() {}

    public void Draw(Graphics g, int x, int y) {
        g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
    }

    public boolean isSolid() { return solid; }

    public int getId() { return id; }
}