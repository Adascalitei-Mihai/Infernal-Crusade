package PaooGame.World.Entities.Environment.Triggers;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

import java.awt.*;

public abstract class Trigger extends Entity {
    protected boolean triggered = false;

    public Trigger(Handler handler, float x, float y, int width, int height) {
        super(handler, x, y, width, height);
    }

    abstract public void Update();

    @Override
    public void Draw(Graphics g) {
        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect((int) (x - handler.getGameCamera().getxOffset()),
                    (int) (y - handler.getGameCamera().getyOffset()), width, height);
        }
    }

    abstract public void onInteraction(Entity e);

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
