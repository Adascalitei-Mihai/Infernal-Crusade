package PaooGame.World.Entities.Creatures.Enemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa abstracta care defineste logica de baza pentru toti inamicii din joc
 */
public abstract class Enemy extends Creature {

    protected enum EnemyState { PATROLLING, CHASING, ACTING }
    protected EnemyState currState = EnemyState.PATROLLING;

    protected static final int PAUSE_DURATION = 90;
    protected int pauseTimer;

    protected float aggroRange;
    protected float[] patrolRange;

    /**
     * Campul care denota miscarea pe Ox: {@code stanga / dreapta => -1 / 1 }
     */
    protected int direction = 1;
    protected float baseSpeed, aggroSpeed;
    protected float knockPercent;
    protected float gravity = 0.5f;

    protected List<EnemyAction> availableActions = new ArrayList<>();
    protected Map<String, Integer> actionCooldowns = new HashMap<>();

    protected EnemyAction currentAction = null;
    protected int actionTimer = 0;
    protected boolean hasExecutedActiveFrame = false;

    protected Map<String, Animation> animations = new HashMap<>();
    protected String currentAnimId = "IDLE";

    protected boolean isDead;
    protected int deathTimer;
    /**
     * Campul care denota daca inamicul are Super Armour - nu isi ia knockback si intrerupere la atac in timpul de windup
     */
    protected boolean hasSuperArmor;
    /**
     * Campul care denota directia in care este imaginea din {@link PaooGame.Graphics.VisualAssets}: {@code stanga / dreapta => true / false }
     */
    protected boolean flipSpriteDirection;

    /**
     * Constructorul pentru initializarea campurilor
     */
    public Enemy(Handler handler, float x, float y, int width, int height, float hp, float knockPercent) {
        super(handler, x, y, width, height, hp);
        this.knockPercent = knockPercent;
    }

    /**
     * Metoda abstracta care aplica miscarea pe verticala
     */
    protected abstract void applyVerticalMovement();

    /**
     * Metoda abstracta care verifica daca calea este blocata
     * @return Daca e blocata
     */
    protected abstract boolean checkPathBlocked();

    /**
     * Metoda care executa atacul
     * @param actionId Tipul de atac
     */
    protected abstract void executeActionHitbox(String actionId);

    /**
     * Metoda care verifica daca inamicul se afla in faza de pregatire a unui atac
     */
    protected boolean isWindingUp() {
        return currentAction != null && actionTimer >= (currentAction.active() + currentAction.recovery());
    }

    /**
     * Metoda care actualizeaza logica inamicului: fizica, AI, actiuni si animatii
     */
    @Override
    public void Update() {
        super.Update();
        checkEnvironmentDamage();

        if (knockbackTimer > 0) {
            applyPhysics();
            return;
        }

        if (pauseTimer > 0) {
            pauseTimer--;
            applyPhysics();
            return;
        }

        updateCooldowns();
        makeDecision();
        updateActionState();
        move();
        updateAnimations();
    }

    /**
     * Metoda care gestioneaza primirea de daune, tinand cont de mecanica de Super Armor
     */
    @Override
    public boolean hurt(float dmg) {
        if (invulnerabilityTimer > 0) {
            return false;
        }

        hp -= dmg;
        invulnerabilityTimer = INVULN_TIME;

        if (hp <= 0) {
            die();
        } else {
            if (!(hasSuperArmor && isWindingUp())) {
                cancelCurrentAction();
                currentAnimId = "HURT";

                if (animations.containsKey("HURT")) {
                    animations.get("HURT").reset();
                }
            }
        }

        return true;
    }

    /**
     * Metoda care aplica knockback inamicului, exceptand situatiile de Super Armor
     */
    @Override
    public void applyKnockback(float forceX, float forceY) {
        if (hasSuperArmor && isWindingUp()) {
            return;
        }

        super.applyKnockback(forceX, forceY);
    }

