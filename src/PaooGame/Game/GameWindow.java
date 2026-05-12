package PaooGame.Game;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Clasa care implementeaza fereastra jocului
 */
public class GameWindow {
    private JFrame wndFrame;
    private final String wndTitle;
    private final int wndWidth;
    private final int wndHeight;

    private Canvas  canvas;

    /**
     * Constructor care initializeaza fereasta
     * @param title Titlul ferestrei
     * @param width Latimea ferestrei
     * @param height Inaltimea ferestrei
     */
    public GameWindow(String title, int width, int height) {
        wndTitle = title;
        wndWidth = width;
        wndHeight = height;
        wndFrame = null;
    }

    /**
     * Metoda care seteaza fereastra sa fie fullscreen
     * @param fullscreen Starea ceruta
     */
    public void setFullScreen(boolean fullscreen) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (fullscreen) {
            if (gd.isFullScreenSupported()) {
                wndFrame.dispose();
                wndFrame.setUndecorated(true);
                gd.setFullScreenWindow(wndFrame);
                wndFrame.setVisible(true);

                canvas.setFocusable(true);
                canvas.requestFocusInWindow();
            }
        } else {
            gd.setFullScreenWindow(null);
            wndFrame.dispose();
            wndFrame.setUndecorated(false);
            wndFrame.setVisible(true);
            wndFrame.setLocationRelativeTo(null);
        }
        canvas.requestFocus();
    }

    /**
     * Metoda care seteaza fereastra sa fie windowed
     */
    public void setWindowed() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (gd.getFullScreenWindow() != null) {
            gd.setFullScreenWindow(null);
            wndFrame.dispose();
            wndFrame.setUndecorated(false);
            wndFrame.setPreferredSize(new Dimension(wndWidth, wndHeight));
            wndFrame.pack();
            wndFrame.setLocationRelativeTo(null);
            wndFrame.setVisible(true);

            canvas.setFocusable(true);
            canvas.requestFocusInWindow();

            hideCursor(false);
        }
    }

    /**
     * Metoda care ascunde cursorul
     * @param hide Starea ceruta
     */
    public void hideCursor(boolean hide) {
        if (canvas == null) return;

        if (hide) {
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor");
            canvas.setCursor(blankCursor);
        } else {
            canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Metoda care construieste fereastra <br>
     * 1. Verifica sa nu creeze un duplicat <br>
     * 2. Creeaza JFrame-ul aferent <br>
     * 3. Il instantiaza cu o anumita dimensiune <br>
     * 4. Opreste afisarea prin apasarea X <br>
     * 5. Isi poate schimba dimensiunea <br>
     * 6. Se deschide in mijlocul ecranului <br>
     * 7. O face vizibila <br>
     * 8. Creeaza panza de desen <br>
     * 9. Il instantiaza cu dimensiuni preferate, maxime si minime <br>
     * 10. Adauga panza la fereastra actuala <br>
     * 11. Redimensioneaza fereastra astfel incat tot continutul sa incapa perfect
     */
    public void BuildGameWindow()
    {

        if(wndFrame != null) { // 1
            return;
        }
        wndFrame = new JFrame(wndTitle); // 2
        wndFrame.setSize(wndWidth, wndHeight); // 3
        wndFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 4
        wndFrame.setResizable(true); // 5
        wndFrame.setLocationRelativeTo(null); // 6
        wndFrame.setVisible(true); // 7

        canvas = new Canvas(); // 8
        canvas.setPreferredSize(new Dimension(wndWidth, wndHeight)); // 9
        canvas.setMaximumSize(new Dimension(wndWidth, wndHeight));
        canvas.setMinimumSize(new Dimension(wndWidth, wndHeight));
        wndFrame.add(canvas); // 10
        wndFrame.pack(); // 11
    }

    public int getWndWidth() { return canvas.getWidth(); }
    public int getWndHeight() { return canvas.getHeight(); }
    public JFrame getFrame() {return wndFrame; }
    public Canvas getCanvas() { return canvas; }
}
