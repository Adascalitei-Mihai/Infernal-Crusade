package PaooGame.World.Entities;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Clasa abstracta care reprezinta o entitate de baza din joc (doar un dreptunghi)
 */
public abstract class Entity {
    protected Handler handler;
    protected float x, y;
    protected int logicWidth, logicHeight;
    protected int visualWidth, visualHeight;

    /**
     * Campul care ajuta la calcularea hitbox-ului: acest dreptunghi translat din (0, 0) in (x, y)
     */
    protected Rectangle bounds;

    /**
     * Campul care denota daca entitatea exista sau nu (trebuie distrusa de {@link EntityManager#Update()})
     */
    protected boolean active = true;

    /**
     * Constructorul care initializeaza campurile cu valori implicite
     * @param handler Handler-ul
     * @param x Pozitia pe x
     * @param y Pozitia pe y
     * @param logicWidth Latimea entitatii (logica si vizuala)
     * @param logicHeight Inaltimea entitatii (logica si vizuala)
     */
    public Entity(Handler handler, float x, float y, int logicWidth, int logicHeight) {
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.logicWidth = visualWidth = logicWidth;
        this.logicHeight = visualHeight = logicHeight;

        this.bounds = new Rectangle(0, 0, logicWidth, logicHeight);
    }

    public abstract void Update();
    public abstract void Draw(Graphics g);
    public abstract void onInteraction(Entity e);

    /**
     * Metoda care calculeaza si returneaza limitele de coliziune ale entitatii (hitbox-ul),
     * luand in calcul un potential decalaj de miscare pentru predictia coliziunilor viitoare
     *
     * @param xOffset Decalajul de testare pe Ox
     * @param yOffset Decalajul de testare pe Oy
     * @return Un obiect Rectangle ce reprezinta zona de coliziune in sistemul de coordonate al lumii
     */
    public Rectangle getCollisionBounds(float xOffset, float yOffset) {
        return new Rectangle((int) (x + bounds.x + xOffset), (int) (y + bounds.y + yOffset), bounds.width, bounds.height);
    }

    /**
     * Metoda care scaneaza toate entitatile din nivel pentru a verifica existenta unei coliziuni
     * viitoare, inainte ca miscarea efectiva sa se produca. <br>
     * Declanseaza interactiunile daca o coliziune este detectata
     *
     * @param xOffset Decalajul de miscare dorit pe Ox
     * @param yOffset Decalajul de miscare dorit pe Oy
     * @return Daca exista o coliziune fizica care opreste miscarea
     */
    public boolean checkEntityCollisions(float xOffset, float yOffset) {
        if (handler.getLevel() == null) {
            return false;
        }

        for (Entity e : handler.getEntities()) {
            if (ignoreCollisionCondition(e)) {
                continue;
            }

            Rectangle otherBounds = e.getCollisionBounds(0f, 0f);
            Rectangle myFutureBounds = getCollisionBounds(xOffset, yOffset);

            if (otherBounds.intersects(myFutureBounds)) {
                e.onInteraction(this);
                this.onInteraction(e);

                if (e instanceof Creature) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metoda care stabileste daca o potentiala coliziune cu o entitate specifica trebuie ignorata <br>
     * In mod implicit, impiedica o entitate sa se ciocneasca cu ea insasi
     * @param e Entitatea cu care se verifica suprapunerea
     * @return Daca interactiunea ar trebui ignorata
     */
    protected boolean ignoreCollisionCondition(Entity e) {
        return e.equals(this);
    }

    /**
     * Metoda care calculeaza coordonatele centrului matematic al hitbox-ului
     * @return Un vector de lungime 2 care reprezinta centrul
     */
    public float[] getCenter() {
        return new float[] {
                x + bounds.x + (float) bounds.width / 2f,
                y + bounds.y + (float) bounds.height / 2f
        };
    }

    /**
     * Metoda care indica daca prezenta acestei entitati se comporta ca un obiect, implicit {@code false} (exceptie {@link PaooGame.World.Entities.Environment.Gate})
     * @return Daca este solid
     */
    public boolean isSolid() { return false; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public int getLogicWidth() { return logicWidth; }
    public void setLogicWidth(int logicWidth) { this.logicWidth = logicWidth; }
    public int getLogicHeight() { return logicHeight; }
    public void setLogicHeight(int logicHeight) { this.logicHeight = logicHeight; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Rectangle getBounds() { return bounds; }
    public void setBounds(Rectangle bounds) { this.bounds = bounds; }
}