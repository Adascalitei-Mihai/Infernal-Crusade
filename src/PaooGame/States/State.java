package PaooGame.States;
import PaooGame.Handler;

import java.awt.*;

/**
 * Clasa abstracta care face legatura intre joc si starea curenta.
 */
public abstract class State {
    protected Handler handler;

    public State(Handler handler) {
        this.handler = handler;
    }

    public void onKeyTyped(char c) {}
    public abstract void Update();
    public abstract void Draw(Graphics g);
}
