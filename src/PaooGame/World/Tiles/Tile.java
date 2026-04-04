package PaooGame.World.Tiles;

import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Tile {
    public static final int TILE_SIZE = 16;
    public static final int NO_TILES = 5000;

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
        g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
    }

    public abstract boolean isSolid();

    public int getId() { return id; }
}