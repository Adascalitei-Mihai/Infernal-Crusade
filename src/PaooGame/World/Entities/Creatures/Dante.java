package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Projectile;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import static PaooGame.States.VisualElements.TransitionManager.TRANSITION_TIME;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care defineste personajul principal, Dante
 */
public class Dante extends Creature {
    public static final int LOGIC_WIDTH = 2 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 4 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2);
    public static final int VISUAL_WIDTH = 6 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 6 * TILE_SIZE;

    public enum Direction { RIGHT, UP, LEFT }
    public enum MovementState { IDLE, RUNNING, JUMPING, FALLING, HURT, DEAD }
    public enum Ranks {
        D(3), C(5), B(7), A(15), S(-1);
        private final int points;
        Ranks(int points) { this.points = points; }
    }
    public enum AnimationState {
        IDLE, RUN, JUMP, FALL, ATTACK1, ATTACK2, HIT, DEATH, ICE_MORPH, EDEN_SHIELD, FIERY_JUDGEMENT
    }

    private Direction currDirection = Direction.RIGHT;
    private MovementState currMove = MovementState.IDLE;
    private DanteAction currAction = DanteAction.IDLE;
    private AnimationState currAnim = AnimationState.IDLE;
    private Ranks currRank = Ranks.D;

    private final Animation[] animations = new Animation[AnimationState.values().length];
    private final int[] cooldowns = new int[DanteAction.values().length];

    public static final float MAX_HP = 1000;
    private static final float BASE_REGEN = 5f / 60;
    private final float REGEN_PERCENT, RESISTANCE;
    public static final float MAX_SPEED = 15f, SPEED = 5f;
    private static final float ACCELERATION = 0.2f, FRICTION = 0.6f, GRAVITY = 0.5f;
    private static final float JUMP_IMPULSE = -10.0f, JUMP_ADDAGE = -0.6f;
    private static final int MAX_JUMP_TIME = 20;

    private static final float BASE_ATTACK_DMG = 50f, KNOCKBACK_FORCE = 15f;
    private int attackDmg, currPoints, actionTimer, jumpTimer, deathTimer, cd;
    private final Rectangle attackBounds = new Rectangle();
    private final Rectangle edenShieldBounds = new Rectangle();

    private boolean isJumping, onGround, slipperyMovement;
    private final float FIERY_JUDGEMENT_DIFF_CD_PERCENT;
    public static final int ICE_TIMER = 600, FIRE_TIMER = 300, FIERY_JUDGEMENT_DMG = 50;
    private static final int FIERY_JUDGEMENT_BASE_CD = 900, FIERY_JUDGEMENT_SPREAD = 40, EDEN_SHIELD_PADDING = TILE_SIZE * 2;

    /**
     * Constructorul care initializeaza campurile, seteaza valorile pentru {@link #RESISTANCE}, {@link #REGEN_PERCENT},
     * {@link #FIERY_JUDGEMENT_DIFF_CD_PERCENT}, {@link DanteAction#EDEN_SHIELD} si animatiile
     * @param handler Handler-ul
     * @param x Pozitia pe Ox
     * @param y Pozitia pe Oy
     */
    public Dante(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP);

        RESISTANCE = switch (handler.getDifficulty()) {
            case EASY -> 0.75f;
            case HARD -> 1.25f;
            default -> 1f;
        };

        REGEN_PERCENT = switch (handler.getDifficulty()) {
            case EASY -> 1.5f;
            case HARD -> 0f;
            default -> 1f;
        };

        FIERY_JUDGEMENT_DIFF_CD_PERCENT = switch (handler.getDifficulty()) {
            case EASY -> 0.8f;
            case HARD -> 1.2f;
            default -> 1f;
        };

        DanteAction.EDEN_SHIELD.setActive(switch (handler.getDifficulty()) {
            case EASY -> 20;
            case HARD -> 8;
            default -> 15;
        });

        this.bounds = new Rectangle(BOUNDS);
        setAnimations();
    }

    /**
     * Metoda principala de actualizare a logicii personajului <br>
     * 1. {@link Creature#Update()} <br>
     * 2. {@link #updateHealth()} <br>
     * 3. {@link #updateRank()} <br>
     * 4. {@link #applyGravity()} <br>
     * 5. {@link #checkLevelBounds()} <br>
     * 6. {@link #updateDirection()} <br>
     * 7. (daca e mort) {@link #handleDeathLogic()} <br>
     * 8. {@link #updateCooldowns()} <br>
     * 9. {@link #updateMovementState()} <br>
     * 10. (daca nu e hurt) {@link #updateDanteAction()} <br>
     * 11. {@link #handleAnimationTransition()}
     */
    @Override
    public void Update() {
        super.Update();

        updateHealth();
        updateRank();
        applyGravity();
        checkLevelBounds();
        updateDirection();

        if (currMove == MovementState.DEAD) {
            handleDeathLogic();
            return;
        }

        updateCooldowns();
        updateMovementState();

        if (currMove != MovementState.HURT) {
            updateDanteAction();
        } else {
            currAction = DanteAction.IDLE;
            actionTimer = 0;
        }

        handleAnimationTransition();
    }

    /**
     * Metoda care gestioneaza logica de dupa moartea personajului si respawn-ul
     */
    private void handleDeathLogic() {
        move();

        if (deathTimer > 0) {
            deathTimer--;
            animations[AnimationState.DEATH.ordinal()].Update();
        } else {
            handler.getTransitionManager().start(() -> handler.getLevel().requestRespawn());

            hp = MAX_HP;
            currMove = MovementState.IDLE;
            currAnim = AnimationState.IDLE;
            xMove = 0;
            yMove = 0;
            knockbackTimer = 0;
        }
    }

    /**
     * Metoda care sincronizeaza starea logica a animatiei cu schimbarile de cadru vizuale
     */
    private void handleAnimationTransition() {
        AnimationState prevAnim = currAnim;
        updateAnimationState();

        if (currAnim != prevAnim) {
            animations[currAnim.ordinal()].reset();
        }

        animations[currAnim.ordinal()].Update();
    }

    /**
     * Metoda care reface sanatatea personajului in timp daca acesta nu este lovit
     */
    private void updateHealth() {
        if (invulnerabilityTimer <= 0) {
            hp = Math.min(MAX_HP, hp + BASE_REGEN * REGEN_PERCENT);
        }
    }

    /**
     * Metoda care actualizeaza rank-ul si modificatorii de {@link #attackDmg} si {@link #cd}
     */
    private void updateRank() {
        if (currRank != Ranks.S && currPoints >= currRank.points) {
            currPoints -= currRank.points;
            currRank = Ranks.values()[currRank.ordinal() + 1];
        }

        attackDmg = (int) (BASE_ATTACK_DMG * (1f + currRank.ordinal() * 0.2f));
        cd = (int) (FIERY_JUDGEMENT_BASE_CD * FIERY_JUDGEMENT_DIFF_CD_PERCENT * (1f - currRank.ordinal() * 0.05f));
    }

    /**
     * Metoda care aplica forta gravitationala
     */
    private void applyGravity() {
        yMove += GRAVITY;
    }

    /**
     * Metoda care verifica pozitia in interiorul nivelului, altfel activeaza {@link #die()}
     */
    private void checkLevelBounds() {
        boolean fellOff = (y + LOGIC_HEIGHT >= (handler.getLevel().getHeight() - 1) * TILE_SIZE);
        boolean exitedLeft = (x + bounds.x + bounds.width < 0);
        boolean exitedRight = (x + bounds.x > handler.getLevel().getWidth() * TILE_SIZE);

        if (fellOff || exitedLeft || exitedRight) {
            die();
        }
    }

    /**
     * Metoda care actualizeaza orientarea curenta a personajului pe baza input-ului
     */
    private void updateDirection() {
        if (handler.getInputManager().up) {
            currDirection = Direction.UP;
        } else if (handler.getInputManager().left) {
            currDirection = Direction.LEFT;
        } else if (handler.getInputManager().right) {
            currDirection = Direction.RIGHT;
        }
    }

    /**
     * Metoda care scade cronometrele de cooldown pentru abilitati
     */
    private void updateCooldowns() {
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
            }
        }
    }

    /**
     * Metoda care determina starea de miscare (mers, sarit, cazut) si aplica vitezele
     */
    private void updateMovementState() {
        if (knockbackTimer > 0) {
            currMove = MovementState.HURT;
            xMove *= FRICTION;
            onGround = false;
            move();
            return;
        }

        if (onGround) {
            slipperyMovement = isOnIce();
        }

        if (!onGround) {
            currMove = (yMove < 0) ? MovementState.JUMPING : MovementState.FALLING;
        } else if (handler.getInputManager().right || handler.getInputManager().left) {
            currMove = MovementState.RUNNING;
        } else {
            currMove = MovementState.IDLE;
        }

        handleHorizontalMovement();
        handleJumpLogic();

        if (currAction == DanteAction.ATTACKING && onGround) {
            xMove = 0;
        }

        move();
    }

    /**
     * Metoda care gestioneaza acceleratia si frictiunea pe Ox
     */
    private void handleHorizontalMovement() {
        if (!slipperyMovement) {
            if (handler.getInputManager().right) {
                xMove = SPEED;
            } else if (handler.getInputManager().left) {
                xMove = -SPEED;
            } else {
                xMove *= FRICTION;
            }
        } else {
            if (handler.getInputManager().right) {
                xMove = Math.min(MAX_SPEED, Math.max(SPEED, xMove + ACCELERATION));
            } else if (handler.getInputManager().left) {
                xMove = Math.max(-MAX_SPEED, Math.min(-SPEED, xMove - ACCELERATION));
            } else {
                xMove *= FRICTION;
            }
        }
    }

    /**
     * Metoda care calculeaza mecanica de saritura variabila
     */
    private void handleJumpLogic() {
        if (yMove > GRAVITY + 0.1f) {
            onGround = false;
        }

        if (handler.getInputManager().space && onGround) {
            yMove = JUMP_IMPULSE;
            onGround = false;
            isJumping = true;
            jumpTimer = MAX_JUMP_TIME;
        }

        if (handler.getInputManager().space && isJumping) {
            if (jumpTimer > 0) {
                yMove += JUMP_ADDAGE;
                --jumpTimer;
            } else {
                isJumping = false;
            }
        }

        if (!handler.getInputManager().space) {
            isJumping = false;
            if (yMove < 0) {
                yMove *= 0.5f;
            }
        }
    }

    /**
     * Metoda care ruleaza logica specifica actiunii curente
     */
    private void updateDanteAction() {
        switch (currAction) {
            case IDLE -> checkInputsForNewAction();
            case ATTACKING -> handleAttackState();
            case ICE_MORPH -> handleIceMorphState();
            case EDEN_SHIELD -> handleEdenShieldState();
            case FIERY_JUDGEMENT -> handleFieryJudgementState();
        }
    }

    /**
     * Metoda care verifica apasarea tastelor pentru a declansa abilitati noi
     */
    private void checkInputsForNewAction() {
        if (handler.getCurrLevel() != 1 && handler.getInputManager().k && cooldowns[DanteAction.EDEN_SHIELD.ordinal()] <= 0 && onGround) {
            startAction(DanteAction.EDEN_SHIELD);
        } else if (handler.getInputManager().j && cooldowns[DanteAction.ATTACKING.ordinal()] <= 0) {
            startAction(DanteAction.ATTACKING);
            handler.getPadManager().rumble(0.0f, 0.5f, 100);
        } else if (handler.getCurrLevel() == 3 && handler.getInputManager().l && cooldowns[DanteAction.FIERY_JUDGEMENT.ordinal()] <= 0 && onGround) {
            startAction(DanteAction.FIERY_JUDGEMENT);
            cooldowns[DanteAction.FIERY_JUDGEMENT.ordinal()] = cd;
            handler.getPadManager().rumble(0.8f, 0.8f, 200);
        } else if (handler.getInputManager().shift && cooldowns[DanteAction.ICE_MORPH.ordinal()] <= 0) {
            startAction(DanteAction.ICE_MORPH);
        }
    }

    /**
     * Metoda care seteaza actiunea curenta si porneste cronometrele aferente
     */
    private void startAction(DanteAction action) {
        currAction = action;
        actionTimer = action.getDuration();
        cooldowns[action.ordinal()] = action.baseCooldown;
    }

    /**
     * Metoda care gestioneaza faza activa a atacului Melee si coliziunile cu inamicii
     */
    private void handleAttackState() {
        actionTimer--;
        boolean isActiveFrame = actionTimer < (currAction.active + currAction.recovery) && actionTimer >= currAction.recovery;

        if (isActiveFrame) {
            updateAttackHitbox();

            for (Entity e : handler.getEntities()) {
                if (e instanceof Enemy target && attackBounds.intersects(target.getCollisionBounds(0f, 0f))) {
                    if (target.hurt(attackDmg)) {
                        currPoints++;
                        handler.getScoreMan().addSuccessfulHit(currRank);

                        float kX = (currDirection == Direction.RIGHT) ? KNOCKBACK_FORCE : (currDirection == Direction.LEFT ? -KNOCKBACK_FORCE : 0);
                        float kY = (currDirection == Direction.UP) ? -10f : -5f;

                        target.applyKnockback(kX * target.getKnockPercent(), kY);
                    }
                }
            }
        }

        if (actionTimer <= 0) {
            currAction = DanteAction.IDLE;
        }
    }

    /**
     * Metoda care repozitioneaza hitbox-ul de atac in functie de directia personajului
     */
    private void updateAttackHitbox() {
        int vWidth = (int) (bounds.width * 1.5f);
        int vHeight = TILE_SIZE * 3;
        int hWidth = TILE_SIZE * 4;
        int hHeight = (int) (bounds.height * 0.5f);

        switch (currDirection) {
            case RIGHT -> {
                attackBounds.setSize(hWidth, hHeight);
                attackBounds.setLocation((int) (x + bounds.x + bounds.width), (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2));
            }
            case UP -> {
                attackBounds.setSize(vWidth, vHeight);
                attackBounds.setLocation((int) (x + bounds.x - (float) (vWidth - bounds.width) / 2), (int) (y + bounds.y - vHeight));
            }
            case LEFT -> {
                attackBounds.setSize(hWidth, hHeight);
                attackBounds.setLocation((int) (x + bounds.x - hWidth), (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2));
            }
        }
    }

    /**
     * Metoda care gestioneaza transformarea mediului in gheata sub Dante
     */
    private void handleIceMorphState() {
        if (actionTimer > 0) {
            actionTimer--;
        }

        if (actionTimer < (currAction.active + currAction.recovery) && actionTimer >= currAction.recovery) {
            int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int ty = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            int startX = (currDirection != Direction.LEFT) ? tx - 2 : tx - 5;
            int endX = (currDirection != Direction.LEFT) ? tx + 5 : tx + 2;

            for (int i = startX; i <= endX; i++) {
                for (int j = ty - 1; j <= ty + 2; j++) {
                    handler.getLevel().setIce(i, j, ICE_TIMER);
                }
            }

            if (handler.getInputManager().shift && actionTimer == currAction.recovery) {
                actionTimer++;
            }
        }

        if (actionTimer <= 0) {
            currAction = DanteAction.IDLE;
        }
    }

    /**
     * Metoda care gestioneaza Eden Shield
     */
    private void handleEdenShieldState() {
        actionTimer--;

        if (actionTimer < (currAction.active + currAction.recovery) && actionTimer >= currAction.recovery) {
            edenShieldBounds.setBounds(
                    (int) (x + bounds.x - EDEN_SHIELD_PADDING),
                    (int) (y + bounds.y - EDEN_SHIELD_PADDING),
                    bounds.width + EDEN_SHIELD_PADDING * 2,
                    bounds.height + EDEN_SHIELD_PADDING * 2
            );
        } else {
            edenShieldBounds.setSize(0, 0);
        }

        if (actionTimer <= 0) {
            currAction = DanteAction.IDLE;
        }
    }

    /**
     * Metoda care gestioneaza Fiery Judgement
     */
    private void handleFieryJudgementState() {
        actionTimer--;

        if (actionTimer == currAction.active + currAction.recovery - 1) {
            spreadFire((int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE, (int) (y + bounds.y + bounds.height + 6) / TILE_SIZE, FIERY_JUDGEMENT_SPREAD);
        }

        if (actionTimer <= 0) {
            currAction = DanteAction.IDLE;
        }
    }

    /**
     * Metoda care propaga focul in mod recursiv prin tile-urile hartii
     */
    private void spreadFire(int tx, int ty, int remainingSteps) {
        if (remainingSteps <= 0 || tx < 0 || ty < 0 ||
                tx >= handler.getLevel().getWidth() || ty >= handler.getLevel().getHeight()) {
            return;
        }

        if (!handler.getLevel().getTile(tx, ty).isSolid() ||
                handler.getLevel().isFire(tx, ty) ||
                handler.getLevel().isIce(tx, ty)) {
            return;
        }

        handler.getLevel().setFire(tx, ty, FIRE_TIMER);

        spreadFire(tx + 1, ty, remainingSteps - 1);
        spreadFire(tx - 1, ty, remainingSteps - 1);
        spreadFire(tx, ty + 1, remainingSteps - 1);
        spreadFire(tx, ty - 1, remainingSteps - 1);
    }

    /**
     * Metoda care verifica daca tile-urile de sub picioarele lui Dante sunt gheata
     */
    private boolean isOnIce() {
        int ty = (int) (y + bounds.y + bounds.height + 8) / TILE_SIZE;
        int txS = (int) (x + bounds.x) / TILE_SIZE;
        int txE = (int) (x + bounds.x + bounds.width - 1) / TILE_SIZE;

        for (int i = txS; i <= txE; i++) {
            if (handler.getLevel().isIce(i, ty)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Metoda care determina care animatie trebuie redata in functie de starea actiunii sau a miscarii
     */
    private void updateAnimationState() {
        if (currAction != DanteAction.IDLE) {
            currAnim = switch (currAction) {
                case ATTACKING -> (currDirection == Direction.UP) ? AnimationState.ATTACK1 : AnimationState.ATTACK2;
                case EDEN_SHIELD -> AnimationState.EDEN_SHIELD;
                case ICE_MORPH -> AnimationState.ICE_MORPH;
                case FIERY_JUDGEMENT -> AnimationState.FIERY_JUDGEMENT;
                default -> AnimationState.IDLE;
            };
        } else {
            currAnim = switch (currMove) {
                case HURT -> AnimationState.HIT;
                case JUMPING -> AnimationState.JUMP;
                case FALLING -> AnimationState.FALL;
                case RUNNING -> AnimationState.RUN;
                case DEAD -> AnimationState.DEATH;
                default -> AnimationState.IDLE;
            };
        }
    }

    /**
     * Metoda care deseneaza imaginea personajului pe ecran
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());
        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT);

        if (currDirection == Direction.LEFT) {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(), drawX + VISUAL_WIDTH, drawY, -VISUAL_WIDTH, VISUAL_HEIGHT, null);
        } else {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(), drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }

        if (handler.getDebugMode()) {
            drawDebug(g, screenX, screenY);
        }
    }

    /**
     * Metoda care deseneaza un overlay in modul debug
     * @param g Grafica de desenare
     * @param sX Pozitia ecranului pe Ox
     * @param sY Pozitia ecranului pe Oy
     */
    private void drawDebug(Graphics g, int sX, int sY) {
        g.setColor(new Color(255, 0, 0, 100));
        g.fillRect(sX + bounds.x, sY + bounds.y, bounds.width, bounds.height);

        if (currAction == DanteAction.ATTACKING) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()), (int) (attackBounds.y - handler.getGameCamera().getyOffset()), attackBounds.width, attackBounds.height);
        }

        if (currAction == DanteAction.EDEN_SHIELD) {
            g.setColor(new Color(0, 255, 0, 100));
            g.fillRect((int) (edenShieldBounds.x - handler.getGameCamera().getxOffset()), (int) (edenShieldBounds.y - handler.getGameCamera().getyOffset()), edenShieldBounds.width, edenShieldBounds.height);
        }
    }

    /**
     * Metoda care aplica logica pentru cazul in care Dante este lovit de un {@link Projectile}
     * @param p Proiectibilul
     */
    public void projectileHit(Projectile p) {
        if (currAction == DanteAction.EDEN_SHIELD && edenShieldBounds.intersects(p.getCollisionBounds(0f, 0f))) {
            p.reflect(this);
            currPoints += 3;
            handler.getScoreMan().addReflect();
            handler.getPadManager().rumble(0.6f, 0.6f, 150);
        } else if (hurt(p.getDamage())) {
            applyKnockback((p.getSpeedX() > 0 ? 1 : -1) * p.getKnockbackForce() * p.getKnockPercent(), -5f);
        }
    }

    /**
     * Metoda care aplica daune personajului, scade rangul si activeaza invulnerabilitatea
     * @param dmg DMG
     */
    @Override
    public boolean hurt(float dmg) {
        if (invulnerabilityTimer > 0 || currAction == DanteAction.EDEN_SHIELD) {
            return false;
        }

        currRank = Ranks.values()[Math.max(0, currRank.ordinal() - 1)];
        currPoints = 0;
        hp -= dmg * RESISTANCE;
        invulnerabilityTimer = INVULN_TIME;
        handler.getPadManager().rumble(0.8f, 0.8f, 200);

        if (hp <= 0) {
            die();
        }

        return true;
    }

    /**
     * Metoda care seteaza starea de deces si porneste cronometrul de tranzitie
     */
    @Override
    public void die() {
        if (currMove == MovementState.DEAD) {
            return;
        }

        currMove = MovementState.DEAD;
        currAction = DanteAction.IDLE;
        currRank = Ranks.D;
        currPoints = 0;
        hp = 0;
        xMove = 0;
        yMove = 0;
        deathTimer = TRANSITION_TIME;
        currAnim = AnimationState.DEATH;
        handler.getPadManager().rumble(0.8f, 0.8f, 300);
    }

    /**
     * Metoda care incarca animatiile
     */
    @Override
    public void setAnimations() {
        try {
            animations[AnimationState.IDLE.ordinal()] = new Animation(6, VisualAssets.Dante.get("Idle"));
            animations[AnimationState.RUN.ordinal()] = new Animation(5, VisualAssets.Dante.get("Run"));
            animations[AnimationState.JUMP.ordinal()] = new Animation(6, VisualAssets.Dante.get("Jump"));
            animations[AnimationState.FALL.ordinal()] = new Animation(6, VisualAssets.Dante.get("Fall"));
            animations[AnimationState.FALL.ordinal()].setFrameDuration(VisualAssets.Dante.get("Fall").length - 1, -1);

            int atkWindup = DanteAction.ATTACKING.windup / 2;
            animations[AnimationState.ATTACK1.ordinal()] = new Animation(atkWindup, VisualAssets.Dante.get("Attack1"));
            animations[AnimationState.ATTACK1.ordinal()].setFrameDuration(2, DanteAction.ATTACKING.active);
            animations[AnimationState.ATTACK1.ordinal()].setFrameDuration(3, DanteAction.ATTACKING.recovery);

            animations[AnimationState.ATTACK2.ordinal()] = new Animation(atkWindup, VisualAssets.Dante.get("Attack2"));
            animations[AnimationState.ATTACK2.ordinal()].setFrameDuration(2, DanteAction.ATTACKING.active);
            animations[AnimationState.ATTACK2.ordinal()].setFrameDuration(3, DanteAction.ATTACKING.recovery);

            animations[AnimationState.HIT.ordinal()] = new Animation(6, VisualAssets.Dante.get("Hit"));
            animations[AnimationState.DEATH.ordinal()] = new Animation(TRANSITION_TIME * 2 / VisualAssets.Dante.get("Death").length, VisualAssets.Dante.get("Death"));
            animations[AnimationState.DEATH.ordinal()].setFrameDuration(VisualAssets.Dante.get("Death").length - 1, -1);

            animations[AnimationState.ICE_MORPH.ordinal()] = new Animation(6, VisualAssets.Dante.get("Ice_Morph"));
            animations[AnimationState.ICE_MORPH.ordinal()].setFrameDuration(VisualAssets.Dante.get("Ice_Morph").length - 1, -1);

            animations[AnimationState.EDEN_SHIELD.ordinal()] = new Animation(DanteAction.EDEN_SHIELD.windup, VisualAssets.Dante.get("Eden_Shield"));
            animations[AnimationState.EDEN_SHIELD.ordinal()].setFrameDuration(1, DanteAction.EDEN_SHIELD.active);
            animations[AnimationState.EDEN_SHIELD.ordinal()].setFrameDuration(2, DanteAction.EDEN_SHIELD.recovery);

            animations[AnimationState.FIERY_JUDGEMENT.ordinal()] = new Animation(DanteAction.FIERY_JUDGEMENT.windup, VisualAssets.Dante.get("Fiery_Judgement"));
            animations[AnimationState.FIERY_JUDGEMENT.ordinal()].setFrameDuration(1, DanteAction.FIERY_JUDGEMENT.active);
            animations[AnimationState.FIERY_JUDGEMENT.ordinal()].setFrameDuration(2, DanteAction.FIERY_JUDGEMENT.recovery);
        } catch (Exception e) {
            System.out.println("Eroare animatii Dante: " + e.getMessage());
        }
    }

    /**
     * Metoda neimplementata, verificam interactiunile restul entitotilor cu Dante, nu invers
     * @param e Entitatea de interactionat
     */
    @Override
    public void onInteraction(Entity e) {
    }

    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public float getCooldownProgress() { return (float) cooldowns[DanteAction.FIERY_JUDGEMENT.ordinal()] / cd; }
    public Ranks getCurrRank() { return currRank; }
    public void resetDeath() { animations[AnimationState.DEATH.ordinal()].reset(); }
}