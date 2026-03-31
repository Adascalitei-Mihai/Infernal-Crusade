package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/**
 * Clasa retine o referinta catre o imagine formata din dale (sprite sheet).
 */
public class SpriteSheet {
    private final BufferedImage spriteSheet;
    private final int tileWidth;
    private final int tileHeight;

    /**
     * Constructor.
     * @param spriteSheet Un obiect BufferedImage valid.
     * @param tileWidth Latimea dalei.
     * @param tileHeight Inaltimea dalei.
     */
    public SpriteSheet(BufferedImage spriteSheet, int tileWidth, int tileHeight) {
        this.spriteSheet = spriteSheet;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Functie care reurneaza o dala. <br>
     * Subimaginea este localizata avand ca referinta punctul din stanga sus.
     * @param x numarul dalei din sprite sheet pe axa x.
     * @param y numarul dalei din sprite sheet pe axa y.
     * @return Dala selectata drep un obiect BufferedImage.
     */
    public BufferedImage crop(int x, int y) {
        return spriteSheet.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
    }
}
