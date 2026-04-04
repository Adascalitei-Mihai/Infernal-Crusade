package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.Game.Game;

import java.awt.*;

public class Dante extends Creature {
    public static final int DEFAULT_WIDTH = 32;
    public static final int DEFAULT_HEIGHT = 64;

    public Dante(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void Update() {}

    @Override
    public void Draw(Graphics g) {}

    @Override
    public void onInteraction(Entity e) {}
}
