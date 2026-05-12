package PaooGame.States;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.States.VisualElements.Button;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Clasa care reprezinta meniul principal al jocului
 */
public class MainMenu extends State {
    private final Button[] btns;

    private final static int BOX_WIDTH = 500;
    private final static int BOX_HEIGHT = 40;
    private final static int BTN_SPACING = 10;
    private final static Color TEXT_COLOR = new Color(228, 214, 167);
    private final static int TEXT_SIZE = 30;

    /**
     * Constructorul meniului principal, initializeaza butoanele si porneste muzica de fundal
     * @param handler Handler-ul
     * @param startText Textul afisat pentru primul buton ("New Game" sau "Continue")
     */
    public MainMenu(Handler handler, String startText) {
        super(handler);

        int btnsX = (handler.getWidth() - BOX_WIDTH) / 2;
        int totalHeight = 5 * (BOX_HEIGHT + BTN_SPACING);
        int btnsY = (handler.getHeight() - totalHeight) / 2;

        btns = new Button[] {
                new Button(btnsX, btnsY, BOX_WIDTH, BOX_HEIGHT,
                        new String[]{startText}),

                new Button(btnsX, btnsY + BOX_HEIGHT + BTN_SPACING, BOX_WIDTH, BOX_HEIGHT,
                        getLevelSelectTexts(handler)),

                new Button(btnsX, btnsY + 2 * (BOX_HEIGHT + BTN_SPACING), BOX_WIDTH, BOX_HEIGHT,
                        new String[]{
                                "Difficulty: Easy",
                                "Difficulty: Normal",
                                "Difficulty: Hard"
                        }),

                new Button(btnsX, btnsY + 3 * (BOX_HEIGHT + BTN_SPACING), BOX_WIDTH, BOX_HEIGHT,
                        new String[]{"Controls"}),

                new Button(btnsX, btnsY + 4 * (BOX_HEIGHT + BTN_SPACING), BOX_WIDTH, BOX_HEIGHT,
                        new String[]{"Quit (ESC)"})
        };

        AudioManager.playBGM(SongAssets.menu);
    }

    /**
     * Metoda care determina optiunile disponibile pentru selectia nivelului, in functie de progresul salvat
     */
    private String[] getLevelSelectTexts(Handler handler) {
        if (handler.getBestScore() != 0) {
            return new String[]{
                    "Level select: Continue Saved",
                    "Level select: Cogito (L1)",
                    "Level select: Selva Antica (L2)",
                    "Level select: Flegitonta (L3)"
            };
        }
        return new String[]{"Level select: Locked"};
    }

    /**
     * Metoda de actualizare <br>
     * 1. Recalculeaza pozitia butoanelor (daca fereastra a fost redimensionata <br>
     * 2. Modifica butonul care este selectat si cursorul <br>
     * 3. Asculta daca controllerul trimite enter pentru a intra sau daca este apasata tasta ESC pentru a iesi din joc
     */
    @Override
    public void Update() {
        int btnsX = (handler.getWidth() - BOX_WIDTH) / 2;
        int totalHeight = btns.length * (BOX_HEIGHT + BTN_SPACING);
        int btnsY = (handler.getHeight() - totalHeight) / 2;

        int mx = handler.getMouseManager().getMouseX();
        int my = handler.getMouseManager().getMouseY();
        boolean isReleased = handler.getMouseManager().isLeftReleased(); // 1

        int hoveredIndex = -1;

        for (int i = 0; i < btns.length; i++) {
            btns[i].setPosition(btnsX, btnsY + i * (BOX_HEIGHT + BTN_SPACING));
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
        } // 2

        if (handler.getPadManager().enter) {
            handler.getPadManager().reset();
            handleStartGame();
        }

        if (handler.getKeyManager().escape) {
            handler.StopGame();
        } // 3
    }

    /**
     * Metoda care gestioneaza apasarea unui buton
     */
    private void handleButtonClick(int index) {
        switch (index) {
            case 0 -> handleStartGame();
            case 1 -> {
                if (handler.getBestScore() != 0) {
                    btns[1].changeState();
                }
            }
            case 2 -> btns[2].changeState();
            case 3 -> handler.getTransitionManager().start(() -> handler.setState(new ControlsMenu(handler)));
            case 4 -> handler.StopGame();
        }
    }

    /**
     * Metoda care proceseaza alegerea nivelului din meniu si initiaza tranzitia catre starea de joc {@link PlayState}
     */
    private void handleStartGame() {
        setDifficulty(btns[2].getState());

        if (handler.getBestScore() != 0) {
            int selectedLevel = btns[1].getState();

            if (selectedLevel > 0) {
                handler.setCurrLevel(selectedLevel);
                handler.setCurrChkp(0);
                handler.setLevel(null);
            }
        }

        handler.getTransitionManager().start(() -> handler.setState(new PlayState(handler)));
    }

    /**
     * Metoda de desenare, afiseaza fundalul din {@link VisualAssets#mainMenuBg}, butoanele si scorurile totale
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        g.drawImage(VisualAssets.mainMenuBg, 0, 0, handler.getWidth(), handler.getHeight(), null);

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.BOLD, TEXT_SIZE));

        int scoreX = 10;
        int scoreY = handler.getHeight() - TEXT_SIZE * 2 + 10;

        g.drawString("Your best score: " + handler.getBestScore(), scoreX, scoreY);
        g.drawString("Global best score: " + handler.getGlobalScore(), scoreX, scoreY + TEXT_SIZE + 5);

        for (Button btn : btns) {
            btn.Draw(g, TEXT_COLOR, TEXT_SIZE);
        }
    }

    /**
     * Metoda care seteaza dificultatea in clasa Handler in functie de selectia jucatorului
     */
    public void setDifficulty(int state) {
        Handler.Difficulty currDifficulty = switch (state) {
            case 0 -> Handler.Difficulty.EASY;
            case 2 -> Handler.Difficulty.HARD;
            default -> Handler.Difficulty.NORMAL;
        };
        handler.setDifficulty(currDifficulty);
    }
}