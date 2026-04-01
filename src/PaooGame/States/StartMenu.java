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

    private final static int BOX_WIDTH = 300;
    private final static int BOX_HEIGTH = 50;

    private int labelX, labelY;
    private final static Color menuColor = new Color(255, 140, 0);
    private final static int textSize = 36;

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
        int rightMargin = 200;
        int bottomMargin = 250;

        this.labelX = this.game.getWidth() - BOX_WIDTH - rightMargin;
        this.labelY = this.game.getHeight() - bottomMargin;
        nameInput.setPosition(this.labelX, this.labelY + 20);
        quitBtn.setPosition(this.labelX, this.labelY + 80);

        int mx = this.game.getMouseManager().getMouseX();
        int my = this.game.getMouseManager().getMouseY();

        quitBtn.checkHover(mx, my);
        nameInput.checkHover(mx, my);

        if(quitBtn.getHovered()) {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        else if (nameInput.getFocus()){
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }
        else {
            game.getWnd().getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if(this.game.getMouseManager().isLeftPressed()) {

            if(quitBtn.getHovered()) {
                this.game.StopGame();
            }
        }

        if(this.game.getKeyManager().enter) {
            String playerName = nameInput.getText();

            if(!playerName.trim().isEmpty()) {
                System.out.println("Jucator: " + playerName);
            }
        }

        if(this.game.getKeyManager().escape) {
            this.game.StopGame();
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
        g.drawImage(Assets.startMenuBg, 0, 0, this.game.getWidth(), this.game.getHeight(), null);

        g.setColor(menuColor);
        g.setFont(new Font("Arial", Font.BOLD, textSize));
        g.drawString("Introdu numele: ", labelX, labelY);

        nameInput.Draw(g);
        quitBtn.Draw(g, menuColor, textSize);
    }
}
