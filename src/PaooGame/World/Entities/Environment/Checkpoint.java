package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.Game.Game;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;

import java.awt.*;

public class Checkpoint extends Entity {
    private Animation wind;
    private boolean activated = false;

    public Checkpoint(Handler handler, float x, float y) {
        super(handler, x, y, 32, 32);
        wind = new Animation(150, Assets.chkp);

        this.bounds = new Rectangle(7, -370, 18, 400);
    }

    @Override
    public void Update() {
        if (activated) {
            wind.tick();
        }
//        else {
//            if (handler.getPlayer().getCollisionBounds(0, 0).intersects(this.getCollisionBounds(0, 0))) {
//                activated = true;
//            }
//        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGame().getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGame().getGameCamera().getyOffset());

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