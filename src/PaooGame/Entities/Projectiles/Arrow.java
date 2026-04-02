package PaooGame.Entities.Projectiles;

import PaooGame.Graphics.Assets;
import PaooGame.Game;
import PaooGame.Entities.Entity;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Arrow extends Projectile {

    public Arrow(Game game, float x, float y, float tx, float ty, Entity owner) {
        super(game, x, y, 32, 8, tx, ty, 9.0f, owner);

        this.damage = 15;

        this.bounds.x = 4;
        this.bounds.y = 2;
        this.bounds.width = 24;
        this.bounds.height = 4;
    }

    @Override
    public void Draw(Graphics g) {}
}