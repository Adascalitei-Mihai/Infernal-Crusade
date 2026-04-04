package PaooGame.Entities.Creatures;

import PaooGame.Entities.Entity;
import PaooGame.Game;

import java.awt.*;

public abstract class Creature extends Entity {
    public Creature(Game game, float x, float y, int width, int height) {
        super(game, x, y, width, height);
    }
}
