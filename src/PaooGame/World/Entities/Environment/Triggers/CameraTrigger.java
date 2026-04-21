package PaooGame.World.Entities.Environment.Triggers;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

public class CameraTrigger extends Trigger {
    private float camMinX, camMinY, camMaxX, camMaxY;
    private boolean isLockTrigger; // true - blocheaza, false - deblocheaza

    public CameraTrigger(Handler handler, float x, float y, int width, int height,
                         float minX, float minY, float maxX, float maxY, boolean isLockTrigger) {
        super(handler, x, y, width, height);
        this.camMinX = minX;
        this.camMinY = minY;
        this.camMaxX = maxX;
        this.camMaxY = maxY;
        this.isLockTrigger = isLockTrigger;
    }

    @Override
    public void Update() {}

    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Dante && !triggered) {
            if (isLockTrigger) {
                handler.getGameCamera().setCustomBounds(camMinX, camMinY, (camMaxX - camMinX), (camMaxY - camMinY));
            }
            else {
                handler.getGameCamera().resetToLevelBounds();
            }
            triggered = true;
        }
    }
}