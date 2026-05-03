package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Spells.SummonCircle;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Necromancer extends GroundEnemy {
    // Dimensiuni logice (Hitbox)
    public static final int LOGIC_WIDTH = 3 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 5 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2);

    // Dimensiuni vizuale (ajustează-le dacă sprite-ul e mai lat/înalt)
    public static final int VISUAL_WIDTH = 10 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 10 * TILE_SIZE;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 2.5f;
    public static final float ATTACK_RANGE = TILE_SIZE * 30;
    public static final float AGGRO_RANGE = TILE_SIZE * 36;
    public static final float X_RANGE = TILE_SIZE * 3;

    public static final float MAX_HP = 300;
    public static final float KNOCK_PERCENT = 0.7f;
    public static final float COLLISION_DMG = 20f;

    // --- Action States ---
    public enum ActionState {
        IDLE(0, 0, 0, 0),
        SUMMONING(90, 10, 40, 200), // Windup, Active, Recovery, Cooldown
        LIFESTEALING(0, 120, 0, 0);  // Durata totală a animației de lifesteal

        public final int windup, active, recovery, baseCooldown;

        ActionState(int windup, int active, int recovery, int baseCooldown) {
            this.windup = windup;
            this.active = active;
            this.recovery = recovery;
            this.baseCooldown = baseCooldown;
        }

        public int getDuration() { return windup + active + recovery; }
    }

    private ActionState currAction = ActionState.IDLE;
    private int actionTimer = 0;
    private int attackCooldown = 0;

    // Lifesteal specific Necromancer
    private static final int LIFESTEAL_COOLDOWN = 240;
    private int lifestealTimer = 0;

    // --- Animații ---
    public enum AnimationState {
        IDLE, WALK, ATTACK, LIFESTEAL, DIE, HURT
    }

    private final Animation[] animations = new Animation[AnimationState.values().length];
    private AnimationState currAnim = AnimationState.IDLE;
    private int deathTimer = 0;
    private boolean isDead = false;

    public Necromancer(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.attackRange = ATTACK_RANGE;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};

        setAnimations();
    }

    @Override
    public void Update() {
        if (isDead) {
            handleDeath();
            return;
        }

        super.Update();
        updateCooldowns();

        if (knockbackTimer > 0 || pauseTimer > 0) {
            currAction = ActionState.IDLE;
            actionTimer = 0;
        } else {
            updateActionState();
        }

        updateAnimationLogic();
    }

    private void handleDeath() {
        if (deathTimer > 0) {
            deathTimer--;
            animations[AnimationState.DIE.ordinal()].Update();
        } else {
            active = false;
        }
    }

    private void updateCooldowns() {
        if (attackCooldown > 0) attackCooldown--;
        if (lifestealTimer > 0) lifestealTimer--;
    }

    private void updateActionState() {
        if (currAction == ActionState.IDLE) return;

        actionTimer--;

        if (currAction == ActionState.SUMMONING) {
            // Spawnăm cercul magic exact când se termină Windup-ul
            if (actionTimer == (currAction.active + currAction.recovery)) {
                executeAttackHitbox();
            }
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    @Override
    protected void attack() {
        if (attackCooldown > 0 || currAction != ActionState.IDLE) return;

        currAction = ActionState.SUMMONING;
        actionTimer = currAction.getDuration();
        attackCooldown = currAction.baseCooldown;

        animations[AnimationState.ATTACK.ordinal()].reset();
    }

    private void executeAttackHitbox() {
        float[] danteCenter = handler.getDante().getCenter();

        float spellX = danteCenter[0] - SummonCircle.LOGIC_WIDTH / 2f;
        float spellY = danteCenter[1] - SummonCircle.LOGIC_WIDTH / 2f;

        handler.addEntity(
                new SummonCircle(handler, spellX, spellY, this)
        );
    }

    private void updateAnimationLogic() {
        AnimationState prevAnim = currAnim;

        if (knockbackTimer > 0) {
            currAnim = AnimationState.HURT;
        } else if (currAction == ActionState.SUMMONING) {
            currAnim = AnimationState.ATTACK;
        } else if (currAction == ActionState.LIFESTEALING) {
            currAnim = AnimationState.LIFESTEAL;
        } else if (Math.abs(xMove) > 0.1f) {
            currAnim = AnimationState.WALK;
        } else {
            currAnim = AnimationState.IDLE;
        }

        if (currAnim != prevAnim) {
            animations[currAnim.ordinal()].reset();
        }
        animations[currAnim.ordinal()].Update();
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT);

        // Oglindire în funcție de direcție
        if (direction != 1) {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX + VISUAL_WIDTH, drawY, -VISUAL_WIDTH, VISUAL_HEIGHT, null);
        } else {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 0, 100)); // Roșu pentru Hitbox
            g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);
        }
    }

    @Override
    public void die() {
        if (isDead) return;
        isDead = true;
        currAnim = AnimationState.DIE;
        currAction = ActionState.IDLE;
        animations[AnimationState.DIE.ordinal()].reset();
        deathTimer = 90;
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    @Override
    public void setAnimations() {
        try {
            animations[AnimationState.IDLE.ordinal()] = new Animation(10, VisualAssets.Necromancer.get("Idle"));
            animations[AnimationState.WALK.ordinal()] = new Animation(8, VisualAssets.Necromancer.get("Walk"));
            animations[AnimationState.HURT.ordinal()] = new Animation(6, VisualAssets.Necromancer.get("Hurt"));
            animations[AnimationState.DIE.ordinal()] = new Animation(12, VisualAssets.Necromancer.get("Die"));

            // Adăugăm animația de Lifesteal
            animations[AnimationState.LIFESTEAL.ordinal()] = new Animation(10, VisualAssets.Necromancer.get("Lifesteal")); // Ajustează viteza după preferințe

            // Sincronizare animație de invocare cu state-ul logicii
            animations[AnimationState.ATTACK.ordinal()] = new Animation(ActionState.SUMMONING.windup / 8, VisualAssets.Necromancer.get("Attack"));
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(8, ActionState.SUMMONING.active);
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(9, ActionState.SUMMONING.recovery / 3);
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(10, ActionState.SUMMONING.recovery / 3);
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(11, ActionState.SUMMONING.recovery / 3);
        }
        catch (Exception e) {
            System.err.println("Eroare la setare animatii Necromancer: " + e.getMessage());
        }
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

    public void heal(float hp) {
        if (isDead) return; // Evităm lifesteal din greșeală dacă entitatea moare fix în acel cadru

        if (lifestealTimer <= 0) {
            this.hp += hp;
            if (this.hp > MAX_HP) this.hp = MAX_HP;

            // Declanșăm starea de LIFESTEALING
            currAction = ActionState.LIFESTEALING;
            actionTimer = ActionState.LIFESTEALING.getDuration();

            // Resetăm animația specifică
            animations[AnimationState.LIFESTEAL.ordinal()].reset();

            lifestealTimer = LIFESTEAL_COOLDOWN;
        }
    }
}