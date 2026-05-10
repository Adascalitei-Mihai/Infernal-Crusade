package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tile;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa abstracta care serveste drept baza pentru toate entitatile vii din joc (Dante, Inamici)
 */
public abstract class Creature extends Entity {
    protected static final int INVULN_TIME = 45;
    protected int invulnerabilityTimer = 0;

    protected static final int KNOCK_TIME = 15;
    protected int knockbackTimer = 0;

    protected float hp;
    protected float xMove, yMove;

    /**
     * Constructor pentru initializarea atributelor de baza ale unei creaturi
     * @param handler Handler-ul
     * @param x Pozitia pe Ox
     * @param y Pozitia pe Oy
     * @param width Latimea creaturii
     * @param height Inaltimea creaturii
     * @param hp Punctele de viata initiale
     */
    public Creature(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height);
        this.hp = hp;
    }

    /**
     * Metoda care actualizeaza timerele interne pentru invulnerabilitate si knockback
     */
    @Override
    public void Update() {
        if (invulnerabilityTimer > 0) {
            --invulnerabilityTimer;
        }

        if (knockbackTimer > 0) {
            --knockbackTimer;
        }
    }

    /**
     * Metoda care gestioneaza miscarea entitatii pe ambele axe, verificand coliziunile cu alte entitati
     */
    public void move() {
        if (!checkEntityCollisions(xMove, 0f)) {
            moveX();
        } else {
            xMove = 0;
        }

        if (!checkEntityCollisions(0f, yMove)) {
            moveY();
        } else {
            if (yMove > 0 && this instanceof Dante dante) {
                dante.setOnGround(true);
            }
            yMove = 0;
        }
    }

    /**
     * Metoda care verifica daca viitoarea pozitie a creaturii se intersecteaza cu alte entitati
     * @param xOffset Distanta de miscare pe Ox
     * @param yOffset Distanta de miscare pe Oy
     * @return Daca exista o coliziune cu o entitate solida
     */
    public boolean checkEntityCollisions(float xOffset, float yOffset) {
        for (Entity e : handler.getEntities()) {
            if (e.equals(this)) {
                continue;
            }

            if (e.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset))) {
                e.onInteraction(this);

                if (e.isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metoda care realizeaza miscarea propriu-zisa pe Ox si gestioneaza coliziunile cu tile-urile solide
     */
    public void moveX() {
        if (xMove > 0) {
            int tx = (int) (x + xMove + bounds.x + bounds.width) / TILE_SIZE;

            if (!isSolidOnEdge(tx, true)) {
                x += xMove;
            } else {
                x = tx * TILE_SIZE - bounds.x - bounds.width - 1;
            }
        } else if (xMove < 0) {
            int tx = (int) (x + xMove + bounds.x) / TILE_SIZE;

            if (!isSolidOnEdge(tx, true)) {
                x += xMove;
            } else {
                x = tx * TILE_SIZE + TILE_SIZE - bounds.x;
            }
        }
    }

    /**
     * Metoda care realizeaza miscarea propriu-zisa pe axa Oy si gestioneaza coliziunile cu tile-urile solide
     */
    public void moveY() {
        if (yMove < 0) {
            int ty = (int) (y + yMove + bounds.y) / TILE_SIZE;

            if (!isSolidOnEdge(ty, false)) {
                y += yMove;
            } else {
                y = ty * TILE_SIZE + TILE_SIZE - bounds.y;
                yMove = 0;
            }
        } else if (yMove > 0) {
            int ty = (int) (y + yMove + bounds.y + bounds.height) / TILE_SIZE;

            if (!isSolidOnEdge(ty, false)) {
                y += yMove;
            } else {
                y = ty * TILE_SIZE - bounds.y - bounds.height - 1;
                yMove = 0;

                if (this instanceof Dante dante) {
                    dante.setOnGround(true);
                }
            }
        }
    }

    /**
     * Verifica daca un tile specific de la coordonatele date este solid
     * @param x Coordonata X in matricea de tile-uri
     * @param y Coordonata Y in matricea de tile-uri
     * @return Daca tile-ul este solid sau inexistent
     */
    protected boolean collisionWithTile(int x, int y) {
        Tile t = handler.getLevel().getTile(x, y);
        return (t == null || t.isSolid());
    }

    /**
     * Verifica intreaga margine (latura) a creaturii pe axa specificata pentru a detecta coliziuni cu mediul
     * @param tileCoord Coordonata tile-ului pe care se verifica marginea
     * @param isXAxis Daca verificarea se face pentru miscarea pe orizontala
     * @return Daca marginea intalneste un obstacol solid
     */
    protected boolean isSolidOnEdge(int tileCoord, boolean isXAxis) {
        if (isXAxis) {
            int tyStart = (int) (y + bounds.y) / TILE_SIZE;
            int tyEnd = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            for (int ty = tyStart; ty <= tyEnd; ty++) {
                if (collisionWithTile(tileCoord, ty)) {
                    return true;
                }
            }
        } else {
            int txStart = (int) (x + bounds.x) / TILE_SIZE;
            int txEnd = (int) (x + bounds.x + bounds.width) / TILE_SIZE;

            for (int tx = txStart; tx <= txEnd; tx++) {
                if (collisionWithTile(tx, tileCoord)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metoda abstracta care aplica daune creaturii.
     * @param dmg DMG
     * @return Daca entitatea a fost ranita cu succes
     */
    public abstract boolean hurt(float dmg);

    /**
     * Metoda abstracta apelata atunci cand HP-ul ajunge la 0
     */
    public abstract void die();

    /**
     * Metoda abstracta care configureaza animatiile specifice pentru fiecare tip de creatura
     */
    public abstract void setAnimations();

    /**
     * Metoda care aplica o forta de respingere (knockback) entitatii
     * @param forceX Intensitatea respingerii pe orizontala.
     * @param forceY Intensitatea respingerii pe verticala.
     */
    public void applyKnockback(float forceX, float forceY) {
        xMove = forceX;
        yMove = forceY;
        knockbackTimer = KNOCK_TIME;
    }

    public float getHp() { return hp; }
}