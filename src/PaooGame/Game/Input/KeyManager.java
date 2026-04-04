package PaooGame.Game.Input;

import PaooGame.Handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/**
 * Clasa care traduce actiunile tastaturii.
 */
public class KeyManager implements KeyListener {

    private final boolean[] keys;
    public boolean up, down, left, right,
            shift, j, k, l,
            enter, escape;

    /**
     * Constructor vid, initializeaza un vector in care retinem caracterele apasate.
     */
    public KeyManager() {
        keys = new boolean[256];
    }

    /**
     * Functie care traduce tastele uzuale.
     */
    public void Update() {
        up = keys[KeyEvent.VK_W];
        down = keys[KeyEvent.VK_S];
        left = keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_D];

        shift = keys[KeyEvent.VK_SHIFT];
        j = keys[KeyEvent.VK_J];
        k = keys[KeyEvent.VK_K];
        l = keys[KeyEvent.VK_L];

        enter = keys[KeyEvent.VK_ENTER];
        escape = keys[KeyEvent.VK_ESCAPE];
    }

    /**
     * Functie care retine apasarea unei taste.
     * @param e Evenimentul procesat.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = true;
        }
    }

    /**
     * Functie care retine eliberarea unei taste.
     * @param e Evenimentul procesat.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = false;
        }
    }

    /**
     * Functie care trimite scrierea unei taste starii actuale.
     * @param e Evenimentul procesat.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if(Handler.getState() != null) {
            Handler.getState().onKeyTyped(e.getKeyChar());
        }
    }

    public void resetKeys() {
        Arrays.fill(keys, false);

        up = false;
        down = false;
        left = false;
        right = false;

        shift = false;
        j = false;
        k = false;
        l = false;

        enter = false;
        escape = false;
    }
}