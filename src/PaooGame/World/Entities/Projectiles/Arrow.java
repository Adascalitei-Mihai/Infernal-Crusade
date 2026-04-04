package PaooGame.World.Entities.Projectiles;

import PaooGame.Game.Game;
import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import java.awt.*;

public class Arrow extends Projectile {

    public Arrow(Handler handler, float x, float y, float tx, float ty, Entity owner) {
        super(handler, x, y, 32, 8, tx, ty, 9.0f, owner);

        this.damage = 15;

        this.bounds.x = 4;
        this.bounds.y = 2;
        this.bounds.width = 24;
        this.bounds.height = 4;
    }

    @Override
    public void Draw(Graphics g) {}
}