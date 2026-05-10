package PaooGame.World.Entities.Creatures.Enemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa abstracta care defineste "creierul" tuturor inamicilor.
 * Gestioneaza un sistem dinamic de actiuni (atacuri, vraji) si cooldown-uri.
 */
public abstract class Enemy extends Creature {
    protected enum EnemyState { PATROLLING, CHASING, ACTING }
    protected EnemyState currState = EnemyState.PATROLLING;

    protected static final int PAUSE_DURATION = 90;
    protected int pauseTimer;

    protected float aggroRange;
    protected float[] patrolRange;

    protected int direction = 1;
    protected float baseSpeed, aggroSpeed;
    protected float knockPercent;
    protected float gravity = 0.5f;

    // --- SISTEMUL DINAMIC DE ACTIUNI ---
    protected List<EnemyAction> availableActions = new ArrayList<>();
    protected Map<String, Integer> actionCooldowns = new HashMap<>();

    protected EnemyAction currentAction = null;
    protected int actionTimer = 0;
    protected boolean hasExecutedActiveFrame = false;

    // --- SISTEMUL DINAMIC DE ANIMATII ---
    protected Map<String, Animation> animations = new HashMap<>();
    protected String currentAnimId = "IDLE";

    protected boolean isDead;
    protected int deathTimer;

    public Enemy(Handler handler, float x, float y, int width, int height, float hp, float knockPercent) {
        super(handler, x, y, width, height, hp);
        this.knockPercent = knockPercent;
    }

    protected boolean hasSuperArmor = false;

    protected abstract void applyVerticalMovement();
    protected abstract boolean checkPathBlocked();
    protected abstract void executeActionHitbox(String actionId);

    protected boolean isWindingUp() {
        return currentAction != null && actionTimer >= (currentAction.active() + currentAction.recovery());
    }

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
            // Logica de Super Armor: Daca inamicul are abilitatea si e in Windup,
            // sare peste anularea atacului si peste animatia de HURT!
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

    @Override
    public void applyKnockback(float forceX, float forceY) {
        // Super Armor: Ignoram socul fizic in timpul windup-ului
        if (hasSuperArmor && isWindingUp()) {
            return;
        }
        super.applyKnockback(forceX, forceY);
    }

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
     * Metoda generala, apelabila de clasele copil, pentru a executa un atac de tip MELEE.
     * Calculeaza cutia de lovire (hitbox-ul) si verifica intersectia cu jucatorul.
     * * @param targetBox Referinta catre un obiect Rectangle pe care metoda il va dimensiona si pozitiona.
     * @param hWidth Latimea cutiei de lovire.
     * @param hHeight Inaltimea cutiei de lovire.
     * @param dmg Daunele care vor fi aplicate jucatorului.
     * @param forceX Forta bruta de knockback (pe X). Semnul se va ajusta automat dupa directia de atac.
     * @param forceY Forta bruta de knockback (pe Y).
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

