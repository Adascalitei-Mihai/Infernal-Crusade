package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/**
 * Clasa retine o referinta catre o imagine formata din dale (sprite sheet).
 */
public class SpriteSheet {
    private final BufferedImage spriteSheet;
    private final int tileWidth;
    private final int tileHeight;

    public SpriteSheet(BufferedImage spriteSheet, int tileWidth, int tileHeight) {
        this.spriteSheet = spriteSheet;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public BufferedImage[] makeSelection(int row, int col, int count) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            frames[i] = crop(i + col, row);
        }
        return frames;
    }

    /**
     * Functie care reurneaza o dala. <br>
     * Subimaginea este localizata avand ca referinta punctul din stanga sus.
     * @param x numarul dalei din sprite sheet pe axa x.
     * @param y numarul dalei din sprite sheet pe axa y.
     * @return Dala selectata drep un obiect BufferedImage.
     */
    public BufferedImage crop(int x, int y) {
        try {
            int startX = x * tileWidth;
            int startY = y * tileHeight;

            if (startX + tileWidth > spriteSheet.getWidth()) {
                System.err.println("EROARE: Taiere pe X in afara limitelor la indicele " + x);
                return spriteSheet.getSubimage(0, 0, tileWidth, tileHeight);
            }
            if (startY + tileHeight > spriteSheet.getHeight()) {
                System.err.println("EROARE: Taiere pe Y in afara limitelor la inaltimea " + tileHeight);
                return spriteSheet.getSubimage(0, 0, tileWidth, tileHeight);
            }

            return spriteSheet.getSubimage(startX, startY, tileWidth, tileHeight);
        }
        catch (Exception e) {
            System.err.println("Eroare la crop: " + e.getMessage());
            return null;
        }
    }
}
