package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tile;

import static PaooGame.World.Tile.TILE_SIZE;

public abstract class Creature extends Entity {
    protected static final int INVULN_TIME = 45;
    protected int invulnerabilityTimer = 0;
    protected static final int KNOCK_TIME = 15;
    protected int knockbackTimer = 0;

    protected float hp;
    protected float xMove, yMove;

    public Creature(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height);
        this.hp = hp;
    }

    @Override
    public void Update() {
        if (invulnerabilityTimer > 0) {
            --invulnerabilityTimer;
        }
        if (knockbackTimer > 0) {
            --knockbackTimer;
        }
    }

    public void move() {
        if (!checkEntityCollisions(xMove, 0f)) {
            moveX();
        }
        else {
            xMove = 0;
        }

        if (!checkEntityCollisions(0f, yMove)) {
            moveY();
        }
        else {
            if (yMove > 0) {
                if (this instanceof Dante dante) {
                    dante.setOnGround(true);
                }
            }
            yMove = 0;
        }
    }

    public boolean checkEntityCollisions(float xOffset, float yOffset) {
        for (Entity e : handler.getEntities()) {
            if (e.equals(this)) continue;

            if (e.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset))) {
                e.onInteraction(this);

                if (e.isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void moveX() {
        if (xMove > 0) {
            int tx = (int) (x + xMove + bounds.x + bounds.width) / TILE_SIZE;
            if (!isSolidOnEdge(tx, true)) {
                x += xMove;
            }
            else {
                x = tx * TILE_SIZE - bounds.x - bounds.width - 1;
            }
        }
        else if (xMove < 0) {
            int tx = (int) (x + xMove + bounds.x) / TILE_SIZE;
            if (!isSolidOnEdge(tx, true)) {
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
            if (!isSolidOnEdge(ty, false)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE + TILE_SIZE - bounds.y;
                yMove = 0;
            }
        }
        else if (yMove > 0) {
            int ty = (int) (y + yMove + bounds.y + bounds.height) / TILE_SIZE;
            if (!isSolidOnEdge(ty, false)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE - bounds.y - bounds.height - 1;
                yMove = 0;
                if(this instanceof Dante dante) {
                    dante.setOnGround(true);
                }
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

    protected boolean isSolidOnEdge(int tileCoord, boolean isXAxis) {
        if (isXAxis) {
            int tyStart = (int) (y + bounds.y) / TILE_SIZE;
            int tyEnd = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            for (int ty = tyStart; ty <= tyEnd; ty++) {
                if (collisionWithTile(tileCoord, ty)) return true;
            }
        }
        else {
            int txStart = (int) (x + bounds.x) / TILE_SIZE;
            int txEnd = (int) (x + bounds.x + bounds.width) / TILE_SIZE;

            for (int tx = txStart; tx <= txEnd; tx++) {
                if (collisionWithTile(tx, tileCoord)) return true;
            }
        }
        return false;
    }

    public abstract boolean hurt(float dmg);
    public abstract void die();
    public abstract void setAnimations();

    public void applyKnockback(float forceX, float forceY) {
        xMove = forceX;
        yMove = forceY;
        knockbackTimer = KNOCK_TIME;
    }

    public void setHp(float hp) { this.hp = hp; }
    public float getHp() { return hp; }
    public float getxMove() { return xMove; }
    public void setxMove(float xMove) { this.xMove = xMove; }
    public float getyMove() { return yMove; }
    public void setyMove(float yMove) { this.yMove = yMove; }
}