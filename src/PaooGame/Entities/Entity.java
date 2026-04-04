package PaooGame.Entities;

import PaooGame.Game;

import java.awt.*;

public abstract class Entity {
    protected Game game;
    protected float x, y;
    protected int width, height;
    protected Rectangle bounds;
    protected boolean active = true;

    public Entity(Game game, float x, float y, int width, int height) {
        this.game = game;
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
}