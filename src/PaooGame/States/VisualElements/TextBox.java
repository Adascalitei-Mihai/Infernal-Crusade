package PaooGame.States.VisualElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Clasa care implementeaza o caseta text
 */
public class TextBox {
    private int x, y;
    private final int width, height;

    /**
     * Campul care este textul curent
     */
    private String text = "";
    /**
     * Campul care indica faptul ca butonul este selectat
     */
    private boolean isFocused = false;

    private final static int MAX_LENGTH = 10;

    /**
     * Constructorul care initializeaza dimensiunile casetei
     * @param x Pozitia pe Ox
     * @param y Pozitia pe Oy
     * @param width Latimea casetei
     * @param height Inaltimea casetei
     */
    public TextBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Metoda care modifica pozitia casetei
     * @param x Noua pozitie pe Ox
     * @param y Noua pozitie pe Oy
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Metoda care asculta daca caseta este selectata
     * @param mouseX Pozitia mouse-ului pe Ox
     * @param mouseY Pozitia mouse-ului pe Oy
     */
    public void checkHover(int mouseX, int mouseY) {
        isFocused = (mouseX >= x && mouseX <= x + width) &&
                (mouseY >= y && mouseY <= y + height);
    }

    /**
     * Metoda care adauga (pana la {@link #MAX_LENGTH}) sau sterge caractere daca caseta este selectata
     * @param c Caracter primit de la KeyManager
     */
    public void addChar(char c) {
        if (!isFocused) return;

        if (c == '\b' && !text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
        }
        else if (text.length() < MAX_LENGTH && Character.isLetterOrDigit(c)) {
            text += c;
        }
    }

    /**
     * Metoda de desenare, afiseaza textul curent, creeaza bara care pulseaza in dreptul ultimei litere si isi modifica fundalul daca este selectata
     * @param g Grafica de desenare
     */
    public void Draw(Graphics g) {
        if (isFocused) { // 1
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }

        g.fillRect(x, y, width, height); // 2
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        g.setFont(new Font("Monospaced", Font.BOLD, 24)); // 3
        g.drawString(text, x + 10, y + 35); // un mic offset

        if (isFocused && (System.currentTimeMillis() / 500) % 2 == 0) { // 4
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawLine(x + 12 + textWidth, y + 10, x + 12 + textWidth, y + 40);
        }
    }

    public String getText() { return text; }
    public boolean getFocus() { return isFocused; }
}