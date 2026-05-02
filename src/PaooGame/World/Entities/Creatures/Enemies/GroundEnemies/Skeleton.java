package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Skeleton extends GroundEnemy {
    public static final int LOGIC_WIDTH = 2 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 4 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(
            4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2
    );

    public static final int VISUAL_WIDTH = 5 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 4 * TILE_SIZE;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 2.5f;
    public static final float ATTACK_RANGE = TILE_SIZE * 5;
    public static final float AGGRO_RANGE = TILE_SIZE * 18;
    public static final float X_RANGE = TILE_SIZE * 5;

    public static final float MAX_HP = 100;
    public static final float KNOCK_PERCENT = 1f;

    private static final int ATTACK_COOLDOWN = 60;
    private static final int ATTACK_DURATION = 15;
    private int attackTimer = 0;
    private int attackActiveTimer = 0;

    private final Rectangle attackBounds = new Rectangle();
    private static final float ATTACK_DMG = 50f;
    private static final float COLLISION_DMG = 20f;

    public enum AnimationState {
        IDLE, WALK, ATTACK, DIE, HURT
    }

    private final Animation[] animations = new Animation[AnimationState.values().length];
    private AnimationState currAnim = AnimationState.IDLE;
    private int deathTimer = 0;
    private boolean isDead = false;

    public Skeleton(Handler handler, float x, float y) {
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
            if (deathTimer > 0) {
                deathTimer--;
                animations[AnimationState.DIE.ordinal()].Update();
            }
            else {
                active = false;
            }
            return;
        }

        super.Update();

        if (knockbackTimer > 0 || pauseTimer > 0) {
            updateAndPlayAnimation();
            return;
        }

        if (attackTimer > 0) {
            attackTimer--;

            if (attackTimer == ATTACK_DURATION) {
                isWindingUp = false;
                isAttacking = true;
                attackActiveTimer = ATTACK_DURATION;

                executeAttackHitbox();
            }
        }

        if (isAttacking) {
            attackActiveTimer--;
            if (attackActiveTimer <= 0) {
                isAttacking = false;
                pauseTimer = PAUSE_DURATION;
            }
        }

        updateAndPlayAnimation();
    }

    private void updateAndPlayAnimation() {
        AnimationState prevAnim = currAnim;
        updateAnimationState();

        if (currAnim != prevAnim) {
            animations[currAnim.ordinal()].reset();
        }

        animations[currAnim.ordinal()].Update();
    }

    private void updateAnimationState() {
        if (knockbackTimer > 0) {
            currAnim = AnimationState.HURT;
        }
        else if (isWindingUp || isAttacking) {
            currAnim = AnimationState.ATTACK;
        }
        else if (Math.abs(xMove) > 0.1f) {
            currAnim = AnimationState.WALK;
        }
        else {
            currAnim = AnimationState.IDLE;
        }
    }

    @Override
    protected void attack() {
        if (attackTimer > 0) {
            return;
        }

        attackTimer = ATTACK_COOLDOWN;
        isWindingUp = true;

        animations[AnimationState.ATTACK.ordinal()].reset();
    }

    private void executeAttackHitbox() {
        int hWidth = TILE_SIZE * 4;
        int hHeight = (int) (bounds.height * 0.5f);

        attackBounds.setSize(hWidth, hHeight);

        if (direction == 1) {
            attackBounds.x = (int) (x + bounds.x + bounds.width);
        }
        else {
            attackBounds.x = (int) (x + bounds.x - hWidth);
        }
        attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);

        Dante dante = handler.getDante();
        if (attackBounds.intersects(dante.getCollisionBounds(0f, 0f))) {
            if (dante.hurt(ATTACK_DMG)) {
                float knockX = (direction == 1) ? 15.0f : -15.0f;
                dante.applyKnockback(knockX, -5.0f);

                pauseTimer = PAUSE_DURATION;
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT);

        if (direction != 1) {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX + VISUAL_WIDTH, drawY,
                    -VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }
        else {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX, drawY,
                    VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);

            if (isAttacking || isWindingUp) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                        (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                        attackBounds.width, attackBounds.height);

                g.setColor(Color.BLACK);
                g.drawRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                        (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                        attackBounds.width, attackBounds.height);
            }
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if (isDead) {
            return;
        }

        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;

                dante.applyKnockback(knockX, -10.0f);
                pauseTimer = PAUSE_DURATION;
            }
        }
    }

    @Override
    public boolean hurt(float dmg) {
        if (isDead) return false;
        return super.hurt(dmg);
    }

    @Override
    public void die() {
        if (isDead) return;

        isDead = true;
        currAnim = AnimationState.DIE;

        animations[AnimationState.DIE.ordinal()].reset();

        deathTimer = 90;
        xMove = 0;

        bounds.width = 0;
        bounds.height = 0;
    }

    @Override
    public void setAnimations() {
        animations[AnimationState.IDLE.ordinal()] = new Animation(9, VisualAssets.Skeleton.get("Idle"));
        animations[AnimationState.WALK.ordinal()] = new Animation(7, VisualAssets.Skeleton.get("Walk"));
        animations[AnimationState.ATTACK.ordinal()] = new Animation(6, VisualAssets.Skeleton.get("Attack1"));
        animations[AnimationState.HURT.ordinal()] = new Animation(6, VisualAssets.Skeleton.get("Hurt"));

        animations[AnimationState.DIE.ordinal()] = new Animation(9, VisualAssets.Skeleton.get("Die"));
        animations[AnimationState.DIE.ordinal()].setFrameDuration(VisualAssets.Skeleton.get("Die").length - 1, -1);
    }
}