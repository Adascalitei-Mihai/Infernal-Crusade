package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

import java.awt.*;

public class CameraTrigger extends Entity {
    private final int centerX, centerY;

    public CameraTrigger(Handler handler, float x, float y, int centerX, int centerY) {
        super(handler, x, y, 0, 0);
        this.centerX = centerX;
        this.centerY = centerY;

        this.bounds = new Rectangle(7, -370, 18, 400);
    }

    public void Update() {}
    public void Draw(Graphics g) {}
    public void onInteraction(Entity e) {
        if(e instanceof Dante) {
            handler.getGameCamera().centerOnPosition(centerX, centerY);
        }
    }
}
