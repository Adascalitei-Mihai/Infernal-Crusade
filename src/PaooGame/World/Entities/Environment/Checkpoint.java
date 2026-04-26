package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;

import java.awt.*;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class Checkpoint extends Entity {
    private final Animation flutter;
    private final int id;
    private boolean activated = false;

    public Checkpoint(Handler handler, float x, float y, int id) {
        super(handler, x, y, 4 * TILE_SIZE, 4 * TILE_SIZE);
        flutter = new Animation(150, Assets.chkp);
        this.id = id;

        this.bounds = new Rectangle(0, -2000, 4 * TILE_SIZE, 4000);
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

        if(handler.getDebugMode()) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect((int) (x - handler.getGameCamera().getxOffset()),
                    (int) (y - handler.getGameCamera().getyOffset()), width, height);
        }
        else {
            if (activated) {
                g.drawImage(flutter.getCurrentFrame(), screenX, screenY, width, height, null);
            }
            else {
                g.drawImage(Assets.chkp[0], screenX, screenY, width, height, null);
            }
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if(e instanceof Dante && !activated) {
            activated = true;
            if(id > handler.getCurrChkp()) {
                handler.updateCurrChkp(id);
            }
        }
    }
}