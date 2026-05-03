package PaooGame.World.Entities.Projectiles;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Creature;
import java.awt.*;
import java.awt.geom.AffineTransform;

import static PaooGame.World.Tile.TILE_SIZE;

public class Arrow extends Projectile {
    private final static int DEFAULT_WIDTH = (int) (TILE_SIZE * 1.5f);
    private final static int DEFAULT_HEIGHT = (int) (TILE_SIZE * 0.5f); // Subțire ca o săgeată
    private final static float SPEED = 10f;
    private final static float DMG = 30f;
    private final static float KNOCK_PERCENT = 1f;

    private Animation animation;

    public Arrow(Handler handler, float x, float y, float tx, float ty, Creature owner) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, tx, ty,
                SPEED, owner, DMG, KNOCK_PERCENT);

        // Inițializăm animația cu cele 2 frame-uri din VisualAssets
        // Viteza 10 înseamnă că schimbă frame-ul la fiecare 10 tick-uri
        this.animation = new Animation(15, VisualAssets.Arrow);

        // Ajustăm hitbox-ul să fie centrat pe imagine dacă e necesar
        this.bounds.x = 0;
        this.bounds.y = 0;
        this.bounds.width = DEFAULT_WIDTH;
        this.bounds.height = DEFAULT_HEIGHT;
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

        // Calculăm poziția pe ecran
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        // Centrul rotației trebuie să fie mijlocul săgeții
        int centerX = screenX + width / 2;
        int centerY = screenY + height / 2;

        // Aplicăm rotația
        // Math.atan2(speedY, speedX) ne dă unghiul în radiani
        g2d.rotate(getAngle(), centerX, centerY);

        // Desenăm frame-ul curent din animație
        g2d.drawImage(animation.getCurrentFrame(), screenX, screenY, width, height, null);

        // Revenim la transformarea inițială
        g2d.setTransform(oldAT);

        // Debug: Hitbox-ul (atenție, hitbox-ul nu se rotește în logica de coliziune AABB standard)
        if (handler.getDebugMode()) {
            g.setColor(new Color(0, 0, 200, 100));
            g.fillRect(screenX, screenY, width, height);
        }
    }
}