package PaooGame.States.VisualElements;

import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Boss;
import PaooGame.World.Levels.Level;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Clasa care afiseaza informatiile relevante jucatoului
 */
public class UI {
    private final Handler handler;
    private final String[] rankLetters = {"D", "C", "B", "A", "S"};

    /**
     * Constructorul care initializeaza handler-ul
     * @param handler Handler-ul
     */
    public UI(Handler handler) { this.handler = handler; }

    /**
     * Metoda de desenare, apeleaza {@link #drawHealthBar(Graphics)} si {@link #drawRank(Graphics)} <br>
     * In functie de tipul arenei in care se afla ruleaza {@link #drawWaveText(Graphics)} sau {@link #drawBossHealthBar(Graphics)}
     * @param g Grafica de desenare
     */
    public void Draw(Graphics g) {
        if (handler.getDante() == null) return;

        drawHealthBar(g);
        drawRank(g);

        if (handler.getCurrLevel() == 3) {
            drawCooldowns(g);
        }

        if (handler.getLevel() != null && handler.getLevel().inArena()) {
            if (handler.getCurrLevel() == 1 || handler.getCurrLevel() == 2) {
                drawWaveText(g);
            } else if (handler.getCurrLevel() == 3) {
                drawBossHealthBar(g);
            }
        }
    }

    /**
     * Meroda care afiseaza, impartita in 5 segmente, viata boss-ului (folosind {@link Level#getBoss()}), prin determinarea procentului de viata actual din cel final
     * @param g Grafica de desenare
     */
    private void drawBossHealthBar(Graphics g) {
        Boss boss = handler.getLevel().getBoss();

        if (boss == null || !boss.isActive()) {
            return;
        }

        int barWidth = 600;
        int barHeight = 25;
        int barX = (handler.getWidth() - barWidth) / 2;
        int barY = handler.getHeight() - 60;

        g.setColor(new Color(40, 40, 40, 200));
        g.fillRect(barX, barY, barWidth, barHeight);

        float hpPercent = boss.getHp() / Boss.MAX_HP;
        if (hpPercent < 0) hpPercent = 0;
        int fillWidth = (int) (barWidth * hpPercent);
        g.setColor(new Color(220, 20, 20));
        g.fillRect(barX, barY, fillWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        int segments = 5;
        int segmentWidth = barWidth / segments;

        g.setColor(new Color(255, 255, 255, 100));
        for (int i = 1; i < segments; i++) {
            int lineX = barX + (i * segmentWidth);
            g.drawLine(lineX, barY, lineX, barY + barHeight);
        }

        String bossName = "Beelzebub";
        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(bossName);

        int textX = (handler.getWidth() - textWidth) / 2;
        int textY = barY - 10;

        g.setColor(Color.BLACK);
        g.drawString(bossName, textX + 2, textY + 2);

        g.setColor(Color.WHITE);
        g.drawString(bossName, textX, textY);
    }

    /**
     * Metoda care afiseaza wave-ul curent din arena (folosind {@link Level#getCurrWave()})
     * @param g Grafica de desenare
     */
    private void drawWaveText(Graphics g) {
        String waveText = "Wave: " + (handler.getLevel().getCurrWave() + 1);

        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(waveText);

        int textX = (handler.getWidth() - textWidth) / 2;
        int textY = handler.getHeight() - 50;

        g.setColor(Color.BLACK);
        g.drawString(waveText, textX + 3, textY + 3);

        g.setColor(Color.WHITE);
        g.drawString(waveText, textX, textY);
    }

    /**
     * Metoda de desenare a vietii lui Dante salvate in {@link VisualAssets#uiHealth} cu ajutorul {@link #getBarIndex(float, float)} si {@link Handler#getDante()}
     * @param g Grafica de desenare
     */
    private void drawHealthBar(Graphics g) {
        float hp = handler.getDante().getHp();
        float maxHp = Dante.MAX_HP;

        int barIndex = getBarIndex(hp, maxHp);

        int xHealth = 20;
        int yHealth = 20;
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

    /**
     * Metoda care afla cadrul corect prin calcularea procentului de viata actual din cel final
     * @param hp Viata actuala
     * @param maxHp Viata totala
     * @return Indexul cadrului
     */
    private static int getBarIndex(float hp, float maxHp) {
        float hpPercent =  hp / maxHp;
        int barIndex = 0;

        if (hpPercent >= 0.85f) {
            barIndex = 1;
        } else if (hpPercent >= 0.68f) {
            barIndex = 2;
        } else if (hpPercent >= 0.51f) {
            barIndex = 3;
        } else if (hpPercent >= 0.34f) {
            barIndex = 4;
        } else if (hpPercent >= 0.17f) {
            barIndex = 5;
        } else if (hpPercent > 0.0f) {
            barIndex = 6;
        }

        return barIndex;
    }

    /**
     * Metoda de desenare a CD salvat in {@link VisualAssets#uiCd} cu ajutorul {@link Dante#getCooldownProgress()}
     * @param g Grafica de desenare
     */
    private void drawCooldowns(Graphics g) {
        float cooldownProgress = handler.getDante().getCooldownProgress();
        int frameIndex;
        
        if (cooldownProgress >= 1.0f) {
            frameIndex = VisualAssets.uiCd.length - 1;
        } else {
            frameIndex = (int) (cooldownProgress * (VisualAssets.uiCd.length - 1));
            
            if (frameIndex >= VisualAssets.uiCd.length - 1) {
                frameIndex = VisualAssets.uiCd.length - 2;
            }
        }
        
        if (frameIndex < 0) frameIndex = 0;

        if (VisualAssets.uiCd[frameIndex] != null) {
            int cdWidth = VisualAssets.uiCd[frameIndex].getWidth() * 2;
            int cdHeight = VisualAssets.uiCd[frameIndex].getHeight() * 2;
            int xCdStart = 20;
            int yCd = 100;
            g.drawImage(VisualAssets.uiCd[frameIndex], xCdStart, yCd, cdWidth, cdHeight, null);
        }
    }

    /**
     * Metoda de desenare a rank-ului curent salvat in {@link VisualAssets#uiRanks} cu ajutorul {@link Dante#getCurrRank()}
     * @param g Grafica de desenare
     */
    private void drawRank(Graphics g) {
        int rankOrdinal = handler.getDante().getCurrRank().ordinal();

        if (VisualAssets.uiRanks != null && rankOrdinal < VisualAssets.uiRanks.length && VisualAssets.uiRanks[rankOrdinal] != null) {
            int rankWidth = VisualAssets.uiRanks[rankOrdinal].getWidth() * 2;
            int rankHeight = VisualAssets.uiRanks[rankOrdinal].getHeight() * 2;

            int xRankCenter = 300;
            int drawX = xRankCenter - (rankWidth / 2);
            int yRankCenter = 50;
            int drawY = yRankCenter - (rankHeight / 2);

            g.drawImage(VisualAssets.uiRanks[rankOrdinal], drawX, drawY, rankWidth, rankHeight, null);

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