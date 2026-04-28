package PaooGame.World.Levels;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies.Ghoul;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.*;
import PaooGame.World.Factories.EntityFactory;
import PaooGame.World.Factories.Level1Factory;

import java.awt.*;

import static PaooGame.World.Tile.SCALE;
import static PaooGame.World.Tile.TILE_SIZE;

public class Level1 extends Level {
    private final EntityFactory factory;
    private final static int L1_WIDTH = 500;
    private final static int L1_HEIGHT = 29;
    private final float[] px = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f}; // Factori Parallax X
    private final float[] py = {0f, 0.3f, 0.6f, 1.0f, 1.0f, 1.0f}; // Factori Parallax Y
    private final int[] ox = {0, 0, 0, 2, -34, 7200};             // Offset X (rotunjit)
    private final int[] oy = {0, 0, -100, -70, 62, -1};      // Offset Y

    private static final int[][] chkpPos = {
            {1, 20},
            {219, 20},
            {436, 20}
    };
    private static final int[] arenaCoords = {
            450, 0, 500, 29
    };
    private static final int[] gateCoords = {
            450, 20
    };

    public Level1(Handler handler) {
        super(handler, L1_WIDTH, L1_HEIGHT, "res/textures/Maps/L1/L1.tmx");
        factory = new Level1Factory(handler);

        requestRespawn();
    }

    @Override
    public void spawn() {
        entityManager.getDante().setX(chkpPos[handler.getCurrChkp()][0] * TILE_SIZE);
        entityManager.getDante().setY(chkpPos[handler.getCurrChkp()][1] * TILE_SIZE);

        entityManager.resetEntities();
        setEnemies();


        for(int i = 0; i < chkpPos.length; ++i) {
            entityManager.addEntity(factory.createCheckpoint(
                    chkpPos[i][0] * TILE_SIZE, chkpPos[i][1] * TILE_SIZE, i));
        } // Checkpoints

        entityManager.addEntity(factory.createCameraTrigger(
                gateCoords[0] * TILE_SIZE, gateCoords[1] * TILE_SIZE, TILE_SIZE, 4 * TILE_SIZE,
                arenaCoords[0] * TILE_SIZE, arenaCoords[1] * TILE_SIZE,
                arenaCoords[2] * TILE_SIZE, arenaCoords[3] * TILE_SIZE,
                true
        )); // Camera

        entityManager.addEntity(factory.createEnemyTrigger(
                arenaCoords[0] * TILE_SIZE, arenaCoords[1] * TILE_SIZE,
                arenaCoords[2] * TILE_SIZE, arenaCoords[3] * TILE_SIZE
        )); // Arena
    }

    @Override
    public void setEnemies() {
        // entityManager.addEntity(new Skeleton(handler, 1000, 800));
        // entityManager.addEntity(new Cyclops(handler, 2000, 400));
        // entityManager.addEntity(new Hunter(handler, 3000, 800));
        // entityManager.addEntity(new Necromancer(handler, 4000, 800));
        // entityManager.addEntity(new Ghoul(handler, 800, 200));
        entityManager.addEntity(new Boss(handler, 5100, 640));
        // entityManager.addEntity(factory.createMinorEnemy(4000, 800));
        // entityManager.addEntity(factory.createMajorEnemy(5000, 800));
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
