package PaooGame.World.Levels;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Environment.Gate;
import PaooGame.World.Factories.Level2Factory;
import java.awt.Graphics;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta nivelul 2
 */
public class Level2 extends Level {
    private static final float SCALE = 1.25f;
    private static final int L2_WIDTH = 200;
    private static final int L2_HEIGHT = 300;

    private static final float[] PX = {0f, 0.5f, 1.0f, 1.0f, 1.0f};
    private static final float[] PY = {0f, 0.5f, 1.0f, 1.0f, 1.0f};

    private static final int[] OX = {0, 3, 4826, 4824, 4828};
    private static final int[] OY = {0, 8169, 670, 694, 1460};

    private boolean isTransitioning = false;

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

    private static final String[] SOLID_LAYERS = {
            "decor",
            "platforme pamant",
            "platforme iarba",
            "nori",
            "munte"
    };

    private static final int[][] CHKP_COORDS = {
            {TILE_SIZE, 292 * TILE_SIZE},
            {32 * TILE_SIZE, 217 * TILE_SIZE},
            {96 * TILE_SIZE, 147 * TILE_SIZE},
            {135 * TILE_SIZE, 45 * TILE_SIZE}
    };

    private static final int[] ARENA_COORDS = {
            151 * TILE_SIZE, 20 * TILE_SIZE, 199 * TILE_SIZE, 51 * TILE_SIZE
    };
    private static final int[] GATE_COORDS = {
            150 * TILE_SIZE, 45 * TILE_SIZE
    };

    private static final int[][] MINOR_ENEMIES = {
            {126 * TILE_SIZE, 277 * TILE_SIZE}, {54 * TILE_SIZE, 262 * TILE_SIZE},
            {68 * TILE_SIZE, 245 * TILE_SIZE}, {114 * TILE_SIZE, 243 * TILE_SIZE},
            {128 * TILE_SIZE, 235 * TILE_SIZE}, {116 * TILE_SIZE, 232 * TILE_SIZE},
            {122 * TILE_SIZE, 222 * TILE_SIZE}, {92 * TILE_SIZE, 219 * TILE_SIZE},
            {55 * TILE_SIZE, 213 * TILE_SIZE}, {10 * TILE_SIZE, 205 * TILE_SIZE},
            {55 * TILE_SIZE, 198 * TILE_SIZE}, {100 * TILE_SIZE, 190 * TILE_SIZE},
            {156 * TILE_SIZE, 172 * TILE_SIZE}, {193 * TILE_SIZE, 157 * TILE_SIZE}
    };
    private static final int[][] MAJOR_ENEMIES = {
            {81 * TILE_SIZE, 136 * TILE_SIZE}, {35 * TILE_SIZE, 132 * TILE_SIZE},
            {52 * TILE_SIZE, 115 * TILE_SIZE}, {92 * TILE_SIZE, 109 * TILE_SIZE},
            {109 * TILE_SIZE, 103 * TILE_SIZE}, {123 * TILE_SIZE, 77 * TILE_SIZE},
            {78 * TILE_SIZE, 78 * TILE_SIZE, 62 * TILE_SIZE, 75 * TILE_SIZE},
            {41 * TILE_SIZE, 73 * TILE_SIZE}, {12 * TILE_SIZE, 61 * TILE_SIZE},
            {69 * TILE_SIZE, 45 * TILE_SIZE}, {120 * TILE_SIZE, 27 * TILE_SIZE}
    };
    private static final int[][][][] ARENA_WAVES = {
            {{{160 * TILE_SIZE, 42 * TILE_SIZE}, {194 * TILE_SIZE, 40 * TILE_SIZE}},
                    {{170 * TILE_SIZE, 36 * TILE_SIZE}, {180 * TILE_SIZE, 41 * TILE_SIZE}}},
            {{{165 * TILE_SIZE, 48 * TILE_SIZE}, {175 * TILE_SIZE, 48 * TILE_SIZE}, {183 * TILE_SIZE, 33 * TILE_SIZE}},
                    {{158 * TILE_SIZE, 35 * TILE_SIZE}, {175 * TILE_SIZE, 32 * TILE_SIZE}, {192 * TILE_SIZE, 30 * TILE_SIZE}}},
            {{{153 * TILE_SIZE, 48 * TILE_SIZE}, {197 * TILE_SIZE, 48 * TILE_SIZE}},
                    {{159 * TILE_SIZE, 31 * TILE_SIZE}, {174 * TILE_SIZE, 27 * TILE_SIZE}, {193 * TILE_SIZE, 30 * TILE_SIZE}}},
            {{{159 * TILE_SIZE, 42 * TILE_SIZE}, {169 * TILE_SIZE, 37 * TILE_SIZE}, {184 * TILE_SIZE, 44 * TILE_SIZE}, {194 * TILE_SIZE, 40 * TILE_SIZE}},
                    {{159 * TILE_SIZE, 31 * TILE_SIZE}, {174 * TILE_SIZE, 27 * TILE_SIZE}}}
    };
    public Level2(Handler handler) {
        super(handler, new Level2Factory(handler),
                L2_WIDTH, L2_HEIGHT, "/textures/Maps/L2/L2.tmx", SongAssets.level1,
                RENDER_ORDER, SOLID_LAYERS, MINOR_ENEMIES, MAJOR_ENEMIES);

        calculateExposureMasks();
        requestRespawn();
    }

    @Override
    public void Update() {
        if(!isTransitioning) {
            super.Update();
            if (arena.isFinished()) {

                isTransitioning = true;

                handler.updateCurrChkp(4);
                handler.updateCurrLevel(3);

                handler.getTransitionManager().start(() -> {
                    Level3 nextLevel = new Level3(handler);
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
                GATE_COORDS[0] + TILE_SIZE * 2, GATE_COORDS[1],
                gate.getLogicWidth(), gate.getLogicHeight(),
                arena, new Gate[]{gate}, SongAssets.arena
        ));

        setEnemies();
        handler.getDante().resetDeath();
        handler.getGameCamera().snapTo(handler.getDante());
    }

    @Override
    public void drawBackground(Graphics g) {
        for (int i = 0; i < VisualAssets.bg2.length; i++) {
            if (VisualAssets.bg2[i] == null) continue;

            int x = (int) ((OX[i] * SCALE - handler.getGameCamera().getxOffset()) * PX[i]);
            int y = (int) ((OY[i] * SCALE - handler.getGameCamera().getyOffset()) * PY[i]);

            int imgW = (int) (VisualAssets.bg2[i].getWidth() * SCALE);
            int imgH = (int) (VisualAssets.bg2[i].getHeight() * SCALE);

            if (i == 0) {
                int startX = x % imgW;
                if (startX > 0) startX -= imgW;

                int startY = y % imgH;
                if (startY > 0) startY -= imgH;

                for (int drawX = startX; drawX < handler.getWidth(); drawX += imgW) {
                    for (int drawY = startY; drawY < handler.getHeight(); drawY += imgH) {
                        g.drawImage(VisualAssets.bg2[i], drawX, drawY, imgW, imgH, null);
                    }
                }
            } else {
                g.drawImage(VisualAssets.bg2[i], x, y, imgW, imgH, null);
            }
        }
    }
}