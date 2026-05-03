package PaooGame.World.Entities.Projectiles;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;
import java.awt.*;
import java.awt.geom.AffineTransform;

import static PaooGame.World.Tile.TILE_SIZE;

public class SoulSpear extends Projectile {
    // Dimensiuni logice (Hitbox-ul real)
    private final static int LOGIC_WIDTH = TILE_SIZE * 2;
    private final static int LOGIC_HEIGHT = TILE_SIZE * 2;

    // Dimensiuni vizuale (De 2 ori mai mari decât hitbox-ul)
    private final static int VISUAL_WIDTH = TILE_SIZE * 4;
    private final static int VISUAL_HEIGHT = TILE_SIZE * 4;

    private final static float SPEED = 14f;
    private final static float DMG = 80f;
    private final static float KNOCK_PERCENT = 2f;

    private Animation animation;

    public SoulSpear(Handler handler, float x, float y, float tx, float ty, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, tx, ty,
                SPEED, owner, DMG, KNOCK_PERCENT);

        // Inițializăm animația. Viteza 10 ar trebui să fie potrivită pentru un proiectil magic rapid.
        this.animation = new Animation(10, VisualAssets.SoulSpear);

        // Ajustăm hitbox-ul să se potrivească cu dimensiunile logice
        this.bounds.x = 0;
        this.bounds.y = 0;
        this.bounds.width = LOGIC_WIDTH;
        this.bounds.height = LOGIC_HEIGHT;
    }

    @Override
    public void Update() {
        super.Update();
        animation.Update();
    }

    @Override
    public void Draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Salvăm transformarea curentă pentru a nu afecta alte obiecte
        AffineTransform oldAT = g2d.getTransform();

        // Calculăm poziția hitbox-ului pe ecran
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        // Centrul rotației trebuie să fie mijlocul hitbox-ului (centrul logic)
        int centerX = screenX + width / 2;
        int centerY = screenY + height / 2;

        // Aplicăm rotația calculată automat din vitezele pe X și Y
        g2d.rotate(getAngle(), centerX, centerY);

        // Calculăm noile coordonate de desenare pentru a centra imaginea mai mare peste hitbox-ul mai mic
        int drawX = screenX + (width - VISUAL_WIDTH) / 2;
        int drawY = screenY + (height - VISUAL_HEIGHT) / 2;

        // Desenăm frame-ul curent din animație folosind dimensiunile VIZUALE
        g2d.drawImage(animation.getCurrentFrame(), drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);

        // Revenim la transformarea inițială ca să nu se rotească și restul ecranului
        g2d.setTransform(oldAT);

        // Hitbox-ul pentru modul Debug (Atenție, acesta desenează dimensiunile logice)
        if (handler.getDebugMode()) {
            g.setColor(new Color(0, 0, 255, 100)); // Albastru transparent
            g.fillRect(screenX, screenY, width, height);
        }
    }
}