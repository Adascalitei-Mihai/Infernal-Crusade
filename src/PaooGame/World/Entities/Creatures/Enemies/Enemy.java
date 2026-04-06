package PaooGame.World.Entities.Creatures.Enemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Entity;

public abstract class Enemy extends Creature {
    public Enemy(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height, hp);
    }

    protected Entity getPlayer() {
        return handler.getDante();
    }

    protected float getDistanceToPlayer() {
        Entity player = getPlayer();
        if(player == null) return Float.MAX_VALUE;

        float dx = (x + bounds.x) - (player.getX() + player.getBounds().x);
        float dy = (y + bounds.y) - (player.getY() + player.getBounds().y);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void Update() {
        makeDecision();
        move();
    }

    protected abstract void makeDecision();
}