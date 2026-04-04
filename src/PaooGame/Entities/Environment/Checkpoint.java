package PaooGame.Entities.Environment;

import PaooGame.Entities.Entity;
import PaooGame.Game;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;

import java.awt.*;

public class Checkpoint extends Entity {
    private Animation wind;
    private boolean activated = false;

    public Checkpoint(Game game, float x, float y) {
        super(game, x, y, 32, 32);
        wind = new Animation(150, Assets.chkp);

        this.bounds = new Rectangle(7, -370, 18, 400);
    }

    @Override
    public void Update() {
        if (activated) {
            wind.tick();
        }
        else {
            if (game.getPlayer().getCollisionBounds(0, 0).intersects(this.getCollisionBounds(0, 0))) {
                activated = true;
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - game.getGameCamera().getxOffset());
        int screenY = (int) (y - game.getGameCamera().getyOffset());

        if (activated) {
            g.drawImage(wind.getCurrentFrame(), screenX, screenY, width, height, null);
        }
        else {
            g.drawImage(Assets.chkp[0], screenX, screenY, width, height, null);
        }
    }

    @Override
    public void onInteraction(Entity e) {}
}