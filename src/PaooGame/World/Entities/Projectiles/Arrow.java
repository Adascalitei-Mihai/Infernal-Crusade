package PaooGame.World.Entities.Projectiles;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta o sageata, cu animatii in {@link VisualAssets#Arrow}
 */
public class Arrow extends Projectile {
    private final static int LOGIC_WIDTH = (int) (TILE_SIZE * 1.5f);
    private final static int LOGIC_HEIGHT = (int) (TILE_SIZE * 0.5f);

    private final static int VISUAL_WIDTH = (int) (TILE_SIZE * 1.5f);
    private final static int VISUAL_HEIGHT = (int) (TILE_SIZE * 0.5f);

    private final static float SPEED = 10f;
    private final static float DMG = 30f;
    private final static float KNOCK_PERCENT = 1f;

    public Arrow(Handler handler, float x, float y, float tx, float ty, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, VISUAL_WIDTH, VISUAL_HEIGHT,
                tx, ty, SPEED, owner, DMG, KNOCK_PERCENT);

        this.animation = new Animation(15, VisualAssets.Arrow);
    }
}