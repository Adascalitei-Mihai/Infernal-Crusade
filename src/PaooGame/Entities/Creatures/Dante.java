package PaooGame.Entities.Creatures;

import PaooGame.Entities.Entity;
import PaooGame.Game;

import java.awt.*;

public class Dante extends Creature {
    public static final int DEFAULT_WIDTH = 32;
    public static final int DEFAULT_HEIGHT = 48;

    public Dante(Game game, float x, float y, int width, int height) {
        super(game, x, y, width, height);
    }

    @Override
    public void Update() {}

    @Override
    public void Draw(Graphics g) {}

    @Override
    public void onInteraction(Entity e) {}
}
