package PaooGame.Entities.Projectiles;

import PaooGame.Entities.Entity;
import PaooGame.Game;
import java.awt.*;

public abstract class Projectile extends Entity {
    protected final static float PROJECTILE_MAX_RANGE = 500.0f;
    protected float startX, startY;

    protected float vX, vY;
    protected float speed;
    protected Entity owner;
    protected int damage;

    public Projectile(Game game, float x, float y, int width, int height, float targetX, float targetY, float speed, Entity owner) {
        super(game, x, y, width, height);
        this.startX = x;
        this.startY = y;

        this.speed = speed;
        this.owner = owner;

        calculateVelocity(targetX, targetY);
    }

    private void calculateVelocity(float targetX, float targetY) {
        float diffX = targetX - x;
        float diffY = targetY - y;
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        vX = (diffX / distance) * speed;
        vY = (diffY / distance) * speed;
    }

    @Override
    public void Update() {
        x += vX;
        y += vY;

        float distanceTravelled = (float) Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2));

        if (distanceTravelled > PROJECTILE_MAX_RANGE) {
            active = false;
        }

        checkCollisionWithEntities();
    }

    private void checkCollisionWithEntities() {
    }

    @Override
    public abstract void Draw(Graphics g);

    @Override
    public void onInteraction(Entity e) {}
}