    /**
     * Metoda care executa un atac Melee cu hitbox-ul dreptunghiular
     */
    protected void executeMeleeAttack(Rectangle targetBox, int hWidth, int hHeight, float dmg, float forceX, float forceY, int hitPauseFrames) {
        targetBox.setSize(hWidth, hHeight);

        if (direction == 1) {
            targetBox.x = (int) (x + bounds.x + bounds.width);
        } else {
            targetBox.x = (int) (x + bounds.x - hWidth);
        }

        targetBox.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);

        Dante dante = handler.getDante();

        if (targetBox.intersects(dante.getCollisionBounds(0f, 0f))) {
            if (dante.hurt(dmg)) {
                float knockX = (direction == 1) ? Math.abs(forceX) : -Math.abs(forceX);
                dante.applyKnockback(knockX, forceY);

                if (hitPauseFrames > 0) {
                    pauseTimer = hitPauseFrames;
                }
            }
        }
    }

    /**
     * Metoda care desseneaza inamicul si hitbox-ul de debug daca este activat
     */
    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (logicWidth - visualWidth) / 2;
        int drawY = screenY + (logicHeight - visualHeight);

        Animation currentAnim = isDead ? animations.get("DIE") : animations.get(currentAnimId);

        if (currentAnim != null && currentAnim.getCurrentFrame() != null) {
            boolean shouldFlip = (direction != 1);

            if (flipSpriteDirection) {
                shouldFlip = !shouldFlip;
            }

            if (shouldFlip) {
                g.drawImage(currentAnim.getCurrentFrame(), drawX + visualWidth, drawY, -visualWidth, visualHeight, null);
            } else {
                g.drawImage(currentAnim.getCurrentFrame(), drawX, drawY, visualWidth, visualHeight, null);
            }
        }

        if (handler.getDebugMode() && !isDead) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);
        }
    }

    /**
     * Metoda care scade timpul de reincarcare pentru toate abilitatile disponibile
     */
    private void updateCooldowns() {
        actionCooldowns.replaceAll((_, v) -> v > 0 ? v - 1 : 0);
    }

    /**
     * Metoda care are logica de decizie a AI-ului {@link EnemyState}
     */
    protected void makeDecision() {
        applyVerticalMovement();

        if (currentAction != null) {
            if (currentAction.stopsMovement()) {
                xMove = 0;
            }
            return;
        }

        float distance = getDistanceToPlayer();
        boolean isInAttackRange = false;

        for (EnemyAction action : availableActions) {
            if (distance <= action.triggerRange()) {
                isInAttackRange = true;

                if (actionCooldowns.getOrDefault(action.actionId(), 0) <= 0) {
                    direction = isPlayerToTheRight() ? 1 : -1;
                    startAction(action);

                    if (action.stopsMovement()) {
                        xMove = 0;
                    }
                    return;
                }
            }
        }

        float speed = baseSpeed;

        if (isInAttackRange) {
            currState = EnemyState.CHASING;
            direction = isPlayerToTheRight() ? 1 : -1;
            xMove = 0;
            return;
        } else if (distance <= aggroRange) {
            currState = EnemyState.CHASING;
            speed = aggroSpeed;
        } else {
            currState = EnemyState.PATROLLING;
        }

        if (currState != EnemyState.PATROLLING) {
            direction = isPlayerToTheRight() ? 1 : -1;
        }

        xMove = direction * speed;
        handlePathing(speed);
    }

    /**
     * Metoda care gestioneaza tranzitiile intre fazele unei actiuni (Windup, Active, Recovery)
     */
    private void updateActionState() {
        if (currentAction == null) {
            return;
        }

        actionTimer--;

        boolean isActiveFrame = actionTimer < (currentAction.active() + currentAction.recovery())
                && actionTimer >= currentAction.recovery();

        if (isActiveFrame && !hasExecutedActiveFrame) {
            executeActionHitbox(currentAction.actionId());
            hasExecutedActiveFrame = true;
        }

        if (actionTimer <= 0) {
            currentAction = null;
            currState = EnemyState.CHASING;
        }
    }

    /**
     * Metoda care incepe o actiune noua si seteaza cronometrele necesare
     */
    protected void startAction(EnemyAction action) {
        currState = EnemyState.ACTING;
        currentAction = action;
        actionTimer = action.getDuration();
        hasExecutedActiveFrame = false;

        actionCooldowns.put(action.actionId(), action.baseCooldown());

        if (animations.containsKey(action.actionId())) {
            animations.get(action.actionId()).reset();
        }
    }

    /**
     * Metoda care sincronizeaza starea logica a inamicului cu animatia vizuala redata
     */
    protected void updateAnimations() {
        if (hasSuperArmor && currentAction != null) {
            currentAnimId = currentAction.actionId();
        } else if (currentAnimId.equals("HURT") && animations.containsKey("HURT")) {
            Animation hurtAnim = animations.get("HURT");

            if (!hurtAnim.isFinished()) {
                hurtAnim.Update();
                return;
            }
        }

        String prevAnim = currentAnimId;

        if (currentAction != null) {
            currentAnimId = currentAction.actionId();
        } else if (Math.abs(xMove) > 0.1f) {
            currentAnimId = "WALK";
        } else {
            currentAnimId = "IDLE";
        }

        if (!currentAnimId.equals(prevAnim) && animations.containsKey(currentAnimId)) {
            animations.get(currentAnimId).reset();
        }

        if (animations.containsKey(currentAnimId)) {
            animations.get(currentAnimId).Update();
        }
    }

    /**
     * Metoda care aplica frictiunea si gravitatia in timpul stărilor de stun sau knockback
     */
    private void applyPhysics() {
        xMove *= 0.8f;

        if (gravity == 0f) {
            yMove *= 0.8f;
        } else {
            yMove += gravity;
        }

        move();
    }

    /**
     * Metoda care gestioneaza deplasarea in regim de patrulare si evitarea obstacolelor
     */
    private void handlePathing(float speed) {
        if (currState == EnemyState.PATROLLING) {
            if (x <= patrolRange[0]) {
                direction = 1;
            } else if (x >= patrolRange[1]) {
                direction = -1;
            } else if (checkPathBlocked()) {
                direction *= -1;
            }

            xMove = direction * speed;
        } else if (checkPathBlocked()) {
            xMove = 0;
        }
    }

    /**
     * Metoda care calculeaza distanta fata de jucator
     */
    protected float getDistanceToPlayer() {
        Entity player = handler.getDante();

        if (player == null) {
            return Float.MAX_VALUE;
        }

        float dx = (x + bounds.x) - (player.getX() + player.getBounds().x);
        float dy = (y + bounds.y) - (player.getY() + player.getBounds().y);

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Metoda care verifica pozitia relativa a jucatorului fata de inamic
     */
    protected boolean isPlayerToTheRight() {
        Entity player = handler.getDante();

        if (player == null) {
            return false;
        }

        return (player.getX() + player.getBounds().x) > (this.x + this.bounds.x);
    }

    /**
     * Metoda care verifica daca inamicul se afla pe un tile de foc
     */
    private void checkEnvironmentDamage() {
        int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 16) / TILE_SIZE;

        if (handler.getLevel().isFire(tx, ty)) {
            if (hurt(Dante.FIERY_JUDGEMENT_DMG)) {
                applyKnockback(0f, -5.0f);
            }
        }
    }

    /**
     * Metoda care verifica coliziunea orizontala cu marginea harii sau tile-uri solide
     */
    public boolean checkHorizontalCollision() {
        int tx = (int) (x + xMove + (xMove > 0 ? bounds.x + bounds.width : bounds.x)) / TILE_SIZE;
        return isSolidOnEdge(tx, true);
    }

    /**
     * Metoda care reseteaza actiunea curenta
     */
    protected void cancelCurrentAction() {
        currentAction = null;
        actionTimer = 0;
        hasExecutedActiveFrame = false;
        currState = EnemyState.CHASING;
    }

    /**
     * Metoda care notifica {@link PaooGame.World.Entities.EntityManager} ca inamicul este mort
     */
    @Override
    public void die() {
        active = false;
    }

    public float getKnockPercent() { return knockPercent; }
}