package PaooGame.VisualElements;

import java.awt.*;

/**
 * Clasa care implementeaza o caseta text.
 */
public class TextBox {
    private int x, y;
    private final int width, height;
    private String text = "";
    private boolean isFocused = false;
    private final static int MAX_LENGTH = 10;

    /**
     * Constructor cu parametrii.
     * @param x Pozitia pe axa Ox.
     * @param y Pozitia pe axa Oy.
     * @param width Latimea casetei.
     * @param height Inaltimea casetei.
     */
    public TextBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Modifica pozitia casetei
     * @param x Noua pozitie pe axa Ox.
     * @param y Noua pozitie pe axa Oy.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Functie care verifica daca coordonatele sunt in interiorul cutiei.
     * @param mouseX Coordonata pe Ox.
     * @param mouseY Coordonata pe Oy.
     */
    public void checkClick(int mouseX, int mouseY) {
        this.isFocused = (mouseX >= this.x && mouseX <= this.x + this.width) &&
                (mouseY >= this.y && mouseY <= this.y + this.height);
    }

    /**
     * Functie care adauga sau sterge caractere. <br>
     * Este activa doar daca caseta este atinsa.
     * @param c Caracter primit de la KeyManager.
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
     * Functie care deseneaza caseta text, aceasta: <br>
     * 1. Modifica interiorul in functie de apasarea casetei. <br>
     * 2. Deseneaza si umple dreptunghiul. <br>
     * 3. Deseneaza textul actual. <br>
     * 4. Creeaza o bara care pulseaza in dreptul ultimei litere.
     * @param g Grafica jocului.
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

        g.setFont(new Font("Arial", Font.BOLD, 24)); // 3
        g.drawString(text, x + 10, y + 35); // un mic offset

        if (isFocused && (System.currentTimeMillis() / 500) % 2 == 0) { // 4
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawLine(x + 12 + textWidth, y + 10, x + 12 + textWidth, y + 40);
        }
    }

    /**
     * Getter pentru text.
     * @return Textul actual.
     */
    public String getText() { return text; }
}