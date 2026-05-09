package PaooGame.States.VisualElements;

import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Boss;

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

        // Verificam conditiile pentru Arena
        if (handler.getLevel() != null && handler.getLevel().inArena()) {
            if (handler.getCurrLevel() == 1 || handler.getCurrLevel() == 2) {
                drawWaveText(g);
            }
            else if (handler.getCurrLevel() == 3) {
                drawBossHealthBar(g);
            }
        }
    }

    private void drawBossHealthBar(Graphics g) {
        // Presupunem ca in clasa Level ai facut o metoda getBoss() care returneaza boss-ul
        // Daca nu ai metoda, va trebui sa o adaugi in Level3/Level (ex: public Boss getBoss() { return bossEntity; })
        Boss boss = handler.getLevel().getBoss();

        if (boss == null || !boss.isActive()) return; // Nu desenam daca boss-ul nu exista sau a murit definitiv

        // Dimensiunile si pozitia barei
        int barWidth = 600;
        int barHeight = 25;
        int barX = (handler.getWidth() - barWidth) / 2;
        int barY = handler.getHeight() - 60; // Plasam bara in partea de jos

        // Desenam fundalul (gri inchis/negru)
        g.setColor(new Color(40, 40, 40, 200));
        g.fillRect(barX, barY, barWidth, barHeight);

        // Calculam latimea portiunii de viata in functie de HP-ul curent
        float hpPercent = boss.getHp() / Boss.MAX_HP;
        if (hpPercent < 0) hpPercent = 0;
        int fillWidth = (int) (barWidth * hpPercent);

        // Desenam viata cu rosu aprins
        g.setColor(new Color(220, 20, 20));
        g.fillRect(barX, barY, fillWidth, barHeight);

        // Desenam chenarul exterior al barei
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        // Desenam segmentarea (5 segmente => 4 linii despartitoare)
        int segments = 5;
        int segmentWidth = barWidth / segments;

        g.setColor(new Color(255, 255, 255, 100)); // Linii albe semitransparente
        for (int i = 1; i < segments; i++) {
            int lineX = barX + (i * segmentWidth);
            // Trasăm o linie verticală care taie bara
            g.drawLine(lineX, barY, lineX, barY + barHeight);
        }

        // Desenam numele Boss-ului deasupra barei
        String bossName = "Beelzebub";
        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(bossName);

        int textX = (handler.getWidth() - textWidth) / 2;
        int textY = barY - 10; // Putin deasupra barei

        // Drop shadow pentru nume
        g.setColor(Color.BLACK);
        g.drawString(bossName, textX + 2, textY + 2);

        // Culoarea textului (alb sau un rosu deschis)
        g.setColor(Color.WHITE);
        g.drawString(bossName, textX, textY);
    }

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

    // ... restul metodelor raman la fel (drawHealthBar, getBarIndex, drawCooldowns, drawRank) ...

    private void drawHealthBar(Graphics g) {
        float hp = handler.getDante().getHp();
        float maxHp = Dante.MAX_HP;

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