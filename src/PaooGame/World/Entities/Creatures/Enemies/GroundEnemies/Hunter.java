package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Arrow;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Hunter extends GroundEnemy {
    public static final int LOGIC_WIDTH = 2 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 3 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2);

    public static final int VISUAL_WIDTH = 5 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 5 * TILE_SIZE;

    public static final float BASE_SPEED = 1.8f;
    public static final float AGGRO_SPEED = 3f;
    public static final float ATTACK_RANGE = TILE_SIZE * 18;
    public static final float AGGRO_RANGE = TILE_SIZE * 25;
    public static final float X_RANGE = TILE_SIZE * 6;

    public static final float MAX_HP = 150;
    public static final float KNOCK_PERCENT = 1.2f;

    public enum ActionState {
        IDLE(0, 0, 0, 0),
        SHOOTING(40, 10, 30, 150);

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
    private static final float COLLISION_DMG = 15f;

    public enum AnimationState {
        IDLE, WALK, ATTACK, DIE, HURT
    }

    private final Animation[] animations = new Animation[AnimationState.values().length];
    private AnimationState currAnim = AnimationState.IDLE;
    private int deathTimer = 0;
    private boolean isDead = false;

    public Hunter(Handler handler, float x, float y) {
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
    }

    private void updateActionState() {
        if (currAction == ActionState.IDLE) return;

        actionTimer--;

        // Tragem săgeata exact când se termină Windup-ul și începe starea Active
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

        currAction = ActionState.SHOOTING;
        actionTimer = currAction.getDuration();
        attackCooldown = currAction.baseCooldown;

        animations[AnimationState.ATTACK.ordinal()].reset();
    }

    private void executeAttackHitbox() {
        float[] spawnCoords = getCenter();
        float[] targetCoords = handler.getDante().getCenter();

        // Spawn săgeată
        handler.addEntity(new Arrow(handler, spawnCoords[0], spawnCoords[1],
                targetCoords[0], targetCoords[1], this));
    }

    private void updateAnimationLogic() {
        AnimationState prevAnim = currAnim;

        if (knockbackTimer > 0) {
            currAnim = AnimationState.HURT;
        } else if (currAction == ActionState.SHOOTING) {
            currAnim = AnimationState.ATTACK;
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
            g.setColor(new Color(0, 255, 0, 100)); // Verde pentru distanță
            g.drawRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);
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
        animations[AnimationState.DIE.ordinal()].reset();
        deathTimer = 90;
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    @Override
    public void setAnimations() {
        try {
            animations[AnimationState.IDLE.ordinal()] = new Animation(9, VisualAssets.Hunter.get("Idle"));
            animations[AnimationState.WALK.ordinal()] = new Animation(7, VisualAssets.Hunter.get("Walk"));
            animations[AnimationState.HURT.ordinal()] = new Animation(6, VisualAssets.Hunter.get("Hurt"));
            animations[AnimationState.DIE.ordinal()] = new Animation(10, VisualAssets.Hunter.get("Die"));

            animations[AnimationState.ATTACK.ordinal()] = new Animation(ActionState.SHOOTING.windup / 4, VisualAssets.Hunter.get("Attack"));
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(4, ActionState.SHOOTING.active);
            animations[AnimationState.ATTACK.ordinal()].setFrameDuration(5, ActionState.SHOOTING.recovery);
        }
        catch (Exception e) {
            System.out.println("Eroare la setare animatii Hunter: " + e.getMessage());
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if (isDead) return;
        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -10.0f : 10.0f;
                dante.applyKnockback(knockX, -8.0f);
            }
        }
    }
}