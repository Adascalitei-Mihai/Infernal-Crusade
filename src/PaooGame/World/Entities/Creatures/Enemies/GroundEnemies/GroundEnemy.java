package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public abstract class GroundEnemy extends Enemy {
    public GroundEnemy(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height, hp);
    }

    @Override
    protected void makeDecision() {
        yMove += 0.5f;
        float distance = getDistanceToPlayer();
        float speed = baseSpeed;

        if (distance <= attackRange) {
            currState = EnemyState.ATTACKING;
            speed = aggroSpeed;
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
        int nextX;
        if (direction == 1) {
            nextX = (int) (x + bounds.x + bounds.width + xMove) / TILE_SIZE;
        }
        else {
            nextX = (int) (x + bounds.x + xMove) / TILE_SIZE;
        }

        int belowY = (int) (y + bounds.y + bounds.height + 1) / TILE_SIZE;
        if (currState == EnemyState.PATROLLING) {
            if (x <= patrolRange[0]) {
                direction = 1;
                xMove = direction * speed;
            }
            else if (x >= patrolRange[1]) {
                direction = -1;
                xMove = direction * speed;
            }
            else if (!collisionWithTile(nextX, belowY) || checkHorizontalCollision()) {
                direction *= -1;
                xMove = direction * speed;
            }
        }
        else if (!collisionWithTile(nextX, belowY) || checkHorizontalCollision()) {
            xMove = 0;
        }
    }

    private boolean checkHorizontalCollision() {
        int tx = (int) (x + xMove + (xMove > 0 ? bounds.x + bounds.width : bounds.x)) / TILE_SIZE;
        return collisionWithTile(tx, (int) (y + bounds.y) / TILE_SIZE) ||
                collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / TILE_SIZE);
    }
}