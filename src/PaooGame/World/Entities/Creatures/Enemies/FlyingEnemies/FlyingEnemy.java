package PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;

import static PaooGame.World.Tile.TILE_SIZE;

public abstract class FlyingEnemy extends Enemy {
    protected float hoverAngle;
    protected float startY;
    protected float yRange;
    protected float hoverSpeed;

    public FlyingEnemy(Handler handler, float x, float y, int width, int height, float hp, float weight,
                       float yRange, float hoverSpeed) {
        super(handler, x, y, width, height, hp, weight);
        this.startY = y;
        this.yRange = yRange;
        this.hoverSpeed = hoverSpeed;
    }

    @Override
    protected void makeDecision() {
        hoverAngle += hoverSpeed;
        if (hoverAngle > Math.PI * 2) {
            hoverAngle -= (float) (Math.PI * 2);
        }

        float targetY = startY + (float) Math.sin(hoverAngle) * yRange;
        float diffY = targetY - this.y;
        float maxRecoverySpeed = baseSpeed * 0.8f;

        if (diffY > maxRecoverySpeed) {
            yMove = maxRecoverySpeed;
        }
        else if (diffY < -maxRecoverySpeed) {
            yMove = -maxRecoverySpeed;
        }
        else {
            yMove = diffY;
        }

        if (isWindingUp || isAttacking) {
            currState = EnemyState.ATTACKING;
            xMove = 0;
            return;
        }

        float distance = getDistanceToPlayer();
        float speed = baseSpeed;

        if (distance <= attackRange) {
            currState = EnemyState.ATTACKING;
            speed = 0;
        }
        else if (distance <= aggroRange) {
            currState = EnemyState.CHASING;
            speed = aggroSpeed;
        }
        else {
            currState = EnemyState.PATROLLING;
        }

        if(currState != EnemyState.PATROLLING) {
            direction = isPlayerToTheRight() ? 1 : -1;
        }

        xMove = direction * speed;

        if (currState == EnemyState.PATROLLING) {
            if (x <= patrolRange[0]) {
                direction = 1;
                xMove = direction * speed;
            }
            else if (x >= patrolRange[1]) {
                direction = -1;
                xMove = direction * speed;
            }
            else if (checkHorizontalCollision()) {
                direction *= -1;
                xMove = direction * speed;
            }
        }
        else if (checkHorizontalCollision()) {
            xMove = 0;
        }
    }
}