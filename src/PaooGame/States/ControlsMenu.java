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

        this.btnX = this.game.getWidth() - BOX_WIDTH;
        this.btnY = 10;

        returnBtn = new Button(this.btnX, this.btnY, BOX_WIDTH, BOX_HEIGTH,
                new String[]{"Return (ESC)"});
    }

    @Override
    public void Update() {
        this.btnX = this.game.getWidth() - BOX_WIDTH;
        this.btnY = 5;

        returnBtn.setPosition(this.btnX, this.btnY);

        returnBtn.checkHover(
                this.game.getMouseManager().getMouseX(),
                this.game.getMouseManager().getMouseY()
        );

        if(returnBtn.getHovered()) {
            this.game.getWnd().getFrame().setCursor(new Cursor(Cursor.HAND_CURSOR));

            if(this.game.getMouseManager().isLeftPressed()) {
                State.setState(new MainMenu(this.game, "New Game"));
            }
        }
        else {
            this.game.getWnd().getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if(this.game.getKeyManager().escape) {
            this.game.getKeyManager().resetKeys();
            State.setState(new MainMenu(this.game, "New Game"));
        }
    }

    @Override
    public void Draw(Graphics g) {
        g.drawImage(Assets.controlsMenuBg, 0, 0, this.game.getWidth(), this.game.getHeight(), null);

        returnBtn.Draw(g, TEXT_COLOR, TEXT_SIZE);
    }
}
