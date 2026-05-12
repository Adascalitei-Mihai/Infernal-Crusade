package PaooGame.States;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.States.VisualElements.Button;
import PaooGame.States.VisualElements.TextBox;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Clasa care reprezinta starea meniului de start
 */
public class StartMenu extends State {
    private final TextBox nameInput;
    private final Button quitBtn;

    private int labelX, labelY;

    private final static int BOX_WIDTH = 300;
    private final static int BOX_HEIGHT = 50;
    private final static Color TEXT_COLOR = new Color(255, 140, 0);
    private final static int TEXT_SIZE = 36;

    /**
     * Constructorul care initializeaza starea meniului de start, caseta de text si butonul de iesire
     * @param handler Handler-ul
     */
    public StartMenu(Handler handler) {
        super(handler);

        nameInput = new TextBox(0, 0, BOX_WIDTH, BOX_HEIGHT);
        quitBtn = new Button(0, 0, BOX_WIDTH, BOX_HEIGHT, new String[]{"Quit (ESC)"});

        AudioManager.playBGM(SongAssets.menu);
    }

    /**
     * Metoda care transmite caracterele tastate direct catre caseta de text activa
     * @param c Caracterul adaugat.
     */
    @Override
    public void onKeyTyped(char c) {
        nameInput.addChar(c);
    }

    /**
     * Metoda care actualizeaza pozitiile elementelor, verifica interactiunile cu mouse-ul
     * si proceseaza input-ul de la tastatura pentru autentificare sau iesire
     */
    @Override
    public void Update() {
        labelX = handler.getWidth() - BOX_WIDTH - 200;
        labelY = handler.getHeight() - 250;

        nameInput.setPosition(labelX, labelY + 20);
        quitBtn.setPosition(labelX, labelY + 80);

        int mx = handler.getMouseManager().getMouseX();
        int my = handler.getMouseManager().getMouseY();

        quitBtn.checkHover(mx, my);
        nameInput.checkHover(mx, my);

        if (quitBtn.getHovered()) {
            handler.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (handler.getMouseManager().isLeftReleased()) {
                handler.StopGame();
            }
        } else if (nameInput.getFocus()) {
            handler.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        } else {
            handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (handler.getKeyManager().enter) {
            handleLogin();
        }

        if (handler.getKeyManager().escape) {
            handler.StopGame();
        }
    }

    /**
     * Metoda care proceseaza numele introdus de jucator, incarca datele din baza de date si initiaza tranzitia catre meniul principal
     */
    private void handleLogin() {
        String playerName = nameInput.getText().trim();

        if (!playerName.isEmpty()) {
            handler.playerLogin(playerName);
            handler.getKeyManager().reset();

            handler.getTransitionManager().start(() -> {
                String menuText = (handler.getCurrLevel() == 1 && handler.getCurrChkp() == 0) ? "New Game" : "Continue";
                handler.setState(new MainMenu(handler, menuText));
            });
        }
    }

    /**
     * Metoda care deseneaza fundalul, textul explicativ, caseta de input si butonul de iesire
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        g.drawImage(VisualAssets.startMenuBg, 0, 0, handler.getWidth(), handler.getHeight(), null);

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.BOLD, TEXT_SIZE));
        g.drawString("Introdu numele: ", labelX, labelY);

        nameInput.Draw(g);
        quitBtn.Draw(g, TEXT_COLOR, TEXT_SIZE);
    }
}