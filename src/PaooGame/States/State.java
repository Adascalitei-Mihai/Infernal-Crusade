package PaooGame.States;

import PaooGame.Handler;
import java.awt.Graphics;

/**
 * Clasa care reprezinta o stare a jocului si asigura legatura cu logica principala
 */
public abstract class State {

    protected Handler handler;

    /**
     * Constructor care initializeaza starea curenta
     * @param handler Handler-ul
     */
    public State(Handler handler) {
        this.handler = handler;
    }

    /**
     * Metoda care proceseaza caracterele tastate de utilizator <br>
     * Are o implementare goala in mod implicit, dar poate fi suprascrisa de starile
     * care necesita introducere de text (ex: StartMenu pentru logare)
     * @param c Caracterul tastat.
     */
    public void onKeyTyped(char c) {
    }

    /**
     * Metoda abstracta care actualizeaza logica interna si evenimentele starii curente.
     */
    public abstract void Update();

    /**
     * Metoda abstracta care deseneaza elementele vizuale ale starii curente.
     * @param g Grafica de desenare
     */
    public abstract void Draw(Graphics g);
}