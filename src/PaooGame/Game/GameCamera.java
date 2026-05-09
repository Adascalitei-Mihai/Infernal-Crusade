package PaooGame.Game;

import PaooGame.World.Entities.Entity;
import PaooGame.Handler;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care implementeaza camera dinamica, de tip Look-Ahead
 */
public class GameCamera {
    private final Handler handler;
    private float xOffset, yOffset;

    /**
     * Campul care indica daca camera este blocata
     */
    private boolean useLevelBounds = true;
    /**
     * Campul care indica marginile camerei (daca este blocata)
     */
    private float minX, maxX, minY, maxY;

    /**
     * Campul care este pozitia antecedenta a entitatii urmarite
     */
    private float previousEntityX;

    /**
     * Campul care este pozitia relativa a entitatii pe orizontala fata de ecran, {@code 0.35f} pentru deplasare la dreapta si {@code 0.65f} pentru deplasare la stanga
     */
    private final float SCREEN_TARGET_X = 0.35f;
    private float currScreenTargetX = SCREEN_TARGET_X;

    private final float SPEED = 0.08f;

    /**
     * Constructor care initializeaza pozitia relativa a entitatii urmarite
     * @param handler Handler-ul
     * @param xOffset Decalajul pe Ox
     * @param yOffset Decalajul pe Oy
     */
    public GameCamera(Handler handler, float xOffset, float yOffset) {
        this.handler = handler;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.previousEntityX = 0;
    }

    /**
     * Metoda care foloseste LERP (utilizand {@link #previousEntityX}, {@link #currScreenTargetX} si {@link #SPEED}) pentru a ajunge la entitatea dorita
     * @param e Enitatea urmarita
     */
    public void center(Entity e) {
        float entityCenterX = e.getX() + (float) e.getWidth() / 2;
        float targetY = e.getY() - (float) handler.getHeight() / 2 + (float) e.getHeight() / 2;

        if (e.getX() > previousEntityX) {
            currScreenTargetX = SCREEN_TARGET_X;
        } else if (e.getX() < previousEntityX) {
            currScreenTargetX= 1 - SCREEN_TARGET_X;
        }

        previousEntityX = e.getX();
        float idealXOffset = entityCenterX - ((float) handler.getWidth() * currScreenTargetX);

        xOffset += (idealXOffset - xOffset) * SPEED;
        yOffset = targetY;

        checkBlankSpace();

        xOffset = Math.round(xOffset);
        yOffset = Math.round(yOffset);
    }

    /**
     * Metoda care teleporteaza camera pe entitatea dorita
     * @param e Entitatea urmarita
     */
    public void snapTo(Entity e) {
        previousEntityX = e.getX();

        xOffset = e.getX() - ((float) handler.getWidth() * SCREEN_TARGET_X);
        yOffset = e.getY() - (float) handler.getHeight() / 2 + (float) e.getHeight() / 2;

        checkBlankSpace();

        xOffset = Math.round(xOffset);
        yOffset = Math.round(yOffset);
    }

    /**
     * Metoda care creeaza limitele camerei
     */
    private void checkBlankSpace() {
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

    /**
     * Metoda care blocheaza camera
     * @param x Coordonata x a dreptunghiului
     * @param y Coordonata y a dreptunghiului
     * @param width Latimea dreptunghiului
     * @param height Inaltimea dreptunghiului
     */
    public void setCustomBounds(float x, float y, float width, float height) {
        this.minX = x;
        this.minY = y;
        this.maxX = x + width;
        this.maxY = y + height;
        this.useLevelBounds = false;
    }

    /**
     * Metoda care deblocheaza camera
     */
    public void resetToLevelBounds() {
        this.useLevelBounds = true;
    }

    public float getxOffset() { return xOffset; }
    public float getyOffset() { return yOffset; }
}