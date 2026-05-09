package PaooGame.States;

import PaooGame.Audio.SongAssets;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.States.VisualElements.Button;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;

/**
 * Clasa care reprezinta meniul de Controls
 */
public class ControlsMenu extends State {
    private final Button returnBtn;
    private int btnX;
    private final int btnY;

    private final static int BOX_WIDTH = 300;
    private final static int BOX_HEIGHT = 40;
    private final static Color TEXT_COLOR = new Color(228, 214, 167);
    private final static int TEXT_SIZE = 40;

    /**
     * Constructorul care creeaza {@link #returnBtn} si porneste melodia {@link SongAssets#menu}
     * @param handler Handler-ul
     */
    public ControlsMenu(Handler handler) {
        super(handler);

        btnX = handler.getWidth() - BOX_WIDTH - 10;
        btnY = 10;

        returnBtn = new Button(btnX, btnY, BOX_WIDTH, BOX_HEIGHT, new String[]{"Return (ESC)"});

        AudioManager.playBGM(SongAssets.menu);
    }

    /**
     * Metoda de actualizare care verifica interactiunea cu butonul sau tasta ESC pentru a realiza tranzitia spre {@link MainMenu}
     */
    @Override
    public void Update() {
        btnX = handler.getWidth() - BOX_WIDTH - 10;
        returnBtn.setPosition(btnX, btnY);

        returnBtn.checkHover(
                handler.getMouseManager().getMouseX(),
                handler.getMouseManager().getMouseY()
        );

        if (returnBtn.getHovered()) {
            handler.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (handler.getMouseManager().isLeftReleased()) {
                returnToMainMenu();
            }
        } else {
            handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (handler.getKeyManager().escape) {
            handler.getKeyManager().escape = false;
            returnToMainMenu();
        }
    }

    /**
     * Metoda utilitara pentru a evita duplicarea codului de tranzitie si a reseta cursorul
     */
    private void returnToMainMenu() {
        handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        handler.getTransitionManager().start(() -> {
            String menuText = (handler.getCurrLevel() == 1 && handler.getCurrChkp() == 0) ? "New Game" : "Continue";
            handler.setState(new MainMenu(handler, menuText));
        });
    }

    /**
     * Metoda de desenare, afiseaza fundalul din {@link VisualAssets#controlsMenuBg} si butonul
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        g.drawImage(VisualAssets.controlsMenuBg, 0, 0, handler.getWidth(), handler.getHeight(), null);
        returnBtn.Draw(g, TEXT_COLOR, TEXT_SIZE);
    }
}