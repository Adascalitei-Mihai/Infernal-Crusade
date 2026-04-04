package PaooGame.State.VisualElements;

import java.awt.*;

/**
 * Clasa care implementeaza un buton.
 */
public class Button {
    private int x, y;
    private final int width, height;

    private int state;
    private final String[] textStates;

    private boolean isHovered;

    public Button(int x, int y, int width, int height, String[] textStates) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        state = 0;
        this.textStates = textStates;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void checkHover(int mouseX, int mouseY) {
        isHovered = (mouseX >= x && mouseX <= x + width) &&
                (mouseY >= y && mouseY <= y + height);
    }

    public void changeState() {
        if(isHovered) {
            state = (state + 1) % textStates.length;
        }
    }

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
