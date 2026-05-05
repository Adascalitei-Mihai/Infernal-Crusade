package PaooGame.World.Entities.Spells;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class SoulPillar extends Spell {
    // Dimensiuni logice (Hitbox-ul care rănește jucătorul)
    public final static int LOGIC_WIDTH = TILE_SIZE * 2;
    public final static int LOGIC_HEIGHT = TILE_SIZE * 6;

    // Dimensiuni vizuale (De regulă mai mari pentru a cuprinde particulele/strălucirea)
    public final static int VISUAL_WIDTH = TILE_SIZE * 4;
    public final static int VISUAL_HEIGHT = TILE_SIZE * 7;

    // Timpii stărilor
    private final static int WINDUP_TIME = 20;   // Timpul de avertizare vizuală
    private final static int ACTIVE_TIME = 10;   // Timpul în care aplică damage (burst)
    private final static int RECOVERY_TIME = 20; // Timpul de disipare

    private final static float DMG = 50f;
    private final static float KNOCK_PERCENT = 0.5f;

    // --- State Machine ---
    public enum SpellState {
        WINDUP, ACTIVE, RECOVERY, DEAD
    }
    private SpellState currState = SpellState.WINDUP;
    private int stateTimer = WINDUP_TIME;
    private boolean hasDealtDamage = false;

    // --- Animatii ---
    private Animation animation;

    public SoulPillar(Handler handler, float x, float y, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, owner, DMG, KNOCK_PERCENT, WINDUP_TIME, ACTIVE_TIME);

        this.bounds = new Rectangle(0, 0, LOGIC_WIDTH, LOGIC_HEIGHT);

        setAnimations();
    }

    @Override
    public void Update() {
        if (currState == SpellState.DEAD) {
            active = false;
            return;
        }

        updateStateTimer();
        if (animation != null) {
            animation.Update();
        }
    }

    private void updateStateTimer() {
        stateTimer--;

        if (stateTimer <= 0) {
            switch (currState) {
                case WINDUP:
                    currState = SpellState.ACTIVE;
                    stateTimer = ACTIVE_TIME;
                    break;
                case ACTIVE:
                    currState = SpellState.RECOVERY;
                    stateTimer = RECOVERY_TIME;
                    break;
                case RECOVERY:
                    currState = SpellState.DEAD;
                    break;
            }
        }

        // Damage aplicat o singură dată (burst) pe primul frame din ACTIVE
        if (currState == SpellState.ACTIVE && !hasDealtDamage) {
            executeHitbox();
            hasDealtDamage = true;
        }
    }

    private void executeHitbox() {
        Rectangle hitArea = getCollisionBounds(0, 0);
        Dante dante = handler.getDante();

        if (hitArea.intersects(dante.getCollisionBounds(0, 0))) {
            if (dante.hurt(DMG)) {
                // Knockback dinspre centrul stâlpului înspre exterior
                float centerX = x + (float) LOGIC_WIDTH / 2;
                float knockX = (dante.getX() > centerX) ? 10.0f : -10.0f;

                // Aplicăm knockback-ul ținând cont de KNOCK_PERCENT
                dante.applyKnockback(knockX * KNOCK_PERCENT, -8.0f);
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT);

        if (animation != null && animation.getCurrentFrame() != null) {
            g.drawImage(animation.getCurrentFrame(), drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 255, 100));
            g.fillRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);
        }
    }

    public void setAnimations() {
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
            System.err.println("Eroare la setare animații SoulPillar: " + e.getMessage());
        }
    }
}