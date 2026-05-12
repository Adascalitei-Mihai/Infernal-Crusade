package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import java.awt.Graphics;
import java.awt.Rectangle;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta o poarta blocanta pentru arene ({@link ArenaTrigger})
 */
public class Gate extends Entity {
    public static final int LOGIC_WIDTH = TILE_SIZE;
    public static final int LOGIC_HEIGHT = 4 * TILE_SIZE;

    /**
     * Enumeratie care defineste starile posibile ale portii
     */
    public enum GateState {
        OPEN,
        CLOSING,
        CLOSED,
        OPENING
    }

    private final Animation openAnim, closeAnim;
    private GateState currentState = GateState.OPEN;

    /**
     * Constructorul care initializeaza o poarta in starea deschisa si incarca animatiile corespunzatoare
     *
     * @param handler Handler-ul
     * @param x Pozitia pe Ox
     * @param y Pozitia pe Oy
     */
    public Gate(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT);

        closeAnim = new Animation(30, VisualAssets.spear_closing);
        openAnim = new Animation(30, VisualAssets.spear_opening);

        // Hitbox-ul fizic al portii (ingust, dar inalt)
        this.bounds = new Rectangle(2, 0, 12, 64);
    }

    /**
     * Metoda care actualizeaza animatiile portii si tranzitiile dintre stadiile de deschidere sau inchidere
     */
    @Override
    public void Update() {
        if (currentState == GateState.CLOSING) {
            closeAnim.Update();

            if (closeAnim.isFinished()) {
                currentState = GateState.CLOSED;
            }
        } else if (currentState == GateState.OPENING) {
            openAnim.Update();

            if (openAnim.isFinished()) {
                currentState = GateState.OPEN;
                closeAnim.reset();
            }
        }
    }

    /**
     * Metoda care deseneaza cadrul curent al portii in functie de starea in care se afla, luand in calcul decalajul camerei
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        switch (currentState) {
            case CLOSING -> g.drawImage(closeAnim.getCurrentFrame(), screenX, screenY, visualWidth, visualHeight, null);
            case OPENING -> g.drawImage(openAnim.getCurrentFrame(), screenX, screenY, visualWidth, visualHeight, null);
            case CLOSED -> g.drawImage(VisualAssets.spear_closing[VisualAssets.spear_closing.length - 1], screenX, screenY, visualWidth, visualHeight, null);
            case OPEN -> {}
        }
    }

    /**
     * Metoda care declanseaza procesul de inchidere al portii
     */
    public void close() {
        if (currentState == GateState.OPEN) {
            currentState = GateState.CLOSING;
            openAnim.reset();
        }
    }

    /**
     * Metoda care declanseaza procesul de deschidere al portii
     */
    public void open() {
        if (currentState == GateState.CLOSED) {
            currentState = GateState.OPENING;
        }
    }

    /**
     * Metoda care indica motorului de coliziuni daca poarta actioneaza ca un perete
     * @return Daca poarta este inchisa sau se inchide
     */
    @Override
    public boolean isSolid() {
        return (currentState == GateState.CLOSING || currentState == GateState.CLOSED);
    }

    /**
     * Metoda vida
     * @param e Entitatea cu care s-a intersectat
     */
    @Override
    public void onInteraction(Entity e) {}
}