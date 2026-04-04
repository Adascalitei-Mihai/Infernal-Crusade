package PaooGame.Tiles;

import java.awt.image.BufferedImage;

public class SurfaceTile extends SolidTile {
    public SurfaceTile(BufferedImage image, int id) { super(image, id); }

    @Override
    public boolean canHostSpells() { return true; }
}