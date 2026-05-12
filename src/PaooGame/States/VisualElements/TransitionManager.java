package PaooGame.States.VisualElements;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Clasa care realizeaza tranzitiile, ruland o actiune in timp ce ecranul este negru
 */
public class TransitionManager {
    public static final int TRANSITION_TIME = 60;

    /**
     * Campul care este o metoda ce ruleaza cat timp ecranul este negru
     */
    private Runnable onBlackScreen;
    /**
     * Campul care este gradul curent de transparenta
     */
    private float alpha;

    private boolean fadingOut;
    private boolean fadingIn;
    /**
     * Campul care este un temporizator, incrementat pentru {@link #fadingIn} activ si decrementat pentru {@link #fadingOut} activ
     */
    private int currFrame;

    /**
     * Metoda care modifica {@link #onBlackScreen} si incepe logica de {@link #fadingOut}
     * @param action Actiunea de aplicat
     */
    public void start(Runnable action) {
        if (!fadingOut && !fadingIn) {
            this.onBlackScreen = action;
            this.fadingOut = true;
            this.currFrame = TRANSITION_TIME;
        }
    }

    /**
     * Metoda de actualizare, asigura tranzitia intre {@link #fadingIn} - {@link #onBlackScreen} - {@link #fadingOut}
     */
    public void Update() {
         if (fadingIn) {
            currFrame++;

            alpha = ((float) (TRANSITION_TIME - currFrame) / TRANSITION_TIME) * 255f;

            if (currFrame >= TRANSITION_TIME) {
                alpha = 0;
                fadingIn = false;
                currFrame = 0;
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

    /**
     * Metoda de desenare, umple ecranul de dreptunghiul cu transparenta {@link #alpha}
     * @param g Grafica de desenare
     * @param screenWidth Latimea ferestrei
     * @param screenHeight Inaltimea ferestrei
     */
    public void Draw(Graphics g, int screenWidth, int screenHeight) {
        if (alpha > 0) {
            int a = (int) Math.max(0, Math.min(255, alpha));
            g.setColor(new Color(0, 0, 0, a));
            g.fillRect(0, 0, screenWidth, screenHeight);
        }
    }
}