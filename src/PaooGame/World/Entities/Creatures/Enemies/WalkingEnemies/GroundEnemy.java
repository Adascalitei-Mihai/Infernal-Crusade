package PaooGame.World.Entities.Creatures.Enemies.WalkingEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public abstract class GroundEnemy extends Enemy {
    protected int direction = 1; // 1 dreapta, -1 stanga
    protected float speed = 1.0f;

    public GroundEnemy(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height, hp);
    }

    @Override
    protected void makeDecision() {
        yMove += 0.5f;
        int nextX = (int) (x + bounds.x + (direction == 1 ? bounds.width + 2 : -2)) / TILE_SIZE;
        int belowY = (int) (y + bounds.y + bounds.height + 2) / TILE_SIZE;
        if (!collisionWithTile(nextX, belowY) || checkHorizontalCollision()) {
            direction *= -1;
        }

        xMove = direction * speed;
    }

    private boolean checkHorizontalCollision() {
        int tx = (int) (x + xMove + (xMove > 0 ? bounds.x + bounds.width : bounds.x)) / TILE_SIZE;
        return collisionWithTile(tx, (int) (y + bounds.y) / TILE_SIZE) ||
                collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / TILE_SIZE);
    }
}
