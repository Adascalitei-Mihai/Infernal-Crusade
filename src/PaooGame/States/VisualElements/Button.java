package PaooGame.States.VisualElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Clasa care implementeaza un buton
 */
public class Button {
    private int x, y;
    private final int width, height;

    /**
     * Campul care este un vector reprezentand toate starile posibile, selectabile prin apasari repetate
     */
    private final String[] textStates;

    /**
     * Campul care este indexul din {@link #textStates}
     */
    private int state;

    /**
     * Campul care indica faptul ca butonul este selectat
     */
    private boolean isHovered;

    /**
     * Constructorul care initializeaza campurile si seteaza butonul in prima stare
     * @param x Pozitia pe Ox
     * @param y Pozitia pe Oy
     * @param width Latimea butonului
     * @param height Inaltimea butonului
     * @param textStates Starile posibile
     */
    public Button(int x, int y, int width, int height, String[] textStates) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        state = 0;
        this.textStates = textStates;
    }

    /**
     * Metoda care modifica pozitia butonului
     * @param x Noua pozitie pe Ox
     * @param y Noua pozitie pe Oy
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Metoda care asculta daca butonul este selectat
     * @param mouseX Pozitia mouse-ului pe Ox
     * @param mouseY Pozitia mouse-ului pe Oy
     */
    public void checkHover(int mouseX, int mouseY) {
        isHovered = (mouseX >= x && mouseX <= x + width) &&
                (mouseY >= y && mouseY <= y + height);
    }

    /**
     * Metoda care modifica starea daca butonul este selectat
     */
    public void changeState() {
        if(isHovered) {
            state = (state + 1) % textStates.length;
        }
    }

    /**
     * Metoda de desenare, afiseaza starea curenta
     * @param g Grafica de desenare
     * @param c Culoarea aleasa
     * @param size Dimeansiunea textului
     */
    public void Draw(Graphics g, Color c, int size) {
        g.setColor(c);
        g.setFont(new Font("Monospaced", Font.BOLD, size));

        FontMetrics fm = g.getFontMetrics();
        String text = getText();

        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();

        g.drawString(text, textX, textY);
    }

    public String getText() { return textStates[state]; }
    public int getState() { return state; }
    public boolean getHovered() { return isHovered; }
}
