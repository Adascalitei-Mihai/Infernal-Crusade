package PaooGame.World.Tiles;
import java.awt.image.BufferedImage;

public class TransparentTile extends Tile {
    public TransparentTile(BufferedImage image, int id) {
        super(image, id);
    }

    @Override
    public boolean isSolid() { return false; }

    @Override
    public boolean getIce() { return false; }
}