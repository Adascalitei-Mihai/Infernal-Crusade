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
 * Clasa care reprezinta inamicul masiv de tip Ciclope.
 * Dispune de un atac cu arie mare de efect si mecanica de Super Armor
 * (nu poate fi intrerupt cat timp isi pregateste lovitura).
 */
public class Cyclops extends GroundEnemy {
    public static final int LOGIC_WIDTH = 4 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 6 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(10, 5, LOGIC_WIDTH - 20, LOGIC_HEIGHT - 10);

    public static final int VISUAL_WIDTH = 16 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 8 * TILE_SIZE;
    public static final int VISUAL_OFFSET_X = -(int)(5 * TILE_SIZE);

    public static final float BASE_SPEED = 1f;
    public static final float AGGRO_SPEED = 2f;
    public static final float ATTACK_RANGE = TILE_SIZE * 14;
    public static final float AGGRO_RANGE = TILE_SIZE * 25;
    public static final float X_RANGE = TILE_SIZE * 6;

    public static final float MAX_HP = 500f;
    public static final float KNOCK_PERCENT = 0.2f;

    // --- TIMPI DE ATAC (IN CADRE) ---
    private static final int ATTACK_WINDUP = 100;
    private static final int ATTACK_ACTIVE = 30;
    private static final int ATTACK_RECOVERY = 40;
    private static final int ATTACK_COOLDOWN = 300;

    private static final float ATTACK_DMG = 100f;
    private static final float COLLISION_DMG = 50f;

    private static final int HITBOX_WIDTH = TILE_SIZE * 7;
    private static final int HITBOX_HEIGHT = TILE_SIZE * 3;
    private final Rectangle attackBounds = new Rectangle(
            0, 0, HITBOX_WIDTH, HITBOX_HEIGHT
    );

    /**
     * Metoda care initializeaza ciclopele si ii inregistreaza atacul devastator.
     */
    public Cyclops(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.visualWidth = VISUAL_WIDTH;
        this.visualHeight = VISUAL_HEIGHT;

        availableActions.add(new EnemyAction("ATTACK", ATTACK_WINDUP, ATTACK_ACTIVE, ATTACK_RECOVERY, ATTACK_COOLDOWN, ATTACK_RANGE, true));

        setAnimations();
        hasSuperArmor = true;
    }

    /**
     * Metoda care executa logica de daune pentru atacul inamicului.
     */
    @Override
    protected void executeActionHitbox(String actionId) {
        if (actionId.equals("ATTACK")) {
            executeMeleeAttack(attackBounds, HITBOX_WIDTH, HITBOX_HEIGHT, ATTACK_DMG, 25.0f, -15.0f, PAUSE_DURATION);
        }
    }

    /**
     * Metoda care actualizeaza starea inamicului, blocand update-ul normal in caz de moarte.
     */
    @Override
    public void Update() {
        if (isDead) {
            handleDeath();
            return;
        }

        // Daca are actiunea de atac activa, suprascriem starea de knockback ca sa nu ii intrerupem animatia (Super Armor partial vizual)
        if (currentAction != null && "ATTACK".equals(currentAction.actionId())) {
            currentAnimId = "ATTACK";
        }

        super.Update();
    }

    /**
     * Metoda care proceseaza tranzitia si disparitia inamicului.
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
     * Metoda care deseneaza ciclopele, tinand cont de offset-ul sau vizual specific (sprite mai lat decat hitbox-ul).
     */
    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT);

        Animation currentAnim = isDead ? animations.get("DIE") : animations.get(currentAnimId);

        if (currentAnim != null && currentAnim.getCurrentFrame() != null) {
            if (direction == 1) {
                g.drawImage(currentAnim.getCurrentFrame(),
                        drawX + VISUAL_WIDTH - VISUAL_OFFSET_X, drawY,
                        -VISUAL_WIDTH, VISUAL_HEIGHT, null);
            } else {
                g.drawImage(currentAnim.getCurrentFrame(),
                        drawX + VISUAL_OFFSET_X, drawY,
                        VISUAL_WIDTH, VISUAL_HEIGHT, null);
            }
        }

        if (handler.getDebugMode() && !isDead) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);

            if ("ATTACK".equals(currentAnimId) && currentAction != null && hasExecutedActiveFrame) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                        (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                        attackBounds.width, attackBounds.height);
            }
        }
    }

    /**
     * Metoda care opreste inamicul si ii incepe secventa de moarte.
     */
    @Override
    public void die() {
        if (isDead) return;
        isDead = true;
        animations.get("DIE").reset();
        deathTimer = 120;
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    /**
     * Metoda care incarca si sincronizeaza animatiile specifice ciclopelui.
     */
    @Override
    public void setAnimations() {
        try {
            animations.put("IDLE", new Animation(12, VisualAssets.Cyclops.get("Idle")));
            animations.put("WALK", new Animation(10, VisualAssets.Cyclops.get("Walk")));
            animations.put("HURT", new Animation(6, VisualAssets.Cyclops.get("Hurt")));

            Animation dieAnim = new Animation(10, VisualAssets.Cyclops.get("Die"));
            dieAnim.setFrameDuration(VisualAssets.Cyclops.get("Die").length - 1, -1);
            animations.put("DIE", dieAnim);

            Animation attackAnim = new Animation(ATTACK_WINDUP / 4, VisualAssets.Cyclops.get("Attack"));
            attackAnim.setFrameDuration(4, ATTACK_ACTIVE);

            int recoveryPerFrame = ATTACK_RECOVERY / 3;
            attackAnim.setFrameDuration(5, recoveryPerFrame);
            attackAnim.setFrameDuration(6, recoveryPerFrame);
            attackAnim.setFrameDuration(7, recoveryPerFrame);
            animations.put("ATTACK", attackAnim);

        } catch (Exception e) {
            System.err.println("[EROARE]: La setarea animatiilor Cyclops: " + e.getMessage());
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if (isDead) return;
        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -15.0f : 15.0f;
                dante.applyKnockback(knockX, -15.0f);
                pauseTimer = PAUSE_DURATION;
            }
        }
    }

}