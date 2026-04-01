package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.VisualElements.Button;

import java.awt.*;

/**
 * Clasa meniului principal
 */
public class MainMenu extends State {
    private final Button[] btns;

    private int btnsX, btnsY, scoreX, scoreY;

    private final static int BOX_WIDTH = 500;
    private final static int BOX_HEIGTH = 40;
    private final static Color TEXT_COLOR = new Color(200, 255, 0);
    private final static int TEXT_SIZE = 30;

    public MainMenu(Game game, String startText) {
        super(game);
        btns = new Button[] {
                new Button(0, 0, BOX_WIDTH, BOX_HEIGTH,
                        new String[]{startText}),
                new Button(0, 0, BOX_WIDTH, BOX_HEIGTH,
                        new String[]{
                                "Level select: Locked",
                                "Level select: Cogito",
                                "Level select: Selva Antica",
                                "Level select: Flegitonta"
                        }),
                new Button(0, 0, BOX_WIDTH, BOX_HEIGTH,
                        new String[]{
                                "Difficulty: Easy",
                                "Difficulty: Normal",
                                "Difficulty: Hard"
                        }),
                new Button(0, 0, BOX_WIDTH, BOX_HEIGTH,
                    new String[]{"Controls"}),
                new Button(0, 0, BOX_WIDTH, BOX_HEIGTH,
                    new String[]{"Quit (ESC)"})
        };
    }

    @Override
    public void Update() {
        this.btnsX = (this.game.getWidth() - BOX_WIDTH) / 2;
        int totalHeight = btns.length * (BOX_HEIGTH + 10);
        this.btnsY = (this.game.getHeight() - totalHeight) / 2;

        int mx = this.game.getMouseManager().getMouseX();
        int my = this.game.getMouseManager().getMouseY();

        boolean hasClicked = this.game.getMouseManager().isLeftReleased();
        int hoveredIndex = -1;

        for (int i = 0; i < btns.length; i++) {
            btns[i].setPosition(this.btnsX, this.btnsY + i * (BOX_HEIGTH + 10));
            btns[i].checkHover(mx, my);

            if (btns[i].getHovered()) {
                hoveredIndex = i;
            }
        }


        if (hoveredIndex != -1) {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (hasClicked) {
                handleButtonClick(hoveredIndex);
            }
        } else {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (this.game.getKeyManager().escape) {
            this.game.StopGame();
        }
    }

    private void handleButtonClick(int index) {
        switch (index) {
            case 0 -> State.setState(null);
            case 1, 2 -> btns[index].changeState();
            case 3 -> State.setState(null);
            case 4 -> this.game.StopGame();
        }
    }

    @Override
    public void Draw(Graphics g) {
        g.drawImage(Assets.mainMenuBg, 0, 0, this.game.getWidth(), this.game.getHeight(), null);

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE));

        g.drawString("Your best score: ----", scoreX, scoreY);
        g.drawString("Global best score: ----", scoreX, scoreY + TEXT_SIZE + 5);

        for (Button btn : btns) {
            btn.Draw(g, TEXT_COLOR, TEXT_SIZE);
        }
    }
}
