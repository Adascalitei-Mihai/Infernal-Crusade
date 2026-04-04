package PaooGame.Game;

import PaooGame.World.Entities.Entity;
import PaooGame.Handler;

public class GameCamera {

    private float xOffset, yOffset;
    private Handler handler;

    public GameCamera(Handler handler, float xOffset, float yOffset) {
        this.handler = handler;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void checkBlankSpace() {
        if(xOffset < 0) {
            xOffset = 0;
        }

        else if(xOffset > handler.getWorld().getWidth() * 16 - handler.getWidth()) {
            xOffset = handler.getWorld().getWidth() * 16 - handler.getWidth();
        }

        if(yOffset < 0) {
            yOffset = 0;
        }
        else if(yOffset > handler.getWorld().getHeight() * 16 - handler.getHeight()) {
            yOffset = handler.getWorld().getHeight() * 16 - handler.getHeight();
        }
    }

    public void centerOnEntity(Entity e) {
        xOffset = e.getX() - (float) handler.getWidth() / 2 + (float) e.getWidth() / 2;
        yOffset = e.getY() - (float) handler.getHeight() / 2 + (float) e.getHeight() / 2;
        checkBlankSpace();
    }

    public void move(float xAmt, float yAmt) {
        xOffset += xAmt;
        yOffset += yAmt;
        checkBlankSpace();
    }

    public float getxOffset() { return xOffset; }
    public float getyOffset() { return yOffset; }
}