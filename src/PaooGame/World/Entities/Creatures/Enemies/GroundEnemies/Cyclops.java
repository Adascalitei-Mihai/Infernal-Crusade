package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tile;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Cyclops extends GroundEnemy {
    public static final int DEFAULT_WIDTH = 3 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 6 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(
            -2, 1, DEFAULT_WIDTH + 2, DEFAULT_HEIGHT - 2
    );

    public static final float BASE_SPEED = 1f;
    public static final float AGGRO_SPEED = 2f;
    public static final float ATTACK_RANGE = TILE_SIZE * 7;
    public static final float AGGRO_RANGE = TILE_SIZE * 20;
    public static final float X_RANGE = TILE_SIZE * 6;

    public static final float MAX_HP = 500f;
    public static final float KNOCK_PERCENT = 0.2f;

    private static final int ATTACK_COOLDOWN = 120;
    private static final int ATTACK_DURATION = 30;
    private int attackTimer = 0;
    private int attackActiveTimer = 0;

    private Rectangle attackBounds = new Rectangle();
    private static final float ATTACK_DMG = 100f;
    private static final float COLLISION_DMG = 50f;

    public Cyclops(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = BOUNDS;

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.attackRange = ATTACK_RANGE;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
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
            }
        }

        if (isWindingUp || isAttacking) {
            xMove = 0;
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
        int hWidth = TILE_SIZE * 5;
        int hHeight = (int) (bounds.height * 0.8f);

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
                float knockX = (direction == 1) ? 120.0f : -120.0f;
                dante.applyKnockback(knockX, -20.0f);

                pauseTimer = PAUSE_DURATION;
            }
        }
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

        if (isAttacking) {
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

    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;

                dante.applyKnockback(knockX, -20.0f);
                pauseTimer = PAUSE_DURATION;
            }
        }
    }
}