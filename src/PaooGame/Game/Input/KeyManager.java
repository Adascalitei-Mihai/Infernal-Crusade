package PaooGame.Game.Input;

import PaooGame.Handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/**
 * Clasa care traduce actiunile tastaturii.
 */
public class KeyManager extends InputManager implements KeyListener {
    private final boolean[] keys;

    /**
     * Constructor vid, initializeaza un vector in care retinem caracterele apasate.
     */
    public KeyManager(Handler handler) {
        super(handler);
        keys = new boolean[256];
    }

    /**
     * Functie care traduce tastele uzuale.
     */
    @Override
    public void Update() {
        up = keys[KeyEvent.VK_W];
        down = keys[KeyEvent.VK_S];
        left = keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_D];

        space = keys[KeyEvent.VK_SPACE];

        shift = keys[KeyEvent.VK_SHIFT];
        j = keys[KeyEvent.VK_J];
        k = keys[KeyEvent.VK_K];
        l = keys[KeyEvent.VK_L];

        enter = keys[KeyEvent.VK_ENTER];
        escape = keys[KeyEvent.VK_ESCAPE];
        pause = keys[KeyEvent.VK_P];
        debug = keys[KeyEvent.VK_BACK_SLASH];
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
        if(handler.getState() != null) {
            handler.getState().onKeyTyped(e.getKeyChar());
        }
    }

    @Override
    public void reset() {
        Arrays.fill(keys, false);
        super.reset();
    }
}