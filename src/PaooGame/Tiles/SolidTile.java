package PaooGame.Tiles;
import java.awt.image.BufferedImage;

public abstract class SolidTile extends Tile {
    public SolidTile(BufferedImage image, int id) { super(image, id); }

    @Override
    public boolean isSolid() { return true; }

    public abstract boolean canHostSpells();
}