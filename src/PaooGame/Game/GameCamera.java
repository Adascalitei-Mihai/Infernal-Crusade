package PaooGame.Game;

import PaooGame.World.Entities.Entity;
import PaooGame.Handler;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class GameCamera {

    private float xOffset, yOffset;
    private Handler handler;

    private boolean useLevelBounds = true;

    private float minX, maxX, minY, maxY;

    private float previousEntityX;
    private float currentScreenTargetX = 0.3f;

    public GameCamera(Handler handler, float xOffset, float yOffset) {
        this.handler = handler;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.previousEntityX = 0;
    }

    public void center(Entity e) {
        float targetY = e.getY() - (float) handler.getHeight() / 2 + (float) e.getHeight() / 2;

        if (e.getX() > previousEntityX) {
            currentScreenTargetX = 0.3f;
        } else if (e.getX() < previousEntityX) {

            currentScreenTargetX = 0.5f;
        }

        previousEntityX = e.getX();

        float idealXOffset = e.getX() - ((float) handler.getWidth() * currentScreenTargetX);

        xOffset += (idealXOffset - xOffset) * 0.05f;

        yOffset = targetY;

        checkBlankSpace();

        xOffset = Math.round(xOffset);
        yOffset = Math.round(yOffset);
    }

    public void centerOnPosition(float x, float y) {
        xOffset = x - (float) handler.getWidth() / 2;
        yOffset = y - (float) handler.getHeight() / 2;
        checkBlankSpace();
    }

    public void checkBlankSpace() {
        float currentLimitMinX = 0;
        float currentLimitMinY = 0;
        float currentLimitMaxX = handler.getLevel().getWidth() * TILE_SIZE - handler.getWidth();
        float currentLimitMaxY = handler.getLevel().getHeight() * TILE_SIZE - handler.getHeight();

        if (!useLevelBounds) {
            currentLimitMinX = minX;
            currentLimitMinY = minY;
            currentLimitMaxX = maxX - handler.getWidth();
            currentLimitMaxY = maxY - handler.getHeight();
        }

        if(xOffset < currentLimitMinX) xOffset = currentLimitMinX;
        else if(xOffset > currentLimitMaxX) xOffset = currentLimitMaxX;

        if(yOffset < currentLimitMinY) yOffset = currentLimitMinY;
        else if(yOffset > currentLimitMaxY) yOffset = currentLimitMaxY;
    }

    public void setCustomBounds(float x, float y, float width, float height) {
        this.minX = x;
        this.minY = y;
        this.maxX = x + width;
        this.maxY = y + height;
        this.useLevelBounds = false;
    }
    public void resetToLevelBounds() {
        this.useLevelBounds = true;
    }

    public float getxOffset() { return xOffset; }
    public float getyOffset() { return yOffset; }
}