package PaooGame.World.Levels;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;

import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.*;
import PaooGame.World.Factories.Level2Factory; // Presupunând că ai un factory separat, altfel folosește Level1Factory

import java.awt.*;

import static PaooGame.World.Tile.SCALE;
import static PaooGame.World.Tile.TILE_SIZE;

public class Level2 extends Level {
    // Dimensiunile citite direct din TMX: width="200" height="300"
    private static final int L2_WIDTH = 200;
    private static final int L2_HEIGHT = 300;

    // Setări Parallax pentru Backgrounds extrase din TMX
    // Index 0: cer | Index 1: ceata | Index 2: arena1 | Index 3: arena2 | Index 4: arena3
    // Cerul e fix (0f), ceața are puțin parallax (0.5f), arenele sunt fixe pe hartă (1.0f)
    private static final float[] PX = {0f, 0.5f, 1.0f, 1.0f, 1.0f};
    private static final float[] PY = {0f, 0.5f, 1.0f, 1.0f, 1.0f};

    // Offset-urile (offsetx, offsety) extrase din <imagelayer>
    private static final int[] OX = {0, 3, 4826, 4824, 4828};
    private static final int[] OY = {0, 8169, 670, 694, 1460};

    // --- CONFIGURARE TMX PARSER ---
    // Cele 5 layere dorite, ordonate de la cel mai îndepărtat (nori) la cel mai apropiat (iarba)
    private static final String[] RENDER_ORDER = {
            "nori",
            "munte",
            "fundal frunze",
            "copaci",
            "pamant si frunze",
            "frunze",
            "platforme iarba",
            "platforme pamant",
            "decor"
    };
    // Stratul folosit pentru calculul coliziunilor fizice
    private static final String[] SOLID_LAYERS = {
            "decor",
            "platforme pamant",
            "platforme iarba"
    };

    // --- CHKP & ARENA (Placeholder temporar, conform cerinței) ---
    private static final int[][] CHKP_POS = { {1, 292} };
    private static final int[] ARENA_COORDS = { 0, 0, 0, 0 };
    private static final int[][][][] ARENA_WAVES = { { { {0, 0} }, {} } };
    private static final int[] GATE_COORDS = { 0, 0 };

    public Level2(Handler handler) {
        // Trimitem SOLID_LAYERS în loc de SOLID_LAYER
        super(handler, new Level2Factory(handler),
                L2_WIDTH, L2_HEIGHT, "/textures/Maps/L2/L2.tmx", SongAssets.level1,
                RENDER_ORDER, SOLID_LAYERS);

        calculateExposureMasks();
        requestRespawn();
    }

    @Override
    public void spawn() {
        handler.getGameCamera().resetToLevelBounds();
        AudioManager.playBGM(bgSong);

        entityManager.getDante().setX(CHKP_POS[handler.getCurrChkp()][0] * TILE_SIZE);
        entityManager.getDante().setY(CHKP_POS[handler.getCurrChkp()][1] * TILE_SIZE);

        entityManager.resetEntities();

        // Checkpoints placeholder
        for(int i = 0; i < CHKP_POS.length; ++i) {
            entityManager.addEntity(factory.createCheckpoint(
                    CHKP_POS[i][0] * TILE_SIZE, CHKP_POS[i][1] * TILE_SIZE, i));
        }

        setEnemies();
        handler.getDante().resetDeath();
        handler.getGameCamera().snapTo(handler.getDante());
    }

    @Override
    public void setEnemies() {
        // Placeholder pentru inamici (poți adăuga în funcție de noile coordonate din Tiled)
        entityManager.addEntity(new Skeleton(handler, 1000, 800));
        boss = new Boss(handler, 2000, 640);
        entityManager.addEntity(boss);
    }

    @Override
    public void drawBackground(Graphics g) {
        // Presupunând că ai definit VisualAssets.bg2 pentru imaginile din Level 2
        // Dacă folosești tot bg1, schimbă "bg2" în "bg1"
        for (int i = 0; i < VisualAssets.bg2.length; i++) {
            if (VisualAssets.bg2[i] == null) continue;

            int x = (int) (OX[i] * SCALE - (handler.getGameCamera().getxOffset() * PX[i]));
            int y = (int) (OY[i] * SCALE - (handler.getGameCamera().getyOffset() * PY[i]));

            int imgW = (int) (VisualAssets.bg2[i].getWidth() * SCALE);
            int imgH = (int) (VisualAssets.bg2[i].getHeight() * SCALE);

            // Primele două layere (Cerul și Ceața) trebuie să aibă repeat-x
            if (i < 2) {
                int movingX = x % imgW;
                if (movingX > 0) movingX -= imgW;

                for (int j = movingX; j < handler.getWidth(); j += imgW) {
                    g.drawImage(VisualAssets.bg2[i], j, y, imgW, imgH, null);
                }
            } else {
                // Imaginile de arenă nu se repetă
                g.drawImage(VisualAssets.bg2[i], x, y, imgW, imgH, null);
            }
        }
    }
}