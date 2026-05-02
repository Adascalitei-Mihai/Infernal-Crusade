package PaooGame.States.VisualElements;

import java.awt.Color;
import java.awt.Graphics;

public class TransitionManager {
    private static final int TRANZITION_TIME = 60;

    private float alpha = 0;
    private boolean fadingOut = false;
    private boolean fadingIn = false;
    private int currentFrame = 0;
    private Runnable onBlackScreen;

    public void start(Runnable action) {
        if (!fadingOut && !fadingIn) {
            this.onBlackScreen = action;
            this.fadingOut = true;
            this.currentFrame = TRANZITION_TIME;
        }
    }

    public void Update() {
        if (fadingOut) {
            currentFrame--;

            alpha = ((float) (TRANZITION_TIME - currentFrame) / TRANZITION_TIME) * 255f;

            if (currentFrame <= 0) {
                alpha = 255;
                fadingOut = false;

                if (onBlackScreen != null) {
                    onBlackScreen.run();
                    onBlackScreen = null;
                }

                fadingIn = true;
            }
        } else if (fadingIn) {
            currentFrame++;

            alpha = ((float) (TRANZITION_TIME - currentFrame) / TRANZITION_TIME) * 255f;

            if (currentFrame >= TRANZITION_TIME) {
                alpha = 0;
                fadingIn = false;
                currentFrame = 0;
            }
        }
    }

    public void Draw(Graphics g, int screenWidth, int screenHeight) {
        if (alpha > 0) {
            int a = (int) Math.max(0, Math.min(255, alpha));
            g.setColor(new Color(0, 0, 0, a));
            g.fillRect(0, 0, screenWidth, screenHeight);
        }
    }
}