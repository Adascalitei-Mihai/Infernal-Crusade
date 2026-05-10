package PaooGame.World.Levels;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Environment.Gate;
import PaooGame.World.Factories.Level1Factory;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Level1 extends Level {
    private static final float SCALE = 2.5f;
    private static final int L1_WIDTH = 500;
    private static final int L1_HEIGHT = 29;

    private static final float[] PX = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f};
    private static final float[] PY = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f};
    private static final int[] OX = {0, 0, 0, 2, -34, 7200};
    private static final int[] OY = {0, 0, -100, -70, 62, -1};
    private static final String[] RENDER_ORDER = {"Tile Layer 1"};
    private static final String[] SOLID_LAYERS = {"Tile Layer 1"};

    private static final int[][] CHKP_COORDS = {
            {TILE_SIZE, 20 * TILE_SIZE},
            {219 * TILE_SIZE, 20 * TILE_SIZE},
            {436 * TILE_SIZE, 20 * TILE_SIZE}
    };

    private static final int[] ARENA_COORDS = {
            450 * TILE_SIZE, TILE_SIZE, 500 * TILE_SIZE, 28 * TILE_SIZE
    };
    private static final int[] GATE_COORDS = {
            449 * TILE_SIZE, 20 * TILE_SIZE
    };

    private static final int[][] MINOR_ENEMIES = {
            {2480, 800}, {2036, 520}, {4600, 240}, {4400, 800}, {4880, 800}, {6070, 240}, {7520, 280}, {8448, 120}, {12901, 560}, {17060, 480}
    };
    private static final int[][] MAJOR_ENEMIES = {
            {6680, 720}, {10125, 720}, {13900, 720}
    };
    private static final int[][][][] ARENA_WAVES = {
            {{{18800, 680}, {19200, 680}, {19500, 680}}, {}},
            {{{18400, 680}, {18800, 680}, {19400, 680}}, {{19000, 680}}},
            {{{18350, 680}, {19550, 680}}, {{18700, 680}, {19300, 680}}},
            {{{18500, 680}, {18800, 680}, {19300, 680}, {19600, 680}}, {{19000, 680}}}
    };

    private boolean isTransitioning = false;

    public Level1(Handler handler) {
        super(handler, new Level1Factory(handler),
                L1_WIDTH, L1_HEIGHT, "/textures/Maps/L1/L1.tmx", SongAssets.level1,
                RENDER_ORDER, SOLID_LAYERS);

        calculateExposureMasks();
        requestRespawn();
    }

    @Override
    public void Update() {
        if(!isTransitioning) {
            super.Update();
            if (arena.isFinished()) {

                isTransitioning = true;

                handler.updateCurrChkp(3);
                handler.updateCurrLevel(2);

                handler.getTransitionManager().start(() -> {
                    Level2 nextLevel = new Level2(handler);
                    handler.setLevel(nextLevel);
                });
            }
        }
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

        Gate gate = factory.createGate(GATE_COORDS[0], GATE_COORDS[1]);
        arena = new Arena(handler, ARENA_WAVES,
                ARENA_COORDS[0], ARENA_COORDS[1],
                ARENA_COORDS[2] - ARENA_COORDS[0],
                ARENA_COORDS[3] - ARENA_COORDS[1]);

        entityManager.addEntity(gate);
        entityManager.addEntity(factory.createArenaTrigger(
                GATE_COORDS[0] + TILE_SIZE * 4, GATE_COORDS[1],
                gate.getLogicWidth(), gate.getLogicHeight(),
                arena, new Gate[]{gate}, SongAssets.arena
        ));

        setEnemies();
        handler.getDante().resetDeath();
        handler.getGameCamera().snapTo(handler.getDante());
    }

    @Override
    public void setEnemies() {
        for (int[] coords : MINOR_ENEMIES) {
            entityManager.addEntity(factory.createMinorEnemy(coords[0], coords[1]));
        }
        for (int[] coords : MAJOR_ENEMIES) {
            entityManager.addEntity(factory.createMajorEnemy(coords[0], coords[1]));
        }
    }

    @Override
    public void drawBackground(Graphics g) {
        for (int i = 0; i < VisualAssets.bg1.length; i++) {
            if (VisualAssets.bg1[i] == null) continue;

            int x = (int) (OX[i] * SCALE - (handler.getGameCamera().getxOffset() * PX[i]));
            int y = (int) (OY[i] * SCALE - (handler.getGameCamera().getyOffset() * PY[i]));

            int imgW = (int) (VisualAssets.bg1[i].getWidth() * SCALE);
            int imgH = (int) (VisualAssets.bg1[i].getHeight() * SCALE);

            if (i < 5) {
                int movingX = x % imgW;
                if (movingX > 0) movingX -= imgW;

                for (int j = movingX; j < handler.getWidth(); j += imgW) {
                    g.drawImage(VisualAssets.bg1[i], j, y, imgW, imgH, null);
                }
            } else {
                g.drawImage(VisualAssets.bg1[i], x, y, imgW, imgH, null);
            }
        }
    }
}