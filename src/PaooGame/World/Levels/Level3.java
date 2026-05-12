package PaooGame.World.Levels;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Boss;
import PaooGame.World.Factories.Level3Factory;
import java.awt.Graphics;

import static PaooGame.World.Tile.TILE_SIZE;

public class Level3 extends Level {
    private static final float SCALE = 1.25f;

    private static final int L3_WIDTH = 150;
    private static final int L3_HEIGHT = 198;

    private static final int[] OX = {704, 224, 4640, 4192, 4576,  32,   0, -128, -352,   32, -128, 128,  0, 32};
    private static final int[] OY = {  0, 5664, 4896, 4096, 3552, 768, 672,  576,  512,   0,  416, -96,  0, 96};

    private static final String[] RENDER_ORDER = {
            "Solid",
            "Deco"
    };

    private static final String[] SOLID_LAYERS = {
            "Solid",
            "Deco"
    };

    private static final int[][] CHKP_COORDS = {
            {2 * TILE_SIZE, 7 * TILE_SIZE},
            {84 * TILE_SIZE, 116 * TILE_SIZE},
    };

    private final static float[] BOSS_COORDS = {114 * TILE_SIZE, 175 * TILE_SIZE};

    public Level3(Handler handler) {
        super(handler, new Level3Factory(handler),
                L3_WIDTH, L3_HEIGHT, "/textures/Maps/L3/L3.tmx", SongAssets.level3,
                RENDER_ORDER, SOLID_LAYERS);

        calculateExposureMasks();
        requestRespawn();
    }

    @Override
    public void spawn() {
        handler.getGameCamera().resetToLevelBounds();
        AudioManager.playBGM(bgSong);

        entityManager.getDante().setX(CHKP_COORDS[handler.getCurrChkp()][0]);
        entityManager.getDante().setY(CHKP_COORDS[handler.getCurrChkp()][1]);

        entityManager.resetEntities();

        for(int i = 0; i < CHKP_COORDS.length; ++i) {
            entityManager.addEntity(factory.createCheckpoint(
                    CHKP_COORDS[i][0], CHKP_COORDS[i][1], i));
        }

        setEnemies();
        handler.getDante().resetDeath();
        handler.getGameCamera().snapTo(handler.getDante());
    }

    @Override
    public void setEnemies() {
        boss = new Boss(handler, BOSS_COORDS[0], BOSS_COORDS[1]);
        entityManager.addEntity(boss);
    }

    @Override
    public void drawBackground(Graphics g) {
        for (int i = 0; i < VisualAssets.bg3.length; i++) {
            if (VisualAssets.bg3[i] == null) continue;

            // Calculul poziției luând în considerare offset-ul camerei și scalarea
            int x = (int) (OX[i] * SCALE - handler.getGameCamera().getxOffset());
            int y = (int) (OY[i] * SCALE - handler.getGameCamera().getyOffset());

            int imgW = (int) (VisualAssets.bg3[i].getWidth() * SCALE);
            int imgH = (int) (VisualAssets.bg3[i].getHeight() * SCALE);

            // Index 0 e cerul/golul negru, vrem să repete peste tot (atât pe X cât și pe Y)
            if (i == 0) {
                int startX = x % imgW;
                if (startX > 0) startX -= imgW;
                int startY = y % imgH;
                if (startY > 0) startY -= imgH;

                for (int drawX = startX; drawX < handler.getWidth(); drawX += imgW) {
                    for (int drawY = startY; drawY < handler.getHeight(); drawY += imgH) {
                        g.drawImage(VisualAssets.bg3[i], drawX, drawY, imgW, imgH, null);
                    }
                }
            }
            else {
                int startX = x % imgW;
                if (startX > 0) startX -= imgW;

                for (int drawX = startX; drawX < handler.getWidth(); drawX += imgW) {
                    g.drawImage(VisualAssets.bg3[i], drawX, y, imgW, imgH, null);
                }
            }
        }
    }
}