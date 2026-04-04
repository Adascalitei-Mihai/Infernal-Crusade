package PaooGame.Game.Input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Clasa care traduce actiunile mouse-ului.
 */
public class MouseManager implements MouseListener, MouseMotionListener {

    private boolean leftPressed, rightPressed;
    private boolean leftReleased;
    private int mouseX, mouseY;

    /**
     * Constructor vid.
     */
    public MouseManager() {
    }

    /**
     * Getter pentru verificarea apasarii butonului din stanga.
     * @return Adevarat / Fals.
     */
    public boolean isLeftPressed() { return leftPressed; }

    public boolean isLeftReleased() {
        if (leftReleased) {
            leftReleased = false;
            return true;
        }
        return false;
    }
    /**
     * Getter pentru verificarea apasarii butonului din dreapta.
     * @return Adevarat / Fals.
     */
    public boolean isRightPressed() { return rightPressed; }

    /**
     * Getter pentru pozitia pe Ox a cursorului.
     * @return Pozitia actuala.
     */
    public int getMouseX() { return mouseX; }

    /**
     * Getter pentru pozitia pe Oy a cursorului.
     * @return Pozitia actuala.
     */
    public int getMouseY() { return mouseY; }

    /**
     * Funtie care determina apasarea unui buton.
     * @param e Evenimentul procesat.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = true;
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            rightPressed = true;
        }
    }

    /**
     * Functie care determina ridicarea unui buton.
     * @param e Evenimentul procesat.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = false;
            leftReleased = true;
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            rightPressed = false;
        }
    }

    /**
     * Functie care determina miscarea mouse-ului.
     * @param e Evenimentul procesat.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Functie care determina tinerea apasata a mouse-ului.
     * @param e Evenimentul procesat.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // Functii necesare, dar pe care nu le vom implementa.
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}