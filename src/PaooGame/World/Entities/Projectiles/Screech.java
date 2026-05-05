package PaooGame.World.Entities.Projectiles;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;
import java.awt.*;
import java.awt.geom.AffineTransform;

import static PaooGame.World.Tile.TILE_SIZE;

public class Screech extends Projectile {
    // Dimensiunea Logică (Hitbox-ul care lovește efectiv jucătorul)
    private final static int LOGIC_WIDTH = 2 * TILE_SIZE;
    private final static int LOGIC_HEIGHT = 2 * TILE_SIZE;

    // Dimensiunea Vizuală (Cât de mare desenăm imaginea pentru a compensa spațiul gol din PNG)
    // Deoarece ai spus că vizualul pare la jumătate, am dublat dimensiunile de desenare:
    private final static int VISUAL_WIDTH = 4 * TILE_SIZE;
    private final static int VISUAL_HEIGHT = 4 * TILE_SIZE;

    private final static float SPEED = 10f;
    private final static float DMG = 40f;
    private final static float KNOCK_PERCENT = 1.5f;

    private Animation animation;

    public Screech(Handler handler, float x, float y, float tx, float ty, Creature owner) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, tx, ty,
                SPEED, owner, DMG, KNOCK_PERCENT);

        this.animation = new Animation(5, VisualAssets.Screech);

        // Ajustăm hitbox-ul
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

        AffineTransform oldAT = g2d.getTransform();

        // Poziția hitbox-ului pe ecran
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        // Centrul de rotație trebuie să rămână centrul logic (Hitbox-ul)
        int centerX = screenX + width / 2;
        int centerY = screenY + height / 2;

        // Aplicăm rotația
        g2d.rotate(getAngle(), centerX, centerY);

        // Calculăm noile coordonate de desenare pentru a centra imaginea mai mare peste hitbox-ul mai mic
        int drawX = screenX + (width - VISUAL_WIDTH) / 2;
        int drawY = screenY + (height - VISUAL_HEIGHT) / 2;

        // Desenăm frame-ul folosind dimensiunile VIZUALE
        g2d.drawImage(animation.getCurrentFrame(), drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);

        g2d.setTransform(oldAT);

        // Hitbox-ul pentru modul Debug
        if (handler.getDebugMode()) {
            g.setColor(new Color(0, 0, 255, 100));
            // Desenăm Hitbox-ul real folosind dimensiunile LOGICE (width și height din clasa de bază)
            g.fillRect(screenX, screenY, width, height);
        }
    }
}