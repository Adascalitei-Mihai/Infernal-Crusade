package PaooGame.Graphics;

import PaooGame.Entities.Entity;
import PaooGame.Handler;

public class GameCamera {

    private float xOffset, yOffset;
    private Handler handler;

    public GameCamera(Handler handler, float xOffset, float yOffset) {
        this.handler = handler;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    // Metoda care verifică să nu ieșim cu camera în afara hărții (Blank Space)
    public void checkBlankSpace() {
        // Limita stângă
        if(xOffset < 0) {
            xOffset = 0;
        }
        // Limita dreaptă (Lățimea hărții în pixeli - Lățimea ferestrei)
        else if(xOffset > handler.getWorld().getWidth() * 16 - handler.getWidth()) {
            xOffset = handler.getWorld().getWidth() * 16 - handler.getWidth();
        }

        // Limita sus
        if(yOffset < 0) {
            yOffset = 0;
        }
        // Limita jos (Înălțimea hărții în pixeli - Înălțimea ferestrei)
        else if(yOffset > handler.getWorld().getHeight() * 16 - handler.getHeight()) {
            yOffset = handler.getWorld().getHeight() * 16 - handler.getHeight();
        }
    }

    // Centrează camera pe o entitate (Player)
    public void centerOnEntity(Entity e) {
        xOffset = e.getX() - handler.getWidth() / 2 + e.getWidth() / 2;
        yOffset = e.getY() - handler.getHeight() / 2 + e.getHeight() / 2;
        checkBlankSpace();
    }

    public void move(float xAmt, float yAmt) {
        xOffset += xAmt;
        yOffset += yAmt;
        checkBlankSpace();
    }

    // Getters și Setters
    public float getxOffset() { return xOffset; }
    public float getyOffset() { return yOffset; }
}