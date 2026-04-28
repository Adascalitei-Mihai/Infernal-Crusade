package PaooGame.Game.Input;

import PaooGame.Handler;

public abstract class InputManager {
    protected final Handler handler;
    public boolean up, down, left, right, space,
            shift, j, k, l,
            enter, escape, debug;

    public InputManager(Handler handler) {
        this.handler = handler;
    }

    public abstract void Update();
    public void reset() {
        up = down = left = right = space = shift = j = k = l = enter = escape = debug = false;
    }
}
