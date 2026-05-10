package PaooGame.World.Entities.Projectiles;

import PaooGame.Graphics.Animation;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa abstracta care reprezinta un proiectibil, avand o miscare de translatie liniara
 */
public abstract class Projectile extends Entity {
    private final static float PROJECTILE_MAX_RANGE = 80 * TILE_SIZE;
    private final static float KNOCKBACK_FORCE = 10f;
    protected Animation animation;

    private final float START_X;
    private final float START_Y;
    private float speedX, speedY;

    private Creature owner;
    private final float damage;
    private final float knockPercent;

    /**
     * Campul care indica daca {@link Dante} a reflectat proiectibilul
     */
    private boolean justReflected;

    /**
     * Constructorul care intitializeaza campurile si calculeaza vitezele (apeleaza {@link #calculateVelocity(float, float, float)})
     * @param handler Handler-ul
     * @param x Pozitia initiala pe Ox
     * @param y Pozitia initiala pe Oy
     * @param logicWidth Latimea logica
     * @param logicHeight Inaltimea logica
     * @param visualWidth Latimea vizuala
     * @param visualHeight Inaltimea vizuala
     * @param targetX Pozitia finala pe Ox
     * @param targetY Pozitia finala pe Oy
     * @param speed Viteza pe directia start - final
     * @param owner Proprietarul
     * @param damage DMG
     * @param knockPercent Procentajul aplicat din {@link #KNOCKBACK_FORCE} in urma unei coliziuni
     */
    public Projectile(Handler handler, float x, float y, int logicWidth, int logicHeight,
                      int visualWidth, int visualHeight, float targetX, float targetY,
                      float speed, Creature owner, float damage, float knockPercent) {
        super(handler, x, y, logicWidth, logicHeight);
        this.visualWidth = visualWidth;
        this.visualHeight = visualHeight;

        this.START_X = x;
        this.START_Y = y;
        calculateVelocity(targetX, targetY, speed);

        this.owner = owner;
        this.damage = damage;
        this.knockPercent = knockPercent;
    }

    /**
     * Metoda care calculeaza deplasarea pe axe
     * @param targetX Pozitia finala pe Ox
     * @param targetY Pozitia finala pe Oy
     * @param speed Viteza relativa
     */
    private void calculateVelocity(float targetX, float targetY, float speed) {
        float diffX = targetX - x;
        float diffY = targetY - y;
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        speedX = (diffX / distance) * speed;
        speedY = (diffY / distance) * speed;
    }

    /**
     * Metoda care impune logica de reflectare, se schimba {@link #owner} si se intoarce spre noua pozitie a proprietarului initial cu {@code speed * 2}
     * @param newOwner Noul proprietar
     */
    public void reflect(Creature newOwner) {
        float targetX = owner.getX() + owner.getLogicWidth() / 2f;
        float targetY = owner.getY() + owner.getLogicHeight() / 2f;

        this.owner = newOwner;
        this.justReflected = true;

        float currentSpeed = (float) Math.sqrt(speedX * speedX + speedY * speedY);

        float diffX = targetX - this.x;
        float diffY = targetY - this.y;
        float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance != 0) {
            speedX = (diffX / distance) * currentSpeed * 2;
            speedY = (diffY / distance) * currentSpeed * 2;
        } else {
            speedX = -speedX;
            speedY = -speedY;
        }
    }

    /**
     * Metoda de actualizare, verifica daca exista coliziune in pozitie viitoare si daca proiectibilul nu a iesit din {@link #PROJECTILE_MAX_RANGE}
     */
    @Override
    public void Update() {
        if(checkEntityCollisions(speedX, speedY)) {
            if (justReflected) {
                justReflected = false;
            } else {
                active = false;
                return;
            }
        }

        x += speedX;
        y += speedY;

        float distanceTravelled = (float) Math.sqrt(Math.pow(x - START_X, 2) + Math.pow(y - START_Y, 2));
        if (distanceTravelled > PROJECTILE_MAX_RANGE) {
            active = false;
        }

        animation.Update();
    }

    /**
     * Metoda de desenare, foloseste {@link #getAngle()} si {@link AffineTransform} pentru a roti cadrul curent al animatiei
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform oldAT = g2d.getTransform();

        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int centerX = screenX + logicWidth / 2;
        int centerY = screenY + logicHeight / 2;

        g2d.rotate(getAngle(), centerX, centerY);

        int drawX = screenX + (logicWidth - visualWidth) / 2;
        int drawY = screenY + (logicHeight - visualHeight) / 2;

        g2d.drawImage(animation.getCurrentFrame(), drawX, drawY, visualWidth, visualHeight, null);

        g2d.setTransform(oldAT);

        if (handler.getDebugMode()) {
            g.setColor(new Color(0, 0, 255, 100));
            g.fillRect(screenX, screenY, logicWidth, logicHeight);
        }
    }

    /**
     * Metoda care modifica contitia din {@link #checkEntityCollisions(float, float)} pentru a ignora proprietarul
     * @param e Entitatea cu care se verifica suprapunerea
     * @return Daca colisiunea trebuie ignorata
     */
    @Override
    protected boolean ignoreCollisionCondition(Entity e) {
        return e.equals(this) || e.equals(owner);
    }

    /**
     * Metoda care executa coliziunea, daca e jucatorul aplica {@link Dante#projectileHit(Projectile)}, altfel logica de {@link Creature#hurt(float)} si {@link Creature#applyKnockback(float, float)}
     * @param e Entitatea cu care se interactioneaza
     */
    @Override
    public void onInteraction(Entity e) {
        if (e.equals(owner)) {
            return;
        }

        if (e instanceof Creature target) {
            if (e instanceof Dante dante) {
                dante.projectileHit(this);
            } else if (target.hurt(damage)) {
                float knockX = (speedX > 0) ? KNOCKBACK_FORCE * knockPercent  : -KNOCKBACK_FORCE * knockPercent;
                target.applyKnockback(knockX, -5f);
            }
        }
    }

    /**
     * Functia care calculeaza unghiul proiectibilului
     * @return Faza in coordonate polare
     */
    public float getAngle() { return (float) Math.atan2(speedY, speedX); }

    public float getDamage() { return damage; }
    public float getSpeedX() { return speedX; }
    public float getKnockPercent() { return knockPercent; }
    public float getKnockbackForce() { return KNOCKBACK_FORCE; }
}