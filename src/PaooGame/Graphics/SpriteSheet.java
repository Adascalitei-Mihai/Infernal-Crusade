package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/**
 * Clasa care retine o imagine formata din dale
 */
public class SpriteSheet {
    private final BufferedImage spriteSheet;
    private final int tileWidth;
    private final int tileHeight;

    /**
     * Constructorul care initializeaza spritesheet-ul
     * @param spriteSheet Imaginea totala
     * @param tileWidth Latimea unei dale
     * @param tileHeight Inaltimea unei dale
     */
    public SpriteSheet(BufferedImage spriteSheet, int tileWidth, int tileHeight) {
        this.spriteSheet = spriteSheet;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Metoda care selecteaza o portiune din imaginea totala, foloseste {@link #crop(int, int)}
     * @param row Randul ales
     * @param col Coloana aleasa
     * @param count Numarul de elemente de extras
     * @return Vectorul de dale
     */
    public BufferedImage[] makeSelection(int row, int col, int count) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            frames[i] = crop(i + col, row);
        }
        return frames;
    }

    /**
     * Metoda care returneaza o dala
     * @param x Numarul dalei pe axa x
     * @param y Numarul dalei pe axa y
     * @return Dala selectata
     */
    public BufferedImage crop(int x, int y) {
        try {
            int startX = x * tileWidth;
            int startY = y * tileHeight;

            if (startX + tileWidth > spriteSheet.getWidth()) {
                System.err.println("[EROARE]: Taiere pe X in afara limitelor la indicele " + x);
                return spriteSheet.getSubimage(0, 0, tileWidth, tileHeight);
            }
            if (startY + tileHeight > spriteSheet.getHeight()) {
                System.err.println("[EROARE]: Taiere pe Y in afara limitelor la inaltimea " + tileHeight);
                return spriteSheet.getSubimage(0, 0, tileWidth, tileHeight);
            }

            return spriteSheet.getSubimage(startX, startY, tileWidth, tileHeight);
        } catch (Exception e) {
            System.err.println("[EROARE]: Taiere: " + e.getMessage());
        }
        return null;
    }
}
