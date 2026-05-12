package PaooGame.States.VisualElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Clasa care realizeaza tranzitiile, ruland o actiune in timp ce ecranul este negru
 */
public class TransitionManager {
    public static final int TRANSITION_TIME = 60;

    private Runnable onBlackScreen;
    private float alpha;

    private boolean fadingOut;
    private boolean fadingIn;
    private int currFrame;

    private String customText;

    public void start(Runnable action) {
        start(action, null);
    }

    public void start(Runnable action, String text) {
        if (!fadingOut && !fadingIn) {
            this.onBlackScreen = action;
            this.customText = text;
            this.fadingOut = true;
            this.currFrame = TRANSITION_TIME;
        }
    }

    public void Update() {
        if (fadingIn) {
            currFrame++;

            alpha = ((float) (TRANSITION_TIME - currFrame) / TRANSITION_TIME) * 255f;

            if (currFrame >= TRANSITION_TIME) {
                alpha = 0;
                fadingIn = false;
                currFrame = 0;
                customText = null;
            }
        } else if (fadingOut) {
            currFrame--;

            alpha = ((float) (TRANSITION_TIME - currFrame) / TRANSITION_TIME) * 255f;

            if (currFrame <= 0) {
                alpha = 255;
                fadingOut = false;

                if (onBlackScreen != null) {
                    onBlackScreen.run();
                    onBlackScreen = null;
                }

                fadingIn = true;
            }
        }
    }

    public void Draw(Graphics g, int screenWidth, int screenHeight) {
        if (alpha > 0) {
            int a = (int) Math.max(0, Math.min(255, alpha));

            g.setColor(new Color(0, 0, 0, a));
            g.fillRect(0, 0, screenWidth, screenHeight);

            if (customText != null) {
                g.setColor(new Color(255, 255, 255, a));

                g.setFont(new Font("Monospaced", Font.BOLD, 70));

                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(customText);
                int textHeight = fm.getHeight();

                int x = (screenWidth - textWidth) / 2;
                int y = (screenHeight - textHeight) / 2 + fm.getAscent();

                g.drawString(customText, x, y);
            }
        }
    }
}