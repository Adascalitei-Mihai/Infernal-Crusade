package PaooGame.World.Entities.Projectiles;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta o sulita, cu animatii in {@link VisualAssets#SoulSpear}
 */
public class SoulSpear extends Projectile {
    private final static int LOGIC_WIDTH = TILE_SIZE * 2;
    private final static int LOGIC_HEIGHT = TILE_SIZE * 2;

    private final static int VISUAL_WIDTH = TILE_SIZE * 4;
    private final static int VISUAL_HEIGHT = TILE_SIZE * 4;

    private final static float SPEED = 14f;
    private final static float DMG = 80f;
    private final static float KNOCK_PERCENT = 2f;

    public SoulSpear(Handler handler, float x, float y, float tx, float ty, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT,
                VISUAL_WIDTH, VISUAL_HEIGHT, tx, ty,
                SPEED, owner, DMG, KNOCK_PERCENT);

        this.animation = new Animation(10, VisualAssets.SoulSpear);
    }
}