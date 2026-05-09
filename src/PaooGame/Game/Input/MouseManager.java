package PaooGame.Game.Input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Clasa care asculta input-urile mouse-ului
 */
public class MouseManager implements MouseListener, MouseMotionListener {
    private boolean leftPressed;
    private boolean leftReleased;
    private int mouseX, mouseY;

    /**
     * Functia care retine daca este apasat MouseBtn1
     * @param e Evenimentul procesat
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = true;
        }
    }

    /**
     * Functia care retine daca este ridicat MouseBtn1
     * @param e Evenimentul procesat
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = false;
            leftReleased = true;
        }
    }

    /**
     * Functia care retine miscarea mouse-ului
     * @param e Evenimentul procesat
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Functia care retine tinerea apasata a mouse-ului
     * @param e Evenimentul procesat
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public boolean isLeftPressed() { return leftPressed; }
    public boolean isLeftReleased() {
        if (leftReleased) {
            leftReleased = false;
            return true;
        }
        return false;
    }

    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }

    /**
     * Functie necesara, neimplementata
     * @param e Evenimentul procesat
     */
    @Override public void mouseClicked(MouseEvent e) {}
    /**
     * Functie necesara, neimplementata
     * @param e Evenimentul procesat
     */
    @Override public void mouseEntered(MouseEvent e) {}
    /**
     * Functie necesara, neimplementata
     * @param e Evenimentul procesat
     */
    @Override public void mouseExited(MouseEvent e) {}
}