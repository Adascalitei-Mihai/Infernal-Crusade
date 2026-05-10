package PaooGame.World.Entities.Projectiles;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta un tipat, cu animatii in {@link VisualAssets#Screech}
 */
public class Screech extends Projectile {
    private final static int LOGIC_WIDTH = 2 * TILE_SIZE;
    private final static int LOGIC_HEIGHT = 2 * TILE_SIZE;

    private final static int VISUAL_WIDTH = 4 * TILE_SIZE;
    private final static int VISUAL_HEIGHT = 4 * TILE_SIZE;

    private final static float SPEED = 10f;
    private final static float DMG = 40f;
    private final static float KNOCK_PERCENT = 1.5f;

    public Screech(Handler handler, float x, float y, float tx, float ty, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT,
                VISUAL_WIDTH, VISUAL_HEIGHT,
                tx, ty, SPEED, owner, DMG, KNOCK_PERCENT);

        this.animation = new Animation(5, VisualAssets.Screech);
    }
}