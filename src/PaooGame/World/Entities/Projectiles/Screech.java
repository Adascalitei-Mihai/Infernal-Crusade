package PaooGame.World.Entities.Projectiles;

import PaooGame.Game.Game;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Entity;
import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Screech extends Projectile {
    private final static int DEFAULT_WIDTH = TILE_SIZE;
    private final static int DEFAULT_HEIGHT = TILE_SIZE;
    private final static float SPEED = 6f;
    private final static float DMG = 40f;
    private final static float KNOCK_PERCENT = 1.5f;

    public Screech(Handler handler, float x, float y, float tx, float ty, Creature owner) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, tx, ty,
                SPEED, owner, DMG, KNOCK_PERCENT);
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                bounds.width, bounds.height);
    }
}