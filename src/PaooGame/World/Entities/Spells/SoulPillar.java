package PaooGame.World.Entities.Spells;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class SoulPillar extends Spell {
    public final static int DEFAULT_WIDTH = TILE_SIZE * 2;
    public final static int DEFAULT_HEIGHT = TILE_SIZE * 6;
    private final static int SUMMON_TIME = 20;
    private final static int ACTIVE_TIME = 10;

    private final static float DMG = 50f;
    private final static float KNOCK_PERCENT = 0.5f;

    public SoulPillar(Handler handler, float x, float y, Creature owner) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT,
                owner, DMG, KNOCK_PERCENT, SUMMON_TIME, ACTIVE_TIME);
    }

    @Override
    public void Draw(Graphics g) {
        if(isSummoning) {
            g.setColor(Color.GRAY);
        }
        else {
            g.setColor(Color.BLACK);
        }

        g.setColor(Color.BLACK);
        g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                bounds.width, bounds.height);
    }
}
