package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Creatures.Enemies.EnemyAction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta inamicul de tip Schelet.
 * Acum foloseste noul sistem dinamic de actiuni bazat pe record-uri
 * si constante clare pentru gestionarea atacurilor.
 */
public class Skeleton extends GroundEnemy {
    public static final int LOGIC_WIDTH = 2 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 4 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2);

    public static final int VISUAL_WIDTH = 5 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 5 * TILE_SIZE;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 2.5f;
    public static final float ATTACK_RANGE = TILE_SIZE * 5;
    public static final float AGGRO_RANGE = TILE_SIZE * 18;
    public static final float X_RANGE = TILE_SIZE * 5;

    public static final float MAX_HP = 100;
    public static final float KNOCK_PERCENT = 1f;

    // --- TIMPI DE ATAC (IN CADRE) ---
    private static final int ATTACK_WINDUP = 30;
    private static final int ATTACK_ACTIVE = 15;
    private static final int ATTACK_RECOVERY = 20;
    private static final int ATTACK_COOLDOWN = 120;

    private static final float ATTACK_DMG = 50f;
    private static final float COLLISION_DMG = 20f;


    private static final int HITBOX_WIDTH = TILE_SIZE * 4;
    private static final int HITBOX_HEIGHT = TILE_SIZE * 2;
    private final Rectangle attackBounds = new Rectangle(
            0, 0, HITBOX_WIDTH, HITBOX_HEIGHT
    );

    /**
     * Metoda care initializeaza scheletul si ii defineste lista de actiuni.
     */
    public Skeleton(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};

        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;

        // Se foloseste noul constructor folosind exclusiv constante
        availableActions.add(new EnemyAction("ATTACK", ATTACK_WINDUP, ATTACK_ACTIVE, ATTACK_RECOVERY, ATTACK_COOLDOWN, ATTACK_RANGE, false));

        setAnimations();
    }

    @Override
    public void Draw(Graphics g) {
        super.Draw(g);

        if (handler.getDebugMode() && !isDead && "ATTACK".equals(currentAnimId) && currentAction != null && hasExecutedActiveFrame) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                    attackBounds.width, attackBounds.height);
        }
    }

    /**
     * Metoda care proceseaza actiunea "ATTACK" doar in momentul ei activ.
     */
    @Override
    protected void executeActionHitbox(String actionId) {
        if (actionId.equals("ATTACK")) {
            // Skeleton are HITBOX_WIDTH definit la clasa (TILE_SIZE * 4)
            // si HITBOX_HEIGHT (TILE_SIZE * 2)
            executeMeleeAttack(attackBounds, HITBOX_WIDTH, HITBOX_HEIGHT, ATTACK_DMG, 15.0f, -5.0f, 0);
        }
    }

    /**
     * Metoda care actualizeaza starea scheletului.
     * Gestioneaza strict animatia de moarte, delegand restul logicii catre Enemy.
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
     * Metoda care proceseaza tranzitia animatiei de disparitie din nivel.
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
     * Metoda care opreste miscarea inamicului si porneste cronometrul mortii.
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
     * Metoda care asociaza cadrele vizuale pentru identificatorii dinamici ai starii.
     */
    @Override
    public void setAnimations() {
        try {
            animations.put("IDLE", new Animation(9, VisualAssets.Skeleton.get("Idle")));
            animations.put("WALK", new Animation(7, VisualAssets.Skeleton.get("Walk")));
            animations.put("HURT", new Animation(6, VisualAssets.Skeleton.get("Hurt")));

            Animation dieAnim = new Animation(10, VisualAssets.Skeleton.get("Die"));
            dieAnim.setFrameDuration(VisualAssets.Skeleton.get("Die").length - 1, -1);
            animations.put("DIE", dieAnim);

            Animation attackAnim = new Animation(ATTACK_WINDUP / 5, VisualAssets.Skeleton.get("Attack"));
            attackAnim.setFrameDuration(5, ATTACK_ACTIVE);

            // Impartim uniform timpul de revenire pe cele 4 cadre ramase (index 6, 7, 8, 9)
            int recoveryPerFrame = ATTACK_RECOVERY / 4;
            attackAnim.setFrameDuration(6, recoveryPerFrame);
            attackAnim.setFrameDuration(7, recoveryPerFrame);
            attackAnim.setFrameDuration(8, recoveryPerFrame);
            attackAnim.setFrameDuration(9, recoveryPerFrame);

            animations.put("ATTACK", attackAnim);

        } catch (Exception e) {
            System.err.println("[EROARE]: La setarea animatiilor Skeleton: " + e.getMessage());
        }
    }

    /**
     * Metoda care răneste jucatorul la simpla atingere (coliziune pasiva).
     */
    @Override
    public void onInteraction(Entity e) {
        if (isDead) return;
        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;
                dante.applyKnockback(knockX, -10.0f);
            }
        }
    }
}