package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.VisualElements.TextBox;

import java.awt.*;

/**
 * Clasa meniului de inceput, este una dintre starile posibile.
 */
public class StartMenu extends State {
    private final TextBox nameInput;
    private final static int BOX_WIDTH = 300;
    private final static int BOX_HEIGTH = 50;
    private int labelX, labelY;

    /**
     * Constructor care primeste jocul si creeaza o caseta text.
     * @param game Instanta jocului actual.
     */
    public StartMenu(Game game) {
        super(game);
        nameInput = new TextBox(0, 0, BOX_WIDTH, BOX_HEIGTH);
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

        if(this.game.getMouseManager().isLeftPressed()) {
            nameInput.checkClick(
                    this.game.getMouseManager().getMouseX(),
                    this.game.getMouseManager().getMouseY()
            );
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

        g.setColor(new Color(255, 140, 0));
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Introdu numele: ", labelX, labelY);

        nameInput.Draw(g);
    }
}