                // Acum inamicul ingheata doar daca ii spunem sa o faca!
                if (hitPauseFrames > 0) {
                    pauseTimer = hitPauseFrames;
                }
            }
        }
    }


    /**
     * Metoda care deseneaza inamicul pe ecran.
     * De asemenea, randeaza vizual zona atacului in modul de depanare.
     */
    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (logicWidth - visualWidth) / 2;
        int drawY = screenY + (logicHeight - visualHeight);

        Animation currentAnim = isDead ? animations.get("DIE") : animations.get(currentAnimId);

        if (currentAnim != null && currentAnim.getCurrentFrame() != null) {
            if (direction != 1) {
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
     * Metoda care scade timpul de asteptare. Varianta perfect sigura in Java.
     */
    private void updateCooldowns() {
        actionCooldowns.replaceAll((_, v) -> v > 0 ? v - 1 : 0);
    }

    /**
     * Metoda refactorizata care rezolva AI-ul inamicilor.
     * Acum inamicii stau pe loc cand asteapta sa atace in loc sa intre in jucator.
     */
    protected void makeDecision() {
        applyVerticalMovement();

        // 1. Daca deja executam un atac care ne cere sa stam pe loc, xMove e 0.
        if (currentAction != null) {
            if (currentAction.stopsMovement()) {
                xMove = 0;
            }
            return;
        }

        float distance = getDistanceToPlayer();
        boolean isInAttackRange = false;

        // 2. Cautam o actiune pe care o putem folosi
        for (EnemyAction action : availableActions) {
            if (distance <= action.triggerRange()) {
                isInAttackRange = true; // Inamicul e destul de aproape ca sa loveasca

                if (actionCooldowns.getOrDefault(action.actionId(), 0) <= 0) {
                    // Daca atacul e gata, ne uitam spre jucator si tragem/lovim!
                    direction = isPlayerToTheRight() ? 1 : -1;
                    startAction(action);

                    if (action.stopsMovement()) {
                        xMove = 0;
                    }
                    return;
                }
            }
        }

        // 3. Daca NU atacam, calculam miscarea.
        float speed = baseSpeed;

        if (isInAttackRange) {
            // Suntem in raza de atac, dar lovitura e pe cooldown (se incarca)!
            // STAM PE LOC si il privim amenintator pe Dante.
            currState = EnemyState.CHASING;
            direction = isPlayerToTheRight() ? 1 : -1;
            xMove = 0;
            return;
        }
        else if (distance <= aggroRange) {
            // Dante e in raza vizuala, dar inca nu a ajuns la raza de atac
            currState = EnemyState.CHASING;
            speed = aggroSpeed;
        } else {
            // Dante e departe
            currState = EnemyState.PATROLLING;
        }

        if (currState != EnemyState.PATROLLING) {
            direction = isPlayerToTheRight() ? 1 : -1;
        }

        xMove = direction * speed;
        handlePathing(speed);
    }

    private void updateActionState() {
        if (currentAction == null) return;

        actionTimer--;

        boolean isActiveFrame = actionTimer < (currentAction.active() + currentAction.recovery())
                && actionTimer >= currentAction.recovery();

        if (isActiveFrame && !hasExecutedActiveFrame) {
            executeActionHitbox(currentAction.actionId());
            hasExecutedActiveFrame = true;
        }

        if (actionTimer <= 0) {
            currentAction = null;
            currState = EnemyState.CHASING; // Lasam pe Chasing pentru a recalcula logic urmatorul frame
        }
    }

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

    protected void updateAnimations() {
        // Dacă avem Super Armor și atacăm, animația de atac are prioritate absolută!
        // Nu lăsăm "HURT" să întrerupă vizual atacul.
        if (hasSuperArmor && currentAction != null) {
            currentAnimId = currentAction.actionId();
        }
        // Altfel, verificăm dacă suntem în starea HURT
        else if (currentAnimId.equals("HURT") && animations.containsKey("HURT")) {
            Animation hurtAnim = animations.get("HURT");
            if (!hurtAnim.isFinished()) {
                hurtAnim.Update();
                return;
            }
        }

        String prevAnim = currentAnimId;

        // Logica standard de priorități (dacă nu suntem deja blocați în Super Armor Attack)
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

    private void applyPhysics() {
        xMove *= 0.8f;
        if (gravity == 0f) yMove *= 0.8f;
        else yMove += gravity;
        move();
    }

    /**
     * S-a reparat bug-ul de vibrare la limitele de patrulare.
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

    protected float getDistanceToPlayer() {
        Entity player = handler.getDante();
        if(player == null) return Float.MAX_VALUE;

        float dx = (x + bounds.x) - (player.getX() + player.getBounds().x);
        float dy = (y + bounds.y) - (player.getY() + player.getBounds().y);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    protected boolean isPlayerToTheRight() {
        Entity player = handler.getDante();
        if(player == null) return false;

        return (player.getX() + player.getBounds().x) > (this.x + this.bounds.x);
    }

    private void checkEnvironmentDamage() {
        int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 16) / TILE_SIZE;

        if (handler.getLevel().isFire(tx, ty)) {
            if (hurt(Dante.FIERY_JUDGEMENT_DMG)) {
                applyKnockback(0f, -5.0f);
            }
        }
    }

    public boolean checkHorizontalCollision() {
        int tx = (int) (x + xMove + (xMove > 0 ? bounds.x + bounds.width : bounds.x)) / TILE_SIZE;
        return isSolidOnEdge(tx, true);
    }
    

    /**
     * Metoda care anuleaza fortat orice actiune pe care inamicul o facea in acel moment.
     */
    protected void cancelCurrentAction() {
        currentAction = null;
        actionTimer = 0;
        hasExecutedActiveFrame = false;
        currState = EnemyState.CHASING; // Sa nu se blocheze, il punem sa fie alert.
    }

    @Override
    public void die() { active = false; }

    public float getKnockPercent() {
        return knockPercent;
    }
}