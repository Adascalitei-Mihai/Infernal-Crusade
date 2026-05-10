package PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Screech;
import PaooGame.World.Entities.Creatures.Enemies.EnemyAction;
import java.awt.Rectangle;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta inamicul zburator de tip Ghoul.
 * Trage cu un proiectil ultrasonic ("Screech") si cade pe pamant la zero HP.
 */
public class Ghoul extends FlyingEnemy {
    public static final int LOGIC_WIDTH = 2 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 2 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(3, 3, LOGIC_WIDTH - 3, LOGIC_HEIGHT - 3);

    public static final int VISUAL_WIDTH = 7 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 5 * TILE_SIZE;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 2.5f;
    public static final float ATTACK_RANGE = TILE_SIZE * 20;
    public static final float AGGRO_RANGE = TILE_SIZE * 26;
    public static final float X_RANGE = TILE_SIZE * 3;

    public static final float MAX_HP = 100;
    public static final float KNOCK_PERCENT = 1f;
    public static final float COLLISION_DMG = 20f;

    protected static final float Y_RANGE = 30f;
    protected static final float HOVER_SPEED = 0.05f;

    // --- TIMPI DE ATAC (IN CADRE) ---
    private static final int SCREECH_WINDUP = 40;
    private static final int SCREECH_ACTIVE = 10;
    private static final int SCREECH_RECOVERY = 30;
    private static final int SCREECH_COOLDOWN = 150;

    private boolean hitGround = false;

    /**
     * Metoda care initializeaza zburatorul si adauga actiunea de atac (Screech).
     */
    public Ghoul(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT, Y_RANGE, HOVER_SPEED);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;

        availableActions.add(new EnemyAction("SCREECH", SCREECH_WINDUP, SCREECH_ACTIVE, SCREECH_RECOVERY, SCREECH_COOLDOWN, ATTACK_RANGE, false));

        setAnimations();
    }

    /**
     * Metoda care lanseaza proiectilul in momentul in care actiunea a ajuns la faza activa.
     */
    @Override
    protected void executeActionHitbox(String actionId) {
        if (actionId.equals("SCREECH")) {
            float[] spawnCoords = getCenter();
            float[] targetCoords = handler.getDante().getCenter();

            // Centram coordonatele proiectilului
            float startX = spawnCoords[0] - ((float) TILE_SIZE);
            float startY = spawnCoords[1] - ((float) TILE_SIZE);

            handler.getLevel().getEntityManager().addEntity(
                    new Screech(handler, startX, startY, targetCoords[0], targetCoords[1], this)
            );
        }
    }

    /**
     * Metoda care actualizeaza starea interna, gestionand si fizica de cadere la deces.
     */
    @Override
    public void Update() {
        if (isDead) {
            handleDeathPhysics();
            return;
        }

        super.Update();
    }

    /**
     * Metoda care asigura faptul ca inamicul moare cu adevarat (este sters din lista)
     * doar DUPA ce atinge o platforma solida.
     */
    private void handleDeathPhysics() {
        if (!hitGround) {
            yMove += 0.8f; // Fortam caderea
            if (yMove > 15f) yMove = 15f;

            float lastY = y;
            move();

            if (y == lastY || Math.abs(yMove) < 0.1f) {
                hitGround = true;
            }
        } else {
            animations.get("DIE").Update();
            if (animations.get("DIE").isFinished()) {
                active = false;
            }
        }
    }

    /**
     * Metoda care declanseaza faza de moarte (caderea spre podea).
     */
    @Override
    public void die() {
        if (isDead) return;
        isDead = true;
        animations.get("DIE").reset();

        // Resetam variabilele de miscare pentru a incepe caderea libera curat
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    /**
     * Metoda care incarca cadrele si configureaza duratele specifice de atac.
     */
    @Override
    public void setAnimations() {
        try {
            animations.put("IDLE", new Animation(10, VisualAssets.Ghoul.get("Idle")));
            animations.put("FLY", new Animation(8, VisualAssets.Ghoul.get("Fly")));
            animations.put("HURT", new Animation(6, VisualAssets.Ghoul.get("Hurt")));

            // Animatia de deces nu se randeaza ciclic
            Animation dieAnim = new Animation(12, VisualAssets.Ghoul.get("Die"));
            dieAnim.setFrameDuration(VisualAssets.Ghoul.get("Die").length - 1, -1);
            animations.put("DIE", dieAnim);

            Animation attackAnim = new Animation(SCREECH_WINDUP / 6, VisualAssets.Ghoul.get("Attack"));
            attackAnim.setFrameDuration(6, SCREECH_ACTIVE);
            attackAnim.setFrameDuration(7, SCREECH_RECOVERY);
            animations.put("SCREECH", attackAnim);

        } catch (Exception e) {
            System.err.println("[EROARE]: La incarcarea animatiilor Ghoul: " + e.getMessage());
        }
    }

    /**
     * Metoda care provoaca daune jucatorului la simpla atingere aeriana.
     */
    @Override
    public void onInteraction(Entity e) {
        if (isDead) return;

        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;
                dante.applyKnockback(knockX, -15.0f);
            }
        }
    }
}