package PaooGame.World.Levels;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies.Ghoul;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.*;
import PaooGame.World.Entities.Environment.Gate;
import PaooGame.World.Factories.Level1Factory;

import java.awt.*;

import static PaooGame.World.Tile.SCALE;
import static PaooGame.World.Tile.TILE_SIZE;

public class Level1 extends Level {
    private static final int L1_WIDTH = 500;
    private static final int L1_HEIGHT = 29;
    private static final float[] PX = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f};
    private static final float[] PY = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f};
    private static final int[] OX = {0, 0, 0, 2, -34, 7200};
    private static final int[] OY = {0, 0, -100, -70, 62, -1};

    private static final int[][] CHKP_POS = {
            {1, 20},
            {219, 20},
            {436, 20}
    };

    private static final int[] ARENA_COORDS = {
            450 * TILE_SIZE, 0, 500 * TILE_SIZE, 29 * TILE_SIZE
    };
    private static final int[][][][] ARENA_WAVES = {
            {
                    // 0: Minori
                    {
                            {18800, 680},
                            {19200, 680},
                            {19500, 680}
                    },
                    // 1: Majori
                    {}
            },
            {
                    // 0: Minori
                    {
                            {18400, 680},
                            {18800, 680},
                            {19400, 680}
                    },
                    {
                            {19000, 680}
                    }
            },
            {
                    // 0: Minori
                    {
                            {18350, 680},
                            {19550, 680}
                    },
                    // 1: Majori
                    {
                            {18700, 680},
                            {19300, 680}
                    }
            },
            {
                    // 0: Minori
                    {
                            {18500, 680},
                            {18800, 680},
                            {19300, 680},
                            {19600, 680}
                    },
                    // 1: Majori
                    {
                            {19000, 680}
                    }
            }
    };
    private static final int[] GATE_COORDS = {
            449 * TILE_SIZE, 20 * TILE_SIZE
    };

    public Level1(Handler handler) {
        super(handler, new Level1Factory(handler),
                L1_WIDTH, L1_HEIGHT, "res/textures/Maps/L1/L1.tmx", SongAssets.level1);

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

        for(int i = 0; i < CHKP_POS.length; ++i) {
            entityManager.addEntity(factory.createCheckpoint(
                    CHKP_POS[i][0] * TILE_SIZE, CHKP_POS[i][1] * TILE_SIZE, i));
        }

        Gate gate = factory.createGate(GATE_COORDS[0], GATE_COORDS[1]);
        Arena arena = new Arena(handler, ARENA_WAVES,
                ARENA_COORDS[0], ARENA_COORDS[1],
                ARENA_COORDS[2] - ARENA_COORDS[0],
                ARENA_COORDS[3] - ARENA_COORDS[1]);

        entityManager.addEntity(gate);
        entityManager.addEntity(factory.createArenaTrigger(
                GATE_COORDS[0] + TILE_SIZE * 4, GATE_COORDS[1],
                gate.getWidth(), gate.getHeight(),
                arena, new Gate[]{gate}, SongAssets.arena
        ));

        setEnemies();
        handler.getGameCamera().snapTo(handler.getDante());
    }

    @Override
    public void setEnemies() {
        entityManager.addEntity(new Skeleton(handler, 1000, 800));
        entityManager.addEntity(new Cyclops(handler, 2000, 400));
        entityManager.addEntity(new Hunter(handler, 3000, 800));
        entityManager.addEntity(new Necromancer(handler, 4000, 800));
        entityManager.addEntity(new Ghoul(handler, 800, 200));
        entityManager.addEntity(new Boss(handler, 5100, 640));
    }

    @Override
    public void drawBackground(Graphics g) {
        for (int i = 0; i < VisualAssets.bg.length; i++) {
            if (VisualAssets.bg[i] == null) continue;

            int x = (int) (OX[i] * SCALE - (handler.getGameCamera().getxOffset() * PX[i]));
            int y = (int) (OY[i] * SCALE - (handler.getGameCamera().getyOffset() * PY[i]));

            int imgW = (int) (VisualAssets.bg[i].getWidth() * SCALE);
            int imgH = (int) (VisualAssets.bg[i].getHeight() * SCALE);

            if (i < 5) {
                int movingX = x % imgW;
                if (movingX > 0) movingX -= imgW;

                for (int j = movingX; j < handler.getWidth(); j += imgW) {
                    g.drawImage(VisualAssets.bg[i], j, y, imgW, imgH, null);
                }
            } else {
                g.drawImage(VisualAssets.bg[i], x, y, imgW, imgH, null);
            }
        }
    }
}