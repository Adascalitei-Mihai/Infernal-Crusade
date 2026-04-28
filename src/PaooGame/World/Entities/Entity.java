package PaooGame.World.Entities;

import PaooGame.Game.Game;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;

import java.awt.*;

public abstract class Entity {
    protected Handler handler;
    protected float x, y;
    protected int width, height;
    protected Rectangle bounds;
    protected boolean active = true;

    public Entity(Handler handler, float x, float y, int width, int height) {
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.bounds = new Rectangle(0, 0, width, height);
    }

    public abstract void Update();
    public abstract void Draw(Graphics g);
    public abstract void onInteraction(Entity e);

    public Rectangle getCollisionBounds(float xOffset, float yOffset) {
        return new Rectangle((int)(x + bounds.x + xOffset), (int)(y + bounds.y + yOffset), bounds.width, bounds.height);
    }
    public boolean checkEntityCollisions(float xOffset, float yOffset) {
        if (handler.getLevel() == null) return false;

        for (Entity e : handler.getEntities()) {
            if (e.equals(this)) continue;

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

    public float[] getCenter() {
        return new float[] {
                x + bounds.x + (float) bounds.width / 2,
                y + bounds.y + (float) bounds.height / 2
        };
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public Rectangle getBounds() { return bounds; }
    public void setBounds(Rectangle bounds) { this.bounds = bounds; }
}