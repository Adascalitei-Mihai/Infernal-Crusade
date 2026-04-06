package PaooGame.World.Tiles;
import java.awt.image.BufferedImage;

public class SolidTile extends Tile {
    private boolean ice;
    public SolidTile(BufferedImage image, int id) {
        super(image, id);
        ice = false;
    }

    @Override
    public boolean isSolid() { return true; }

    @Override
    public boolean getIce() { return ice; }

    public void setIce(boolean ice) { this.ice = ice; }
}