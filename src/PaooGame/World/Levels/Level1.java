package PaooGame.World.Levels;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.World.Factories.EntityFactory;
import PaooGame.World.Factories.Level1Factory;
import PaooGame.World.Tiles.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

import static PaooGame.World.Tiles.Tile.SCALE;

public class Level1 extends Level {
    private final EntityFactory factory;
    private final static int L1_WIDTH = 500;
    private final static int L1_HEIGHT = 29;
    private final float[] px = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f}; // Factori Parallax X
    private final float[] py = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f}; // Factori Parallax Y
    private final int[] ox = {0, 0, 0, 2, -34, 7200};             // Offset X (rotunjit)
    private final int[] oy = {-250, -140, -170, -70, 62, -1};      // Offset Y

    public Level1(Handler handler) {
        super(handler, L1_WIDTH, L1_HEIGHT, 0, 0, "res/textures/Maps/L1/L1.tmx");
        factory = new Level1Factory(handler);
    }

    @Override
    public void drawBackground(Graphics g) {

        for (int i = 0; i < Assets.bg.length; i++) {
            if (Assets.bg[i] == null) continue;

            int x = (int) (ox[i] * SCALE - (handler.getGameCamera().getxOffset() * px[i]));
            int y = (int) (oy[i] * SCALE - (handler.getGameCamera().getyOffset() * py[i]));

            int imgW = (int) (Assets.bg[i].getWidth() * SCALE);
            int imgH = (int) (Assets.bg[i].getHeight() * SCALE);

            if (i < 5) {
                int movingX = x % imgW;
                if (movingX > 0) movingX -= imgW;

                for (int j = movingX; j < handler.getWidth(); j += imgW) {
                    g.drawImage(Assets.bg[i], j, y, imgW, imgH, null);
                }
            } else {
                g.drawImage(Assets.bg[i], x, y, imgW, imgH, null);
            }
        }
    }
}
