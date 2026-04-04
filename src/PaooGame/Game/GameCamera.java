package PaooGame.Game;

import PaooGame.World.Entities.Entity;
import PaooGame.Handler;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class GameCamera {

    private float xOffset, yOffset;
    private Handler handler;

    private float horizontalPadding = 150f;

    public GameCamera(Handler handler, float xOffset, float yOffset) {
        this.handler = handler;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void center(Entity e) {
        yOffset = e.getY() - (float) handler.getHeight() / 2 + (float) e.getHeight() / 2;

        float targetX = e.getX() - (float) handler.getWidth() / 2 + (float) e.getWidth() / 2 + horizontalPadding;

        if (targetX > xOffset) {
            xOffset = targetX;
        }

        else if (e.getX() < xOffset + 100) {
            xOffset = e.getX() - 100;
        }

        checkBlankSpace();
    }
    public void centerOnPosition(float x, float y) {
        xOffset = x - (float) handler.getWidth() / 2;
        yOffset = y - (float) handler.getHeight() / 2;
        checkBlankSpace();
    }

    public void checkBlankSpace() {
        if(xOffset < 0) xOffset = 0;
        else if(xOffset > handler.getWorld().getWidth() * TILE_SIZE - handler.getWidth())
            xOffset = handler.getWorld().getWidth() * TILE_SIZE - handler.getWidth();

        if(yOffset < 0) yOffset = 0;
        else if(yOffset > handler.getWorld().getHeight() * TILE_SIZE - handler.getHeight())
            yOffset = handler.getWorld().getHeight() * TILE_SIZE - handler.getHeight();
    }

    public float getxOffset() { return xOffset; }
    public float getyOffset() { return yOffset; }
}