package PaooGame.World.Entities.Spells;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta o vraja cerc demonic
 */
public class SummonCircle extends Spell {
    public final static int LOGIC_WIDTH = TILE_SIZE * 8;
    public final static int LOGIC_HEIGHT = TILE_SIZE * 8;

    public final static int VISUAL_WIDTH = TILE_SIZE * 7;
    public final static int VISUAL_HEIGHT = TILE_SIZE * 11;

    private final static int WINDUP_TIME = 60;
    private final static int ACTIVE_TIME = 15;
    private final static int RECOVERY_TIME = 30;

    private final static float DMG = 100f;
    private final static float KNOCK_PERCENT = 1f;

    public SummonCircle(Handler handler, float x, float y, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, VISUAL_WIDTH, VISUAL_HEIGHT,
                owner, DMG, KNOCK_PERCENT, WINDUP_TIME, ACTIVE_TIME, RECOVERY_TIME);

        setAnimations();
    }

    public void setAnimations() {
        try {
            animation = new Animation(WINDUP_TIME / 3, VisualAssets.SummonCircle);
            animation.setFrameDuration(3, ACTIVE_TIME);
            animation.setFrameDuration(4, RECOVERY_TIME);

        } catch (Exception e) {
            System.err.println("[EROARE]: La setarea animatiilor SummonCircle: " + e.getMessage());
        }
    }
}