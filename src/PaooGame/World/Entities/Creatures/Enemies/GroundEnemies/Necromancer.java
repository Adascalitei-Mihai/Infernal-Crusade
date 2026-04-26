package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Arrow;
import PaooGame.World.Entities.Spells.SummonCircle;
import PaooGame.World.Tile;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Necromancer extends GroundEnemy {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;
    public static final float MAX_HP = 300;
    public static final float KNOCK_PERCENT = 0.7f;

    private static final int ATTACK_COOLDOWN = 240;
    private static final int ATTACK_DURATION = 30;
    private int attackTimer;
    private int attackActiveTimer;

    private static final int LIFESTEAL_COOLDOWN = 240;
    private int lifestealTimer;

    private static final float COLLISION_DMG = 20f;

    public Necromancer(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_HP, KNOCK_PERCENT);

        bounds.x = 4;
        bounds.width = DEFAULT_WIDTH - 8;
        bounds.y = 1;
        bounds.height = DEFAULT_HEIGHT - 2;

        this.baseSpeed = 2.0f;
        this.aggroSpeed = 2.5f;
        this.attackRange = TILE_SIZE * 30;
        this.aggroRange = TILE_SIZE * 36;
        this.patrolRange = new float[]{x - Tile.TILE_SIZE * 3, x + Tile.TILE_SIZE * 3};
    }

    @Override
    public void Update() {
        super.Update();
        if (knockbackTimer > 0 || pauseTimer > 0) {
            return;
        }

        if (lifestealTimer > 0) {
            lifestealTimer--;
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

        move();
        if (currState == EnemyState.ATTACKING) {
            attack();
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
        float[] danteCenter = handler.getDante().getCenter();

        float spellX = danteCenter[0] - SummonCircle.DEFAULT_WIDTH / 2f;
        float spellY = danteCenter[1] - SummonCircle.DEFAULT_HEIGHT / 2f;

        handler.getLevel().getEntityManager().addEntity(
                new SummonCircle(handler, spellX, spellY, this)
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

    public void heal(float hp) {
        if (lifestealTimer == 0) {
            this.hp += hp;
            lifestealTimer = LIFESTEAL_COOLDOWN;
        }
    }
}