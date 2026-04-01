package PaooGame.Graphics;

import javax.swing.*;
import java.awt.*;

/**
 * Clasa care implementeaza fereastra jocului.
 */
public class GameWindow {
    private JFrame wndFrame;
    private final String wndTitle;
    private final int wndWidth;
    private final int wndHeight;

    private Canvas  canvas;

    /**
     * Constructor cu parametrii.
     * @param title Titlul ferestrei.
     * @param width Latimea ferestrei.
     * @param height Inaltimea ferestrei.
     */
    public GameWindow(String title, int width, int height) {
        this.wndTitle    = title;
        this.wndWidth    = width;
        this.wndHeight   = height;
        this.wndFrame    = null;
    }

    /**
     * Functie care construieste fereastra, aceasta: <br>
     * 1. Verifica sa nu creeze un duplicat. <br>
     * 2. Creeaza JFrame-ul aferent. <br>
     * 3. Il instantiaza cu o anumita dimensiune. <br>
     * 4. Opreste afisarea prin apasarea X. <br>
     * 5. Isi poate schimba dimensiunea. <br>
     * 6. Se deschide in mijlocul ecranului. <br>
     * 7. O face vizibila. <br>
     * 8. Creeaza panza de desen. <br>
     * 9. Il instantiaza cu dimensiuni preferate, maxime si minime. <br>
     * 10. Adauga panza la fereastra actuala. <br>
     * 11. Redimensioneaza fereastra astfel incat tot continutul sa incapa perfect. <br>
     */
    public void BuildGameWindow()
    {

        if(this.wndFrame != null) { // 1
            return;
        }
        this.wndFrame = new JFrame(this.wndTitle); // 2
        this.wndFrame.setSize(this.wndWidth, this.wndHeight); // 3
        this.wndFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 4
        this.wndFrame.setResizable(true); // 5
        this.wndFrame.setLocationRelativeTo(null); // 6
        this.wndFrame.setVisible(true); // 7

        this.canvas = new Canvas(); // 8
        this.canvas.setPreferredSize(new Dimension(this.wndWidth, this.wndHeight)); // 9
        this.canvas.setMaximumSize(new Dimension(this.wndWidth, this.wndHeight)); 
        this.canvas.setMinimumSize(new Dimension(this.wndWidth, this.wndHeight));
        this.wndFrame.add(this.canvas); // 10
        this.wndFrame.pack(); // 11
    }

    /**
     * Getter pentru latimea ferestrei.
     * @return Latimea actuala.
     */
    public int getWndWidth() { return canvas.getWidth(); }

    /**
     * Getter pentru inaltimea ferestrei.
     * @return Inaltimea actuala.
     */
    public int getWndHeight() { return canvas.getHeight(); }

    /**
     * Getter pentru fereastra.
     * @return Fereastra actuala.
     */
    public JFrame getFrame() {return wndFrame; }

    /**
     * Getter pentru panza ferestrei.
     * @return Panza actuala.
     */
    public Canvas getCanvas() { return canvas; }
}
