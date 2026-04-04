package PaooGame.States;

import PaooGame.Game;
import PaooGame.Handler;

import java.awt.*;

/**
 * Clasa abstracta care face legatura intre joc si starea curenta.
 */
public abstract class State {
    private static State currentState = null;

    /**
     * Setter pentru modificarea starii curente.
     * @param state Noua stare.
     */
    public static void setState(State state) { currentState = state; }

    /**
     * Getter pentru starea curenta.
     * @return Starea curenta.
     */
    public static State getState() { return currentState; }

    protected Handler handler;

    /**
     * Constructor, primeste referinta la joc.
     * @param game Instanta jocului actual.
     */
    public State(Handler handler) {
        this.handler = handler;
    }

    /**
     * Functie care primeste scrierea de taste, folosita doar in meniuri care necesita casete text.
     * @param c Caracterul primit prin intermediul KeyManager.
     */
    public void onKeyTyped(char c) {}

    /**
     * Functia de actualizare, necesara oricarei stari.
     */
    public abstract void Update();

    /**
     * Functia de desenare, necesara oricarei stari.
     * @param g Grafica jocului.
     */
    public abstract void Draw(Graphics g);
}
