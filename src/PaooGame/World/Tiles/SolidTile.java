package PaooGame.World.Tiles;
import java.awt.image.BufferedImage;

public class SolidTile extends Tile {
    private static final int ICE_TIME = 600;

    private int iceTimer = 0;
    public SolidTile(BufferedImage image, int id) {
        super(image, id);
    }

    @Override
    public void Update() {
        iceTimer -= 1;
    }

    @Override
    public boolean isSolid() { return true; }

    @Override
    public boolean isIce() { return iceTimer > 0; }

    public void setIce() { this.iceTimer = ICE_TIME; }
}