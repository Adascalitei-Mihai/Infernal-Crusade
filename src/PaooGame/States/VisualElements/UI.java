package PaooGame.States.VisualElements;

import PaooGame.Graphics.VisualAssets;
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

        if (barIndex > 0 && barIndex < VisualAssets.uiHealth.length) {
            int fillWidth = VisualAssets.uiHealth[barIndex].getWidth() * 4;
            int fillHeight = VisualAssets.uiHealth[barIndex].getHeight() * 4;
            g.drawImage(VisualAssets.uiHealth[barIndex], xHealth, yHealth, fillWidth, fillHeight, null);
        }

        if (VisualAssets.uiHealth[0] != null) {
            int borderWidth = VisualAssets.uiHealth[0].getWidth() * 4;
            int borderHeight = VisualAssets.uiHealth[0].getHeight() * 4;
            g.drawImage(VisualAssets.uiHealth[0], xHealth, yHealth, borderWidth, borderHeight, null);
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
        int frameIndex = (int) (cooldownProgress * (VisualAssets.uiCd.length - 1));

        if (frameIndex < 0) frameIndex = 0;
        if (frameIndex >= VisualAssets.uiCd.length) frameIndex = VisualAssets.uiCd.length - 1;

        if (VisualAssets.uiCd[frameIndex] != null) {
            int cdWidth = VisualAssets.uiCd[frameIndex].getWidth() * 2;
            int cdHeight = VisualAssets.uiCd[frameIndex].getHeight() * 2;
            g.drawImage(VisualAssets.uiCd[frameIndex], xCdStart, yCd, cdWidth, cdHeight, null);
        }
    }

    private void drawRank(Graphics g) {
        int rankOrdinal = handler.getDante().getCurrRank().ordinal();

        if (VisualAssets.uiRanks != null && rankOrdinal < VisualAssets.uiRanks.length && VisualAssets.uiRanks[rankOrdinal] != null) {
            int rankWidth = VisualAssets.uiRanks[rankOrdinal].getWidth() * 2;
            int rankHeight = VisualAssets.uiRanks[rankOrdinal].getHeight() * 2;

            int drawX = xRankCenter - (rankWidth / 2);
            int drawY = yRankCenter - (rankHeight / 2);

            g.drawImage(VisualAssets.uiRanks[rankOrdinal], drawX, drawY, rankWidth, rankHeight, null);

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