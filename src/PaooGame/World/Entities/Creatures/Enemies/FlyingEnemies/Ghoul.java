package PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Screech;
import PaooGame.World.Tile;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Ghoul extends FlyingEnemy {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(
            3, 3, DEFAULT_WIDTH - 3, DEFAULT_HEIGHT - 3
    );

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 2.5f;
    public static final float ATTACK_RANGE = TILE_SIZE * 20;
    public static final float AGGRO_RANGE = TILE_SIZE * 26;
    public static final float X_RANGE = TILE_SIZE * 3;

    public static final float MAX_HP = 100;
    public static final float KNOCK_PERCENT = 1f;

    private static final int ATTACK_COOLDOWN = 180;
    private static final int ATTACK_DURATION = 15;
    private int attackTimer = 0;
    private int attackActiveTimer = 0;

    private static final float COLLISION_DMG = 20f;
    protected static final float Y_RANGE = 30f;
    protected static final float HOVER_SPEED = 0.05f;

    public Ghoul(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_HP, KNOCK_PERCENT, Y_RANGE, HOVER_SPEED);
        this.bounds = BOUNDS;

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.attackRange = ATTACK_RANGE;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.gravity = 0f;
    }

    @Override
    public void Update() {
        super.Update();
        if (knockbackTimer > 0 || pauseTimer > 0) {
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

        if (isWindingUp || isAttacking) {
            move();
        }
    }

    @Override
    protected void attack() {
        if (attackTimer > 0) {
            return;
        }

        attackTimer = ATTACK_COOLDOWN;
        isWindingUp = true;
    }

    private void executeAttackHitbox() {
        float[] spawnCoords = getCenter(), targetCoords = handler.getDante().getCenter();

        handler.getLevel().getEntityManager().addEntity(
                new Screech(handler, spawnCoords[0], spawnCoords[1],
                        targetCoords[0], targetCoords[1], this)
        );
    }

    @Override
    public void Draw(Graphics g) {
        if (isWindingUp) {
            g.setColor(Color.MAGENTA);
        }
        else {
            g.setColor(Color.ORANGE);
        }

        g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                bounds.width, bounds.height);
    }

    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;

                dante.applyKnockback(knockX, -15.0f);
                pauseTimer = PAUSE_DURATION;
            }
        }
    }
}