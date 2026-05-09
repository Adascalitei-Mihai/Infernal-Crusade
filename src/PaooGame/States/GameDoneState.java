package PaooGame.States;

import PaooGame.Handler;
import PaooGame.States.VisualElements.Button;

import java.awt.*;

public class GameDoneState extends State {
    private final Button returnBtn;
    private final int finalScore;

    public GameDoneState(Handler handler) {
        super(handler);

        // Preia scorul total actualizat din baza de date
        this.finalScore = handler.getDbMan().getTotalBestScore(handler.getPlayerID(), Handler.NR_CHKPTS);

        // Setăm dimensiunile butonului
        int btnWidth = 350;
        int btnHeight = 40;
        int btnX = (handler.getWidth() - btnWidth) / 2;
        int btnY = handler.getHeight() / 2 + 100;

        returnBtn = new Button(btnX, btnY, btnWidth, btnHeight, new String[]{"Return to Menu"});
    }

    @Override
    public void Update() {
        int mx = handler.getMouseManager().getMouseX();
        int my = handler.getMouseManager().getMouseY();

        // Verificăm dacă mouse-ul este pe buton
        returnBtn.checkHover(mx, my);

        if (returnBtn.getHovered()) {
            handler.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Dacă dăm click, mergem în MainMenu
            if (handler.getMouseManager().isLeftReleased()) {
                handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                handler.getTransitionManager().start(() -> {
                    handler.setState(new MainMenu(handler, "Start Game"));
                });
            }
        } else {
            handler.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if(handler.getInputManager().escape) {
            handler.getGame().getGameCamera().resetToLevelBounds();
            handler.getGame().getWnd().setWindowed();
            handler.getInputManager().reset();

            handler.setState(new MainMenu(handler, "Continue"));
        }
    }

    @Override
    public void Draw(Graphics g) {
        // Fundal întunecat
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        // Desenăm "CONGRATULATIONS!"
        g.setColor(new Color(228, 214, 167)); // Culoarea pe care o folosești și în meniu
        g.setFont(new Font("Monospaced", Font.BOLD, 60));

        String congratsText = "CONGRATULATIONS!";
        FontMetrics fm = g.getFontMetrics();
        int cX = (handler.getWidth() - fm.stringWidth(congratsText)) / 2;
        g.drawString(congratsText, cX, handler.getHeight() / 2 - 50);

        // Desenăm Scorul Total
        g.setFont(new Font("Monospaced", Font.PLAIN, 40));
        String scoreText = "Total Score: " + finalScore;
        fm = g.getFontMetrics();
        int sX = (handler.getWidth() - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, sX, handler.getHeight() / 2 + 20);

        // Desenăm butonul
        returnBtn.Draw(g, new Color(228, 214, 167), 30);
    }
}