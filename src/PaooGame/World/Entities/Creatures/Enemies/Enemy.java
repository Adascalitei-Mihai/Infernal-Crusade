package PaooGame.World.Entities.Creatures.Enemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Entity;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public abstract class Enemy extends Creature {
    protected enum EnemyState { PATROLLING, CHASING, ATTACKING }
    protected EnemyState currState = EnemyState.PATROLLING;

    protected static final int PAUSE_DURATION = 90;
    protected int pauseTimer;

    protected float aggroRange;
    protected float attackRange;
    protected float[] patrolRange;

    protected boolean isWindingUp;
    protected boolean isAttacking;

    protected int direction = 1; // -1 stanga, 1 dreapta
    protected float baseSpeed, aggroSpeed;

    public Enemy(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height, hp);
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

    @Override
    public void Update() {
        super.Update();

        if (knockbackTimer > 0) {
            isAttacking = false;
            isWindingUp = false;

            xMove *= 0.8f;
            yMove += 0.5f;

            move();
            return;
        }
        if(pauseTimer > 0) {
            pauseTimer--;

            xMove = 0;
            yMove += 0.5f;
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
}