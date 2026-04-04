package PaooGame.State;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.State.VisualElements.Button;

import java.awt.*;

public class ControlsMenu implements State {
    private final Handler handler;
    private final Button returnBtn;
    private int btnX, btnY;

    private final static int BOX_WIDTH = 300;
    private final static int BOX_HEIGTH = 40;
    private final static Color TEXT_COLOR = new Color(228, 214, 167);
    private final static int TEXT_SIZE = 40;

    public ControlsMenu(Handler handler) {
        this.handler = handler;

        btnX = handler.getWidth() - BOX_WIDTH;
        btnY = 10;

        returnBtn = new Button(btnX, btnY, BOX_WIDTH, BOX_HEIGTH,
                new String[]{"Return (ESC)"});
    }

    @Override
    public void onKeyTyped(char c) {}

    @Override
    public void Update() {
        btnX = handler.getWidth() - BOX_WIDTH;
        btnY = 5;

        returnBtn.setPosition(btnX, btnY);

        returnBtn.checkHover(
                handler.getMouseManager().getMouseX(),
                handler.getMouseManager().getMouseY()
        );

        if(returnBtn.getHovered()) {
            handler.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if(handler.getMouseManager().isLeftPressed()) {
                Handler.setState(new MainMenu(handler,
                        (handler.getCurrLevel() == 1 && handler.getCurrChkp() == 0) ? "New Game" : "Continue"));
            }
        }
        else {
            handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if(handler.getKeyManager().escape) {
            handler.getKeyManager().resetKeys();
            Handler.setState(new MainMenu(handler, "New Game"));
        }
    }

    @Override
    public void Draw(Graphics g) {
        g.drawImage(Assets.controlsMenuBg, 0, 0, handler.getWidth(), handler.getHeight(), null);

        returnBtn.Draw(g, TEXT_COLOR, TEXT_SIZE);
    }
}
