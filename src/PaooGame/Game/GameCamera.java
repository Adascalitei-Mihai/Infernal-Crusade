package PaooGame.Game;

import PaooGame.World.Entities.Entity;
import PaooGame.Handler;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class GameCamera {

    private float xOffset, yOffset;
    private Handler handler;

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
            // Mergem la STÂNGA: Vrem ca jucătorul să fie la 50% din ecran (centru)
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
        if(xOffset < 0) xOffset = 0;
        else if(xOffset > handler.getLevel().getWidth() * TILE_SIZE - handler.getWidth())
            xOffset = handler.getLevel().getWidth() * TILE_SIZE - handler.getWidth();

        if(yOffset < 0) yOffset = 0;
        else if(yOffset > handler.getLevel().getHeight() * TILE_SIZE - handler.getHeight())
            yOffset = handler.getLevel().getHeight() * TILE_SIZE - handler.getHeight();
    }

    public float getxOffset() { return xOffset; }
    public float getyOffset() { return yOffset; }
}