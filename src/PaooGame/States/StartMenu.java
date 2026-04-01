package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.VisualElements.Button;
import PaooGame.VisualElements.TextBox;

import java.awt.*;

/**
 * Clasa meniului de inceput, este una dintre starile posibile.
 */
public class StartMenu extends State {
    private final TextBox nameInput;
    private final Button quitBtn;

    private int labelX, labelY;

    private final static int BOX_WIDTH = 300;
    private final static int BOX_HEIGTH = 50;
    private final static Color TEXT_COLOR = new Color(255, 140, 0);
    private final static int TEXT_SIZE = 36;

    /**
     * Constructor care primeste jocul si creeaza o caseta text.
     * @param game Instanta jocului actual.
     */
    public StartMenu(Game game) {
        super(game);

        nameInput = new TextBox(0, 0, BOX_WIDTH, BOX_HEIGTH);
        quitBtn = new Button(0, 0, BOX_WIDTH, BOX_HEIGTH,
                new String[]{"Quit (ESC)"});
    }

    /**
     * Functie care primeste un caracter prin intermediul KeyManager.
     * @param c Caracterul adaugat in caseta text.
     */
    @Override
    public void onKeyTyped(char c) {
        nameInput.addChar(c);
    }

    /**
     * Functia de actualizare, aceasta: <br>
     * 1. Modifica pozitia textului in functie de marimea jocului, se ancoreaza de coltul dreapta-jos. <br>
     * 2. Ii transmite casetei text daca a fost apasata. <br>
     * 3. Asculta pentru apasarea testei: <br>
     *      Enter - porneste MainMenu <br>
     *      Escape - inchide jocul.
     */
    @Override
    public void Update() {
        labelX = game.getWidth() - BOX_WIDTH - 200;
        labelY = game.getHeight() - 250;

        nameInput.setPosition(labelX, labelY + 20);
        quitBtn.setPosition(labelX, labelY + 80);

        int mx = game.getMouseManager().getMouseX();
        int my = game.getMouseManager().getMouseY();

        quitBtn.checkHover(mx, my);
        nameInput.checkHover(mx, my);

        if(quitBtn.getHovered()) {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.HAND_CURSOR));

            if(game.getMouseManager().isLeftPressed()) {
                game.StopGame();
            }
        }
        else if (nameInput.getFocus()){
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }
        else {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if(game.getKeyManager().enter) {
            String playerName = nameInput.getText();

            if(!playerName.trim().isEmpty()) {
                game.playerLogin(playerName.trim());

                State.setState(new MainMenu(game,
                        (game.getCurrLevel() == 1 && game.getCurrChkp() == 0) ? "New Game" : "Continue"));
            }
        }

        if(game.getKeyManager().escape) {
            game.StopGame();
        }
    }

    /**
     * Functia de desenare, aceasta: <br>
     * 1. Deseneaza si modifica fundalul primit prin Assets. <br>
     * 2. Deseneaza in locul stabilit de Update() textul si caseta.
     * @param g Grafica jocului.
     */
    @Override
    public void Draw(Graphics g) {
        g.drawImage(Assets.startMenuBg, 0, 0, game.getWidth(), game.getHeight(), null);

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.BOLD, TEXT_SIZE));
        g.drawString("Introdu numele: ", labelX, labelY);

        nameInput.Draw(g);
        quitBtn.Draw(g, TEXT_COLOR, TEXT_SIZE);
    }
}
