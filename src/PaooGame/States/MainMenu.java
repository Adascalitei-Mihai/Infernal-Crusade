package PaooGame.States;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.States.VisualElements.Button;

import java.awt.*;

/**
 * Clasa meniului principal
 */
public class MainMenu extends State {
    private final Button[] btns;

    private int btnsX, btnsY, scoreX, scoreY;

    private final static int BOX_WIDTH = 500;
    private final static int BOX_HEIGTH = 40;
    private final static Color TEXT_COLOR = new Color(228, 214, 167);
    private final static int TEXT_SIZE = 30;
    private final static int BTN_COUNT = 5;

    public MainMenu(Handler handler, String startText) {
        super(handler);

        btnsX = (handler.getWidth() - BOX_WIDTH) / 2;
        int totalHeight = BTN_COUNT * (BOX_HEIGTH + 10);
        btnsY = (handler.getHeight() - totalHeight) / 2;

        scoreX = 10;
        scoreY = handler.getHeight() - TEXT_SIZE * 2 + 10;

        // Determinăm ce texte va avea butonul de Level Select
        String[] levelSelectStrings;
        if (handler.getBestScore() != 0) {
            levelSelectStrings = new String[]{
                    "Level select: Continue Saved", // Index 0 (Nu modifică progresul salvat)
                    "Level select: Cogito (L1)",    // Index 1
                    "Level select: Selva Antica (L2)", // Index 2
                    "Level select: Flegitonta (L3)" // Index 3
            };
        } else {
            levelSelectStrings = new String[]{"Level select: Locked"};
        }

        btns = new Button[] {
                new Button(btnsX, btnsY, BOX_WIDTH, BOX_HEIGTH,
                        new String[]{startText}),

                // Folosim array-ul generat mai sus
                new Button(btnsX, btnsY + BOX_HEIGTH + 10, BOX_WIDTH, BOX_HEIGTH, levelSelectStrings),

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

        AudioManager.playBGM(SongAssets.menu);
    }

    @Override
    public void Update() {
        btnsX = (handler.getWidth() - BOX_WIDTH) / 2;
        int totalHeight = btns.length * (BOX_HEIGTH + 10);
        btnsY = (handler.getHeight() - totalHeight) / 2;

        scoreX = 10;
        scoreY = handler.getHeight() - TEXT_SIZE * 2 + 10;

        int mx = handler.getMouseManager().getMouseX();
        int my = handler.getMouseManager().getMouseY();

        boolean isReleased = handler.getMouseManager().isLeftReleased();
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

            if (isReleased) {
                handleButtonClick(hoveredIndex);
            }
        } else {
            handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (handler.getPadManager().enter) {
            handler.getPadManager().reset();
            handleStartGame(); // Am scos logica într-o funcție comună pentru a nu o repeta
        }

        if (handler.getKeyManager().escape) {
            handler.StopGame();
        }
    }

    private void handleButtonClick(int index) {
        switch (index) {
            case 0:
                handleStartGame();
                break;
            case 1:
                if (handler.getBestScore() != 0) {
                    btns[1].changeState();
                }
                break;
            case 2:
                btns[2].changeState();
                break;
            case 3:
                handler.getTransitionManager().start(() -> {
                    handler.setState(new ControlsMenu(handler));
                });
                break;
            case 4:
                handler.StopGame();
        }
    }

    // Funcția care se ocupă de procesarea alegerilor și pornirea jocului
    private void handleStartGame() {
        setDifficulty(btns[2].getState());

        // Verificăm dacă jucătorul are opțiunea de level select deblocată
        if (handler.getBestScore() != 0) {
            int selectedLevel = btns[1].getState();

            // Dacă este > 0, înseamnă că nu a lăsat pe "Continue Saved", ci a ales un nivel anume (1, 2 sau 3)
            if (selectedLevel > 0) {
                handler.setCurrLevel(selectedLevel);
                handler.setCurrChkp(0); // Începe de la începutul nivelului ales

                // VITAL: Trebuie să curățăm nivelul vechi din memorie (în caz că dăduse resume).
                // Astfel PlayState va fi forțat să apeleze handler.initLevel() cu noul nivel ales.
                handler.setLevel(null);
            }
        }

        handler.getTransitionManager().start(() -> {
            handler.setState(new PlayState(handler));
        });
    }

    @Override
    public void Draw(Graphics g) {
        g.drawImage(VisualAssets.mainMenuBg, 0, 0, handler.getWidth(), handler.getHeight(), null);

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.BOLD, TEXT_SIZE));

        g.drawString("Your best score: " + handler.getBestScore(), scoreX, scoreY);
        g.drawString("Global best score: " + handler.getGlobalScore(), scoreX, scoreY + TEXT_SIZE + 5);

        for (Button btn : btns) {
            btn.Draw(g, TEXT_COLOR, TEXT_SIZE);
        }
    }

    public void setDifficulty(int state) {
        Handler.Difficulty currDifficulty = switch (state) {
            case 0 -> Handler.Difficulty.EASY;
            case 2 -> Handler.Difficulty.HARD;
            default -> Handler.Difficulty.NORMAL;
        };
        handler.setDifficulty(currDifficulty);
    }
}