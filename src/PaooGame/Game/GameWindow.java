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

    private Canvas canvas;

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
            setWindowed();
        }
        canvas.requestFocus();
    }

    /**
     * Metoda care seteaza fereastra sa fie windowed
     */
    public void setWindowed() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (gd.getFullScreenWindow() != null || wndFrame.isUndecorated()) {
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
     * 2. Creeaza JFrame-ul aferent si opreste Repaint-ul automat <br>
     * 3. Creeaza panza de desen si opreste Repaint-ul automat <br>
     * 4. Asambleaza componentele si face fereastra vizibila
     */
    public void BuildGameWindow()
    {
        if(wndFrame != null) {
            return;
        }

        wndFrame = new JFrame(wndTitle);
        wndFrame.setSize(wndWidth, wndHeight);
        wndFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wndFrame.setResizable(false);
        wndFrame.setLocationRelativeTo(null);

        wndFrame.setIgnoreRepaint(true); // 2

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(wndWidth, wndHeight));
        canvas.setMaximumSize(new Dimension(wndWidth, wndHeight));
        canvas.setMinimumSize(new Dimension(wndWidth, wndHeight));

        canvas.setIgnoreRepaint(true); // 3
        canvas.setFocusable(true);

        wndFrame.add(canvas);
        wndFrame.pack();

        wndFrame.setVisible(true); // 4
    }

    public int getWndWidth() { return canvas.getWidth(); }
    public int getWndHeight() { return canvas.getHeight(); }
    public JFrame getFrame() {return wndFrame; }
    public Canvas getCanvas() { return canvas; }
}