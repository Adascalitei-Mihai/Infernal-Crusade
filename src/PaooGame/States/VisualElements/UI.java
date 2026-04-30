package PaooGame.States.VisualElements;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class UI {
    private final Handler handler;

    private final int xHealth = 20, yHealth = 20;
    private final int xCdStart = 20, yCd = 100;

    private final int xRankCenter = 300, yRankCenter = 50;

    private final String[] rankLetters = {"D", "C", "B", "A", "S"};

    public UI(Handler handler) {
        this.handler = handler;
    }

    public void Update() {
    }

    public void Draw(Graphics g) {
        if (handler.getDante() == null) return;

        drawHealthBar(g);
        drawRank(g);
        if(handler.getCurrLevel() == 3) {
            drawCooldowns(g);
        }
    }

    private void drawHealthBar(Graphics g) {
        float hp = handler.getDante().getHp();
        float maxHp = Dante.MAX_HP;

        if (maxHp <= 0) return;

        int barIndex = getBarIndex(hp, maxHp);

        if (barIndex > 0 && barIndex < Assets.uiHealth.length) {
            int fillWidth = Assets.uiHealth[barIndex].getWidth() * 4;
            int fillHeight = Assets.uiHealth[barIndex].getHeight() * 4;
            g.drawImage(Assets.uiHealth[barIndex], xHealth, yHealth, fillWidth, fillHeight, null);
        }

        if (Assets.uiHealth[0] != null) {
            int borderWidth = Assets.uiHealth[0].getWidth() * 4;
            int borderHeight = Assets.uiHealth[0].getHeight() * 4;
            g.drawImage(Assets.uiHealth[0], xHealth, yHealth, borderWidth, borderHeight, null);
        }
    }

    private static int getBarIndex(float hp, float maxHp) {
        float hpPercent =  hp / maxHp;
        int barIndex = 0;

        if (hpPercent >= 0.85f) { barIndex = 1; }
        else if (hpPercent >= 0.68f) { barIndex = 2; }
        else if (hpPercent >= 0.51f) { barIndex = 3; }
        else if (hpPercent >= 0.34f) { barIndex = 4; }
        else if (hpPercent >= 0.17f) { barIndex = 5; }
        else if (hpPercent > 0.0f) { barIndex = 6; }

        return barIndex;
    }

    private void drawCooldowns(Graphics g) {
        float cooldownProgress = handler.getDante().getCooldownProgress();
        int frameIndex = (int) (cooldownProgress * (Assets.uiCd.length - 1));

        if (frameIndex < 0) frameIndex = 0;
        if (frameIndex >= Assets.uiCd.length) frameIndex = Assets.uiCd.length - 1;

        if (Assets.uiCd[frameIndex] != null) {
            int cdWidth = Assets.uiCd[frameIndex].getWidth() * 2;
            int cdHeight = Assets.uiCd[frameIndex].getHeight() * 2;
            g.drawImage(Assets.uiCd[frameIndex], xCdStart, yCd, cdWidth, cdHeight, null);
        }
    }

    private void drawRank(Graphics g) {
        int rankOrdinal = handler.getDante().getCurrRank().ordinal();

        if (Assets.uiRanks != null && rankOrdinal < Assets.uiRanks.length && Assets.uiRanks[rankOrdinal] != null) {
            int rankWidth = Assets.uiRanks[rankOrdinal].getWidth() * 2;
            int rankHeight = Assets.uiRanks[rankOrdinal].getHeight() * 2;

            int drawX = xRankCenter - (rankWidth / 2);
            int drawY = yRankCenter - (rankHeight / 2);

            g.drawImage(Assets.uiRanks[rankOrdinal], drawX, drawY, rankWidth, rankHeight, null);

            if (rankOrdinal >= 0 && rankOrdinal <= 4) {
                String letter = rankLetters[rankOrdinal];
                g.setFont(new Font("Monospaced", Font.BOLD, 40));

                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(letter);
                int textAscent = fm.getAscent();
                int textDescent = fm.getDescent();

                int textX = xRankCenter - (textWidth / 2);
                int textY = yRankCenter + (textAscent / 2) - (textDescent / 2);

                g.setColor(Color.BLACK);
                g.drawString(letter, textX + 2, textY + 2);

                g.setColor(Color.WHITE);
                g.drawString(letter, textX, textY);
            }
        }
    }
}