package PaooGame.States;

import PaooGame.Handler;
import PaooGame.States.VisualElements.Button;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Clasa care reprezinta ecranul de finalizare a jocului (Victorie).
 */
public class GameDoneState extends State {
    private final Button returnBtn;
    private final int finalScore;

    private final static int BOX_WIDTH = 350;
    private final static int BOX_HEIGHT = 40;
    private final static Color TEXT_COLOR = new Color(228, 214, 167);

    private final static int TITLE_SIZE = 60;
    private final static int SCORE_SIZE = 40;
    private final static int BTN_TEXT_SIZE = 30;

    /**
     * Constructorul care preia scorul final si initializeaza butonul
     * @param handler Handler-ul
     */
    public GameDoneState(Handler handler) {
        super(handler);

        this.finalScore = handler.getDbMan().getTotalBestScore(handler.getPlayerID(), Handler.NR_CHKPTS);

        int btnX = (handler.getWidth() - BOX_WIDTH) / 2;
        int btnY = handler.getHeight() / 2 + 100;

        returnBtn = new Button(btnX, btnY, BOX_WIDTH, BOX_HEIGHT, new String[]{"Return to Menu"});
    }

    /**
     * Metoda de actualizare care verifica interactiunea cu butonul sau tasta ESC
     */
    @Override
    public void Update() {
        int mx = handler.getMouseManager().getMouseX();
        int my = handler.getMouseManager().getMouseY();

        returnBtn.checkHover(mx, my);

        if (returnBtn.getHovered()) {
            handler.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (handler.getMouseManager().isLeftReleased()) {
                returnToMainMenu();
            }
        } else {
            handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if(handler.getInputManager().escape) {
            handler.getGame().getGameCamera().resetToLevelBounds();
            handler.getGame().getWnd().setWindowed();
            handler.getInputManager().reset();

            returnToMainMenu();
        }
    }

    /**
     * Metoda care realizeaza tranzitia spre {@link MainMenu}
     */
    private void returnToMainMenu() {
        handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        handler.getTransitionManager().start(() -> {
            handler.updateScores();
            handler.setCurrChkp(0);
            handler.setCurrLevel(1);
            handler.setState(new MainMenu(handler, "Start Game"));
        });
    }

    /**
     * Metoda de desenare a ecranului de final
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.BOLD, TITLE_SIZE));

        String congratsText = "CONGRATULATIONS!";
        FontMetrics fm = g.getFontMetrics();
        int cX = (handler.getWidth() - fm.stringWidth(congratsText)) / 2;
        g.drawString(congratsText, cX, handler.getHeight() / 2 - 50);

        g.setFont(new Font("Monospaced", Font.PLAIN, SCORE_SIZE));
        String scoreText = "Total Score: " + finalScore;
        fm = g.getFontMetrics();
        int sX = (handler.getWidth() - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, sX, handler.getHeight() / 2 + 20);

        returnBtn.Draw(g, TEXT_COLOR, BTN_TEXT_SIZE);
    }
}