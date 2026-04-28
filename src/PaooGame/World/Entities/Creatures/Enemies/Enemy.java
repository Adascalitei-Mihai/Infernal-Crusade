package PaooGame.World.Entities.Creatures.Enemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

import static PaooGame.World.Tile.TILE_SIZE;

public abstract class Enemy extends Creature {
    protected enum EnemyState { PATROLLING, CHASING, ATTACKING }
    protected EnemyState currState = EnemyState.PATROLLING;

    protected static final int PAUSE_DURATION = 90;
    protected int pauseTimer;

    protected float aggroRange;
    protected float attackRange;
    protected float[] patrolRange;

    protected int direction = 1; // -1 stanga, 1 dreapta
    protected float baseSpeed, aggroSpeed;

    protected boolean isWindingUp;
    protected boolean isAttacking;

    protected float knockPercent;
    protected float gravity = 0.5f;

    public Enemy(Handler handler, float x, float y, int width, int height, float hp, float knockPercent) {
        super(handler, x, y, width, height, hp);
        this.knockPercent = knockPercent;
    }

    protected float getDistanceToPlayer() {
        Entity player = handler.getDante();
        if(player == null) return Float.MAX_VALUE;

        float dx = (x + bounds.x) - (player.getX() + player.getBounds().x);
        float dy = (y + bounds.y) - (player.getY() + player.getBounds().y);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    protected boolean isPlayerToTheRight() {
        Entity player = handler.getDante();
        if(player == null) return false;

        return (player.getX() + player.getBounds().x) > (this.x + this.bounds.x);
    }

    private void checkEnvironmentDamage() {
        int tx = (int) (x + bounds.x + bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 16) / TILE_SIZE;


        if (handler.getLevel().isFire(tx, ty)) {
            if (hurt(Dante.FIERY_JUDGEMENT_DMG)) {
                applyKnockback(0f, -5.0f);
            }
        }
    }

    @Override
    public void Update() {
        super.Update();

        checkEnvironmentDamage();
        if (knockbackTimer > 0) {
            isAttacking = false;
            isWindingUp = false;

            xMove *= 0.8f;

            if (gravity == 0f) {
                yMove *= 0.8f;
            }
            else {
                yMove += gravity;
            }

            move();
            return;
        }
        if(pauseTimer > 0) {
            pauseTimer--;

            xMove = 0;

            if (gravity != 0f) {
                yMove *= 0.8f;
            }
            else {
                yMove += gravity;
            }

            isAttacking = false;
            isWindingUp = false;

            move();
            return;
        }

        makeDecision();

        switch (currState) {
            case PATROLLING:
            case CHASING:
                move();
                break;
            case ATTACKING:
                attack();
                break;
        }
    }

    public boolean checkHorizontalCollision() {
        int tx = (int) (x + xMove + (xMove > 0 ? bounds.x + bounds.width : bounds.x)) / TILE_SIZE;
        return collisionWithTile(tx, (int) (y + bounds.y) / TILE_SIZE) ||
                collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / TILE_SIZE);
    }

    @Override
    public boolean hurt(float dmg) {
        if (invulnerabilityTimer > 0) {
            return false;
        }
        hp -= dmg;
        invulnerabilityTimer = INVULN_TIME;

        if (hp <= 0) {
            die();
        }
        return true;
    }

    @Override
    public void die() {
        active = false;
    }

    protected abstract void makeDecision();
    protected abstract void attack();

    public float getKnockPercent() {
        return knockPercent;
    }
}