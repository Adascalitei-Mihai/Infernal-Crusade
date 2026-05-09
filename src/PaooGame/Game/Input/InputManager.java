package PaooGame.Game.Input;

import PaooGame.Handler;

/**
 * Clasa care asculta input-uri
 */
public abstract class InputManager {
    protected final Handler handler;
    public boolean up, down, left, right, space,
            shift, j, k, l,
            enter, escape, pause, debug;

    public InputManager(Handler handler) {
        this.handler = handler;
    }

    public abstract void Update();

    /**
     * Metoda care reseteaza toate tastele
     */
    public void reset() {
        up = down = left = right = space = shift = j = k = l = enter = escape = pause = debug = false;
    }
}
