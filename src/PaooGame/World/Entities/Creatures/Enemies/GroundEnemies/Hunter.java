package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Arrow;
import PaooGame.World.Entities.Creatures.Enemies.EnemyAction;
import java.awt.Rectangle;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta inamicul terestru de tip Vanator.
 * Acest inamic pastreaza distanta si ataca tragand cu sageti.
 */
public class Hunter extends GroundEnemy {
    public static final int LOGIC_WIDTH = 2 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 3 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2);

    public static final int VISUAL_WIDTH = 5 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 5 * TILE_SIZE;

    public static final float BASE_SPEED = 1.8f;
    public static final float AGGRO_SPEED = 3f;
    public static final float ATTACK_RANGE = TILE_SIZE * 18;
    public static final float AGGRO_RANGE = TILE_SIZE * 25;
    public static final float X_RANGE = TILE_SIZE * 6;

    public static final float MAX_HP = 150;
    public static final float KNOCK_PERCENT = 1.2f;

    // --- TIMPI DE ATAC (IN CADRE) ---
    private static final int SHOOT_WINDUP = 40;
    private static final int SHOOT_ACTIVE = 10;
    private static final int SHOOT_RECOVERY = 30;
    private static final int SHOOT_COOLDOWN = 150;

    private static final float COLLISION_DMG = 15f;

    /**
     * Metoda care initializeaza vanatorul si adauga actiunea de tragere cu arcul.
     */
    public Hunter(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;

        availableActions.add(new EnemyAction("SHOOT", SHOOT_WINDUP, SHOOT_ACTIVE, SHOOT_RECOVERY, SHOOT_COOLDOWN, ATTACK_RANGE, true));

        setAnimations();
    }

    /**
     * Metoda care lanseaza proiectilul in momentul in care actiunea a ajuns la faza activa.
     */
    @Override
    protected void executeActionHitbox(String actionId) {
        if (actionId.equals("SHOOT")) {
            float[] spawnCoords = getCenter();
            float[] targetCoords = handler.getDante().getCenter();

            // Spawn sagetata inspre centrul jucatorului
            handler.addEntity(new Arrow(handler, spawnCoords[0], spawnCoords[1],
                    targetCoords[0], targetCoords[1], this));
        }
    }

    /**
     * Metoda care actualizeaza logica interna si blocheaza comportamentul normal in timpul mortii.
     */
    @Override
    public void Update() {
        if (isDead) {
            handleDeath();
            return;
        }

        super.Update();
    }

    /**
     * Metoda care gestioneaza disparitia inamicului dupa animatia de moarte.
     */
    private void handleDeath() {
        if (deathTimer > 0) {
            deathTimer--;
            animations.get("DIE").Update();
        } else {
            active = false;
        }
    }

    /**
     * Metoda care blocheaza miscarea inamicului la zero HP si declanseaza animatia de deces.
     */
    @Override
    public void die() {
        if (isDead) return;
        isDead = true;
        animations.get("DIE").reset();
        deathTimer = 90;
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    /**
     * Metoda care initializeaza si configureaza duratele cadrelor de animatie.
     */
    @Override
    public void setAnimations() {
        try {
            animations.put("IDLE", new Animation(9, VisualAssets.Hunter.get("Idle")));
            animations.put("WALK", new Animation(7, VisualAssets.Hunter.get("Walk")));
            animations.put("HURT", new Animation(6, VisualAssets.Hunter.get("Hurt")));

            Animation dieAnim = new Animation(10, VisualAssets.Hunter.get("Die"));
            dieAnim.setFrameDuration(VisualAssets.Hunter.get("Die").length - 1, -1);
            animations.put("DIE", dieAnim);

            Animation attackAnim = new Animation(SHOOT_WINDUP / 4, VisualAssets.Hunter.get("Attack"));
            attackAnim.setFrameDuration(4, SHOOT_ACTIVE);
            attackAnim.setFrameDuration(5, SHOOT_RECOVERY);
            animations.put("SHOOT", attackAnim);

        } catch (Exception e) {
            System.err.println("[EROARE]: La setarea animatiilor Hunter: " + e.getMessage());
        }
    }

    /**
     * Metoda care proceseaza coliziunile fizice (damage la atingere) daca jucatorul se apropie prea mult.
     */
    @Override
    public void onInteraction(Entity e) {
        if (isDead) return;
        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -10.0f : 10.0f;
                dante.applyKnockback(knockX, -8.0f);
            }
        }
    }
}