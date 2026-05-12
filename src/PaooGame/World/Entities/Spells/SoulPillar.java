package PaooGame.World.Entities.Spells;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta o vraja stalp de suflete
 */
public class SoulPillar extends Spell {

    public final static int LOGIC_WIDTH = TILE_SIZE * 2;
    public final static int LOGIC_HEIGHT = TILE_SIZE * 6;

    public final static int VISUAL_WIDTH = TILE_SIZE * 4;
    public final static int VISUAL_HEIGHT = TILE_SIZE * 7;

    private final static int WINDUP_TIME = 20;
    private final static int ACTIVE_TIME = 10;
    private final static int RECOVERY_TIME = 20;

    private final static float DMG = 50f;
    private final static float KNOCK_PERCENT = 0.5f;

    public SoulPillar(Handler handler, float x, float y, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, VISUAL_WIDTH, VISUAL_HEIGHT,
                owner, DMG, KNOCK_PERCENT, WINDUP_TIME, ACTIVE_TIME, RECOVERY_TIME);

        setAnimations();
    }

    protected void setAnimations() {
        try {
            animation = new Animation(WINDUP_TIME / 3, VisualAssets.SoulPillar);

            animation.setFrameDuration(3, ACTIVE_TIME);
            animation.setFrameDuration(4, RECOVERY_TIME);
            animation.setFrameDuration(5, RECOVERY_TIME);
            animation.setFrameDuration(6, RECOVERY_TIME);
            animation.setFrameDuration(7, RECOVERY_TIME);
            animation.setFrameDuration(8, RECOVERY_TIME);
            animation.setFrameDuration(9, RECOVERY_TIME);

        } catch (Exception e) {
            System.err.println("[EROARE]: La setarea animatiilor SoulPillar: " + e.getMessage());
        }
    }
}