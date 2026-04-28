package PaooGame.World.Entities.Projectiles;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.Game.Game;
import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public abstract class Projectile extends Entity {
    protected final static float PROJECTILE_MAX_RANGE = 80 * TILE_SIZE;
    protected final static float KNOCKBACK_FORCE = 10f;
    protected final float START_X;
    protected final float START_Y;
    protected float speedX, speedY;

    protected Creature owner;
    protected float damage;
    protected float knockPercent;

    protected boolean justReflected = false;

    public Projectile(Handler handler, float x, float y, int width, int height, float targetX, float targetY,
                      float speed, Creature owner, float damage, float knockPercent) {
        super(handler, x, y, width, height);
        this.START_X = x;
        this.START_Y = y;
        calculateVelocity(targetX, targetY, speed);

        this.owner = owner;
        this.damage = damage;
        this.knockPercent = knockPercent;
    }

    private void calculateVelocity(float targetX, float targetY, float speed) {
        float diffX = targetX - x;
        float diffY = targetY - y;
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        speedX = (diffX / distance) * speed;
        speedY = (diffY / distance) * speed;
    }

    public void reflect(Creature newOwner) {
        float targetX = owner.getX() + owner.getWidth() / 2f;
        float targetY = owner.getY() + owner.getHeight() / 2f;

        this.owner = newOwner;
        this.justReflected = true;

        float currentSpeed = (float) Math.sqrt(speedX * speedX + speedY * speedY);

        float diffX = targetX - this.x;
        float diffY = targetY - this.y;
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance != 0) {
            speedX = (diffX / distance) * currentSpeed * 2;
            speedY = (diffY / distance) * currentSpeed * 2;
        }
        else {
            speedX = -speedX;
            speedY = -speedY;
        }
    }

    @Override
    public void Update() {
        if(checkEntityCollisions(speedX, speedY)) {
            if (justReflected) {
                justReflected = false;
            }
            else {
                active = false;
                return;
            }
        }

        x += speedX;
        y += speedY;

        float distanceTravelled = (float) Math.sqrt(Math.pow(x - START_X, 2) + Math.pow(y - START_Y, 2));
        if (distanceTravelled > PROJECTILE_MAX_RANGE) {
            active = false;
        }
    }

    @Override
    public boolean checkEntityCollisions(float xOffset, float yOffset) {
        if (handler.getLevel() == null) return false;

        for (Entity e : handler.getEntities()) {
            if (e.equals(this) || e.equals(owner)) continue;

            Rectangle otherBounds = e.getCollisionBounds(0f, 0f);
            Rectangle myFutureBounds = getCollisionBounds(xOffset, yOffset);

            if (otherBounds.intersects(myFutureBounds)) {
                e.onInteraction(this);
                this.onInteraction(e);

                if (e instanceof Creature) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInteraction(Entity e) {
        if (e.equals(owner)) {
            return;
        }

        if (e instanceof Creature target) {
            if (e instanceof Dante dante) {
                dante.projectileHit(this);
            }
            else if (target.hurt(damage)) {
                float knockX = (speedX > 0) ? KNOCKBACK_FORCE * knockPercent  : -KNOCKBACK_FORCE * knockPercent;
                target.applyKnockback(knockX, -5f);
            }
        }
    }

    public float getDamage() { return damage; }
    public float getSpeedX() { return speedX; }
    public float getKnockPercent() { return knockPercent; }
    public float getKnockbackForce() { return KNOCKBACK_FORCE; }
}