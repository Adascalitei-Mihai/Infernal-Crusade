package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tile;

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

    private Rectangle attackBounds = new Rectangle();
    private static final float ATTACK_DMG = 50f;
    private static final float COLLISION_DMG = 20f;

    // ==========================================
    // --- VARIABILE PENTRU ANIMAȚII ---
    // ==========================================
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_WALK = 1;
    public static final int ANIM_ATTACK = 2;
    public static final int ANIM_DIE = 3;
    public static final int ANIM_HURT = 4;

    private final Animation[] animations = new Animation[5];
    private int currAnim = ANIM_IDLE;
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

        animations[ANIM_IDLE] = new Animation(150, Assets.Skeleton.get("Idle"));
        animations[ANIM_WALK] = new Animation(120, Assets.Skeleton.get("Walk"));
        animations[ANIM_ATTACK] = new Animation(100, Assets.Skeleton.get("Attack1"));
        animations[ANIM_HURT] = new Animation(100, Assets.Skeleton.get("Hurt"));
        animations[ANIM_DIE] = new Animation(150, Assets.Skeleton.get("Die"));
    }

    @Override
    public void Update() {
        if (isDead) {
            if (deathTimer > 0) {
                deathTimer--;
                animations[ANIM_DIE].tick();
            }
            else {
                active = false;
            }
            return;
        }

        super.Update();

        if (knockbackTimer > 0 || pauseTimer > 0) {
            updateAnimationState();
            animations[currAnim].tick();
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

        updateAnimationState();
        animations[currAnim].tick();
    }

    private void updateAnimationState() {
        if (knockbackTimer > 0) {
            currAnim = ANIM_HURT;
        }
        else if (isWindingUp || isAttacking) {
            currAnim = ANIM_ATTACK;
        }
        else if (Math.abs(xMove) > 0.1f) {
            currAnim = ANIM_WALK;
        }
        else {
            currAnim = ANIM_IDLE;
        }
    }

    @Override
    protected void attack() {
        if (attackTimer > 0) {
            return;
        }

        attackTimer = ATTACK_COOLDOWN;
        isWindingUp = true;

        // Resetăm animația de atac pentru a rula curat de la cadul 0
        animations[ANIM_ATTACK].reset();
    }

    private void executeAttackHitbox() {
        int hWidth = TILE_SIZE * 4;
        int hHeight = (int) (bounds.height * 0.5f);

        attackBounds.setSize(hWidth, hHeight);

        if (direction == 1) { // 1 înseamnă se uită la dreapta
            attackBounds.x = (int) (x + bounds.x + bounds.width);
        }
        else { // se uită la stânga
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

        // Calculăm poziția de desenare astfel încât imaginea vizuală (care e mai lată)
        // să fie perfect centrată pe hitbox-ul logic pe axa X,
        // și aliniată la baza hitbox-ului pe axa Y.
        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT);

        if (direction != 1) {
            // Inversare pentru mersul la stânga (Flip orizontal)
            g.drawImage(animations[currAnim].getCurrentFrame(),
                    drawX + VISUAL_WIDTH, drawY,
                    -VISUAL_WIDTH, VISUAL_HEIGHT, null);
        } else {
            // Desenare normală pentru dreapta
            g.drawImage(animations[currAnim].getCurrentFrame(),
                    drawX, drawY,
                    VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }

        // =====================================
        // --- HITBOX-URI (Pentru Debug) ---
        // =====================================
        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 0, 100)); // Roșu transparent (Hitbox Bază)
            g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);

            if (isAttacking || isWindingUp) {
                g.setColor(new Color(255, 255, 255, 150)); // Alb transparent (Hitbox Atac)
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
        currAnim = ANIM_DIE;
        deathTimer = 90; // Ex: 90 cadre până dispare corpul de pe hartă
        xMove = 0;
    }
}