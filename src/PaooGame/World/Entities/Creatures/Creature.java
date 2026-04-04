package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.Game.Game;

public abstract class Creature extends Entity {
    public Creature(Handler handler, float x, float y, int width, int height) {
        super(handler, x, y, width, height);
    }
}
