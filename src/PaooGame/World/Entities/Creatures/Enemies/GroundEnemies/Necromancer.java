package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Spells.SummonCircle;
import PaooGame.World.Entities.Creatures.Enemies.EnemyAction;
import java.awt.Rectangle;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta inamicul Necromancer care dispune de Lifesteal
 */
public class Necromancer extends GroundEnemy {
    public static final int LOGIC_WIDTH = 3 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 5 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2);
    public static final int VISUAL_WIDTH = 10 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 10 * TILE_SIZE;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 2.5f;
    public static final float ATTACK_RANGE = TILE_SIZE * 20;
    public static final float AGGRO_RANGE = TILE_SIZE * 25;
    public static final float X_RANGE = TILE_SIZE * 3;

    public static final float MAX_HP = 180f;
    public static final float KNOCK_PERCENT = 0.7f;

    private static final int SUMMON_WINDUP = 90;
    private static final int SUMMON_ACTIVE = 10;
    private static final int SUMMON_RECOVERY = 40;
    private static final int SUMMON_COOLDOWN = 200;

    private static final int LIFESTEAL_DURATION = 120;
    private static final int LIFESTEAL_COOLDOWN = 240;

    public static final float COLLISION_DMG = 20f;
    private final EnemyAction lifestealAction;

    public Necromancer(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;

        availableActions.add(new EnemyAction("SUMMON", SUMMON_WINDUP, SUMMON_ACTIVE, SUMMON_RECOVERY, SUMMON_COOLDOWN, ATTACK_RANGE, true));
        lifestealAction = new EnemyAction("LIFESTEAL", 0, LIFESTEAL_DURATION, 0, LIFESTEAL_COOLDOWN, 0, true);

        setAnimations();
    }

    @Override
    public void Update() {
        if (isDead) {
            handleDeath();
            return;
        }

        super.Update();
    }

    @Override
    public void onInteraction(Entity e) {
        if (isDead) return;

        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;
                dante.applyKnockback(knockX, -15.0f);
                pauseTimer = PAUSE_DURATION;
            }
        }
    }

    @Override
    protected void executeActionHitbox(String actionId) {
        if (actionId.equals("SUMMON")) {
            float[] danteCenter = handler.getDante().getCenter();

            float spellX = danteCenter[0] - SummonCircle.LOGIC_WIDTH / 2f;
            float spellY = danteCenter[1] - SummonCircle.LOGIC_WIDTH / 2f;

            handler.addEntity(new SummonCircle(handler, spellX, spellY, this));
        }
    }

    public void heal(float hp) {
        if (isDead) {
            return;
        }

        if (actionCooldowns.getOrDefault("LIFESTEAL", 0) <= 0) {
            this.hp += hp;
            if (this.hp > MAX_HP) {
                this.hp = MAX_HP;
            }

            startAction(lifestealAction);
        }
    }

    private void handleDeath() {
        if (deathTimer > 0) {
            deathTimer--;
            animations.get("DIE").Update();
        } else {
            active = false;
        }
    }

    @Override
    public void die() {
        if (isDead) return;
        isDead = true;
        animations.get("DIE").reset();
        deathTimer = 90;
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    @Override
    public void setAnimations() {
        try {
            animations.put("IDLE", new Animation(10, VisualAssets.Necromancer.get("Idle")));
            animations.put("WALK", new Animation(8, VisualAssets.Necromancer.get("Walk")));
            animations.put("HURT", new Animation(6, VisualAssets.Necromancer.get("Hurt")));

            Animation dieAnim = new Animation(12, VisualAssets.Necromancer.get("Die"));
            dieAnim.setFrameDuration(VisualAssets.Necromancer.get("Die").length - 1, -1);
            animations.put("DIE", dieAnim);

            animations.put("LIFESTEAL", new Animation(10, VisualAssets.Necromancer.get("Lifesteal")));

            Animation attackAnim = new Animation(SUMMON_WINDUP / 8, VisualAssets.Necromancer.get("Attack"));
            attackAnim.setFrameDuration(8, SUMMON_ACTIVE);
            int recoveryPerFrame = SUMMON_RECOVERY / 3;
            attackAnim.setFrameDuration(9, recoveryPerFrame);
            attackAnim.setFrameDuration(10, recoveryPerFrame);
            attackAnim.setFrameDuration(11, recoveryPerFrame);
            animations.put("SUMMON", attackAnim);

        } catch (Exception e) {
            System.err.println("[EROARE]: La setarea animatiilor Necromancer: " + e.getMessage());
        }
    }
}