package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Main;
import PaooGame.VisualElements.Button;

import java.awt.*;

public class ControlsMenu extends State {
    private final Button returnBtn;
    private int btnX, btnY;

    private final static int BOX_WIDTH = 300;
    private final static int BOX_HEIGTH = 40;
    private final static Color TEXT_COLOR = new Color(228, 214, 167);
    private final static int TEXT_SIZE = 40;

    public ControlsMenu(Game game) {
        super(game);

        btnX = game.getWidth() - BOX_WIDTH;
        btnY = 10;

        returnBtn = new Button(btnX, btnY, BOX_WIDTH, BOX_HEIGTH,
                new String[]{"Return (ESC)"});
    }

    @Override
    public void Update() {
        btnX = game.getWidth() - BOX_WIDTH;
        btnY = 5;

        returnBtn.setPosition(btnX, btnY);

        returnBtn.checkHover(
                game.getMouseManager().getMouseX(),
                game.getMouseManager().getMouseY()
        );

        if(returnBtn.getHovered()) {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.HAND_CURSOR));

            if(game.getMouseManager().isLeftPressed()) {
                State.setState(new MainMenu(game,
                        (game.getCurrLevel() == 1 && game.getCurrChkp() == 0) ? "New Game" : "Continue"));
            }
        }
        else {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if(game.getKeyManager().escape) {
            game.getKeyManager().resetKeys();
            State.setState(new MainMenu(game, "New Game"));
        }
    }

    @Override
    public void Draw(Graphics g) {
        g.drawImage(Assets.controlsMenuBg, 0, 0, game.getWidth(), game.getHeight(), null);

        returnBtn.Draw(g, TEXT_COLOR, TEXT_SIZE);
    }
}
