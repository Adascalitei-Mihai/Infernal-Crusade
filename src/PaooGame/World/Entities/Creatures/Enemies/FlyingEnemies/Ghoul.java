package PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Screech;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Ghoul extends FlyingEnemy {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 2 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(3, 3, DEFAULT_WIDTH - 3, DEFAULT_HEIGHT - 3);

    public static final int VISUAL_WIDTH = 7 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 5 * TILE_SIZE;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 2.5f;
    public static final float ATTACK_RANGE = TILE_SIZE * 20;
    public static final float AGGRO_RANGE = TILE_SIZE * 26;
    public static final float X_RANGE = TILE_SIZE * 3;

    public static final float MAX_HP = 100;
    public static final float KNOCK_PERCENT = 1f;
    public static final float COLLISION_DMG = 20f;

    protected static final float Y_RANGE = 30f;
    protected static final float HOVER_SPEED = 0.05f;

    // --- Sistemul ActionState (Inspirat direct de la Hunter) ---
    public enum ActionState {
        IDLE(0, 0, 0, 0),
        SCREECHING(40, 10, 30, 150); // Windup, Active, Recovery, Cooldown

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

    // --- Sistemul de Animatii ---
    public enum AnimationState {
        IDLE, FLY, ATTACK, HURT, DIE
    }

    private final Animation[] animations = new Animation[AnimationState.values().length];
    private AnimationState currAnim = AnimationState.IDLE;

    private boolean isDead = false;
    private boolean hitGround = false;

    public Ghoul(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_HP, KNOCK_PERCENT, Y_RANGE, HOVER_SPEED);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.attackRange = ATTACK_RANGE;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.gravity = 0f;

        setAnimations();
    }

    @Override
    public void Update() {
        if (isDead) {
            handleDeathPhysics();
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

        // Sincronizare cu FlyingEnemy (pentru ca makeDecision sa stie ca atacam si sa opreasca miscarea)
        isWindingUp = (currAction != ActionState.IDLE);
    }

    private void handleDeathPhysics() {
        if (!hitGround) {
            yMove += 0.8f; // Cadere
            if (yMove > 15f) yMove = 15f;

            float lastY = y;
            moveY();

            if (y == lastY || Math.abs(yMove) < 0.1f) {
                hitGround = true;
            }
        } else {
            animations[AnimationState.DIE.ordinal()].Update();
            if (animations[AnimationState.DIE.ordinal()].isFinished()) {
                active = false;
            }
        }
    }

    private void updateCooldowns() {
        if (attackCooldown > 0) attackCooldown--;
    }

    private void updateActionState() {
        if (currAction == ActionState.IDLE) return;

        actionTimer--;

        // Tragem proiectilul exact când se termină Windup-ul
        if (actionTimer == (currAction.active + currAction.recovery)) {
            executeAttackHitbox();
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    @Override
    protected void attack() {
        if (attackCooldown > 0 || currAction != ActionState.IDLE) return;

        currAction = ActionState.SCREECHING;
        actionTimer = currAction.getDuration();
        attackCooldown = currAction.baseCooldown;

        animations[AnimationState.ATTACK.ordinal()].reset();
    }

    private void executeAttackHitbox() {
        float[] spawnCoords = getCenter();
        float[] targetCoords = handler.getDante().getCenter();

        // Proiectilul Screech are dimensiunile de 1 * TILE_SIZE.
        // Scădem jumătate din dimensiunea lui din coordonatele de spawn pentru a-l centra perfect.
        float startX = spawnCoords[0] - ((float) TILE_SIZE);
        float startY = spawnCoords[1] - ((float) TILE_SIZE);

        handler.getLevel().getEntityManager().addEntity(
                new Screech(handler, startX, startY,
                        targetCoords[0], targetCoords[1], this)
        );
    }

    private void updateAnimationLogic() {
        AnimationState prevAnim = currAnim;

        if (knockbackTimer > 0) {
            currAnim = AnimationState.HURT;
        } else if (currAction == ActionState.SCREECHING) {
            currAnim = AnimationState.ATTACK;
        } else if (Math.abs(xMove) > 0.1f) {
            currAnim = AnimationState.FLY;
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

        int drawX = screenX + (DEFAULT_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (DEFAULT_HEIGHT - VISUAL_HEIGHT);

        if (direction != 1) {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX + VISUAL_WIDTH, drawY, -VISUAL_WIDTH, VISUAL_HEIGHT, null);
        } else {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 0, 100));
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
        isWindingUp = false;

        animations[AnimationState.DIE.ordinal()].reset();
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    @Override
    public void setAnimations() {
        try {
            animations[AnimationState.IDLE.ordinal()] = new Animation(10, VisualAssets.Ghoul.get("Idle"));
            animations[AnimationState.FLY.ordinal()] = new Animation(8, VisualAssets.Ghoul.get("Fly"));
            animations[AnimationState.HURT.ordinal()] = new Animation(6, VisualAssets.Ghoul.get("Hurt"));
            animations[AnimationState.DIE.ordinal()] = new Animation(12, VisualAssets.Ghoul.get("Die"));

            animations[AnimationState.ATTACK.ordinal()] = new Animation(ActionState.SCREECHING.windup / 6, VisualAssets.Ghoul.get("Attack"));
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(6, ActionState.SCREECHING.active);
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(7, ActionState.SCREECHING.recovery);
        }
        catch (Exception e) {
            System.err.println("Eroare la incarcarea animatiilor Ghoul: " + e.getMessage());
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if (isDead) return;

        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;
                dante.applyKnockback(knockX, -15.0f);
            }
        }
    }
}