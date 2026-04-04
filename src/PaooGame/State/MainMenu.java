package PaooGame.State;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.State.VisualElements.Button;

import java.awt.*;

/**
 * Clasa meniului principal
 */
public class MainMenu implements State {
    private final Handler handler;
    private final Button[] btns;

    private int btnsX, btnsY, scoreX, scoreY;

    private final static int BOX_WIDTH = 500;
    private final static int BOX_HEIGTH = 40;
    private final static Color TEXT_COLOR = new Color(228, 214, 167);
    private final static int TEXT_SIZE = 30;
    private final static int BTN_COUNT = 5;

    public MainMenu(Handler handler, String startText) {
        this.handler = handler;

        btnsX = (handler.getWidth() - BOX_WIDTH) / 2;
        int totalHeight = BTN_COUNT * (BOX_HEIGTH + 10);
        btnsY = (handler.getHeight() - totalHeight) / 2;

        scoreX = 10;
        scoreY = handler.getHeight() - TEXT_SIZE * 2 + 10;

        btns = new Button[] {
                new Button(btnsX, btnsY, BOX_WIDTH, BOX_HEIGTH,
                        new String[]{startText}),
                new Button(btnsX, btnsY + BOX_HEIGTH + 10, BOX_WIDTH, BOX_HEIGTH,
                        new String[]{
                                "Level select: Locked",
                                "Level select: Cogito",
                                "Level select: Selva Antica",
                                "Level select: Flegitonta"
                        }),
                new Button(btnsX, btnsY + 2 * (BOX_HEIGTH + 10), BOX_WIDTH, BOX_HEIGTH,
                        new String[]{
                                "Difficulty: Easy",
                                "Difficulty: Normal",
                                "Difficulty: Hard"
                        }),
                new Button(btnsX, btnsY + 3 * (BOX_HEIGTH + 10), BOX_WIDTH, BOX_HEIGTH,
                    new String[]{"Controls"}),
                new Button(btnsX, btnsY + 4 * (BOX_HEIGTH + 10), BOX_WIDTH, BOX_HEIGTH,
                    new String[]{"Quit (ESC)"})
        };
    }

    @Override
    public void onKeyTyped(char c) {}

    @Override
    public void Update() {
        btnsX = (handler.getWidth() - BOX_WIDTH) / 2;
        int totalHeight = btns.length * (BOX_HEIGTH + 10);
        btnsY = (handler.getHeight() - totalHeight) / 2;

        scoreX = 10;
        scoreY = handler.getHeight() - TEXT_SIZE * 2 + 10;

        int mx = handler.getMouseManager().getMouseX();
        int my = handler.getMouseManager().getMouseY();

        boolean isRealased = handler.getMouseManager().isLeftReleased();
        int hoveredIndex = -1;
        for (int i = 0; i < btns.length; i++) {
            btns[i].setPosition(btnsX, btnsY + i * (BOX_HEIGTH + 10));
            btns[i].checkHover(mx, my);

            if (btns[i].getHovered()) {
                hoveredIndex = i;
            }
        }


        if (hoveredIndex != -1) {
            handler.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (isRealased) {
                handleButtonClick(hoveredIndex);
            }
        } else {
            handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (handler.getKeyManager().escape) {
            handler.StopGame();
        }
    }

    private void handleButtonClick(int index) {
        switch (index) {
            case 0:
                Handler.setState(new PlayState(handler));
                break;
            case 1:
                if(handler.getBestScore() != 0) {
                    btns[1].changeState();
                }
                break;
            case 2:
                btns[2].changeState();
                break;
            case 3:
                Handler.setState(new ControlsMenu(handler));
                break;
            case 4:
                handler.StopGame();
        }
    }

    @Override
    public void Draw(Graphics g) {
        g.drawImage(Assets.mainMenuBg, 0, 0, handler.getWidth(), handler.getHeight(), null);

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.BOLD, TEXT_SIZE));

        g.drawString("Your best score: " + handler.getBestScore(), scoreX, scoreY);
        g.drawString("Global best score: " + handler.getGlobalScore(), scoreX, scoreY + TEXT_SIZE + 5);

        for (Button btn : btns) {
            btn.Draw(g, TEXT_COLOR, TEXT_SIZE);
        }
    }
}
