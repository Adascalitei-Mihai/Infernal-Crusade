package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tiles.Tile;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public abstract class Creature extends Entity {
    protected float hp;
    protected float xMove, yMove;

    public Creature(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height);
        this.hp = hp;
    }
    public void move() {
        if(!checkEntityCollisions(xMove, 0f))
            moveX();
        if(!checkEntityCollisions(0f, yMove))
            moveY();
    }
    public void moveX() {
        if (xMove > 0) {
            int tx = (int) (x + xMove + bounds.x + bounds.width) / TILE_SIZE;
            if (!collisionWithTile(tx, (int) (y + bounds.y) / TILE_SIZE) &&
                    !collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / TILE_SIZE)) {
                x += xMove;
            }
            else {
                x = tx * TILE_SIZE - bounds.x - bounds.width - 1;
            }
        }
        else if (xMove < 0) {
            int tx = (int) (x + xMove + bounds.x) / TILE_SIZE;
            if (!collisionWithTile(tx, (int) (y + bounds.y) / TILE_SIZE) &&
                    !collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / TILE_SIZE)) {
                x += xMove;
            }
            else {
                x = tx * TILE_SIZE + TILE_SIZE - bounds.x;
            }
        }
    }
    public void moveY() {
        if (yMove < 0) {
            int ty = (int) (y + yMove + bounds.y) / TILE_SIZE;
            if (!collisionWithTile((int) (x + bounds.x) / TILE_SIZE, ty) &&
                    !collisionWithTile((int) (x + bounds.x + bounds.width) / TILE_SIZE, ty)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE + TILE_SIZE - bounds.y;
            }
        }
        else if (yMove > 0) {
            int ty = (int) (y + yMove + bounds.y + bounds.height) / TILE_SIZE;
            if (!collisionWithTile((int) (x + bounds.x) / TILE_SIZE, ty) &&
                    !collisionWithTile((int) (x + bounds.x + bounds.width) / TILE_SIZE, ty)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE - bounds.y - bounds.height - 1;
            }
        }
    }

    protected boolean collisionWithTile(int x, int y) {
        Tile t = handler.getLevel().getTile(x, y);

        if (t == null) {
            return true;
        }

        return t.isSolid();
    }

    public abstract void hurt(float dmg);
    public abstract void die();

    public void setHp(float hp) { this.hp = hp; }
    public float getHp() { return hp; }
    public float getxMove() { return xMove; }
    public void setxMove(float xMove) { this.xMove = xMove; }
    public float getyMove() { return yMove; }
    public void setyMove(float yMove) { this.yMove = yMove; }
}
