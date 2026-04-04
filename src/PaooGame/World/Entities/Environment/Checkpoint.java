package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;

import java.awt.*;

public class Checkpoint extends Entity {
    private final Animation flutter;
    private final int id;
    private boolean activated = false;

    public Checkpoint(Handler handler, float x, float y, int id) {
        super(handler, x, y, 32, 32);
        flutter = new Animation(150, Assets.chkp);
        this.id = id;

        this.bounds = new Rectangle(7, -370, 18, 400);
    }

    @Override
    public void Update() {
        if (activated) {
            flutter.tick();
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getxOffset());
        int screenY = (int) (y - handler.getyOffset());

        if (activated) {
            g.drawImage(flutter.getCurrentFrame(), screenX, screenY, width, height, null);
        }
        else {
            g.drawImage(Assets.chkp[0], screenX, screenY, width, height, null);
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if(e instanceof Dante) {
            activated = true;
        }
    }
}