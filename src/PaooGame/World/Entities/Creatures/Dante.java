package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Projectile;
import PaooGame.World.Tile;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Dante extends Creature {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(
            4, 1, DEFAULT_WIDTH - 8, DEFAULT_HEIGHT - 2
    );

    public enum Direction {
        RIGHT,
        UP,
        LEFT
    }
    private Direction currDirection = Direction.RIGHT;

    private static final float MAX_HP = 1000;
    private static final float BASE_REGEN = 5f / 60;
    private final float REGEN_PERCENT;
    private final float RESISTANCE;

    public static final float MAX_SPEED = 15f;
    public static final float SPEED = 5f;
    private static final float ACCELERATION = 0.2f;
    private static final float FRICTION = 0.6f;

    private static final float GRAVITY = 0.5f;
    private static final float JUMP_IMPULSE = -10.0f;
    private static final float JUMP_ADDAGE = -0.6f;
    private static final int MAX_JUMP_TIME = 20;
    private int jumpTimer = 0;
    private boolean isJumping;
    private boolean onGround = false;

    public enum MovementState {
        IDLE,
        RUNNING,
        JUMPING,
        FALLING,
        HURT,
        DEAD
    }
    private MovementState currMove = MovementState.IDLE;

    public enum ActionState {
        IDLE(0, 0, 0),
        ATTACKING(5, 7, 40),
        ICE_MORPH(3, 1, 0),
        EDEN_SHIELD(0, 15, 300),
        FIERY_JUDGEMENT(10, 1, 600);

        private final int windup;
        private final int active;
        private final int baseCooldown;

        ActionState(int windup, int active, int baseCooldown) {
            this.windup = windup;
            this.active = active;
            this.baseCooldown = baseCooldown;
        }
        public int getDuration() { return windup + active; }
    }
    private ActionState currAction = ActionState.IDLE;
    private int actionTimer = 0;
    private final int[] cooldowns = new int[ActionState.values().length];

    public enum Ranks {
        D(3),
        C(5),
        B(7),
        A(15),
        S(-1);

        private final int points;
        Ranks(int points) {
            this.points = points;
        }
    }
    private Ranks currRank = Ranks.D;
    private int currPoints;
    private static final float RANK_GAIN_DMG = 0.2f;
    private static final float RANK_GAIN_CD = 0.05f;

    private static final float BASE_ATTACK_DMG = 50f;
    private static final float KNOCKBACK_FORCE = 15f;
    private int attackDmg;
    private final Rectangle attackBounds = new Rectangle();

    public static final int ICE_TIMER = 600;

    private static final int EDEN_SHIELD_PADDING = TILE_SIZE;
    private final int EDEN_SHIELD_DURATION;
    private final Rectangle edenShieldBounds = new Rectangle();

    public static final int FIRE_TIMER = 300;
    public static final int FIERY_JUDGEMENT_DMG = 50;
    private static final int FIERY_JUDGEMENT_BASE_CD = 600;
    private static final int FIERY_JUDGEMENT_SPREAD = 40;
    private final float FIERY_JUDGEMENT_DIFF_CD_PERCENT;
    private int cd;

    public Dante(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_HP);

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
        EDEN_SHIELD_DURATION = switch (handler.getDifficulty()) {
            case EASY -> 20;
            case HARD -> 8;
            default -> 15;
        };
        FIERY_JUDGEMENT_DIFF_CD_PERCENT = switch (handler.getDifficulty()) {
            case EASY -> 0.8f;
            case HARD -> 1.2f;
            default -> 1f;
        };

        this.bounds = new Rectangle(BOUNDS);
    }

    private void updateHealth() {
        if (invulnerabilityTimer <= 0) {
            hp += BASE_REGEN * REGEN_PERCENT;
            if (hp > MAX_HP) {
                hp = MAX_HP;
            }
        }
    }

    private void updateRank() {
        if(currRank != Ranks.S && currPoints >= currRank.points) {
            currPoints -= currRank.points;
            currRank = Ranks.values()[currRank.ordinal() + 1];
        }

        float attack_modifier = 1f + currRank.ordinal() * RANK_GAIN_DMG;
        float cd_modifier = 1f - currRank.ordinal() * RANK_GAIN_CD;

        attackDmg = (int) (BASE_ATTACK_DMG * attack_modifier);
        cd = (int) (FIERY_JUDGEMENT_BASE_CD * FIERY_JUDGEMENT_DIFF_CD_PERCENT * cd_modifier);
    }

    private void applyGravity() {
        yMove += GRAVITY;
        if (y + DEFAULT_HEIGHT >= (handler.getLevel().getHeight() - 1) * TILE_SIZE) {
            die();
        }
    }

    private void getDir() {
        if (handler.getInputManager().up) {
            currDirection = Direction.UP;
        }
        else if (handler.getInputManager().left) {
            currDirection = Direction.LEFT;
        }
        else if (handler.getInputManager().right) {
            currDirection = Direction.RIGHT;
        }
    }

    private void updateCooldowns() {
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
            }
        }
    }

    @Override
    public void Update() {
        if (currMove == MovementState.DEAD) {
            return;
        }

        super.Update();

        updateHealth();
        updateRank();
        applyGravity();
        getDir();

        updateCooldowns();
        updateMovementState();

        if (currMove != MovementState.HURT) {
            updateActionState();
        }
        else {
            currAction = ActionState.IDLE;
            actionTimer = 0;
        }
    }

    private void updateMovementState() {
        if (knockbackTimer > 0) {
            currMove = MovementState.HURT;
            xMove *= FRICTION;
            moveX();
            moveY();
            return;
        }

        if (!onGround) {
            if (yMove < 0) {
                currMove = MovementState.JUMPING;
            }
            else {
                currMove = MovementState.FALLING;
            }
        }
        else if (handler.getInputManager().right || handler.getInputManager().left) {
            currMove = MovementState.RUNNING;
        }
        else {
            currMove = MovementState.IDLE;
        }

        handleHorizontalMovement();
        handleJumpLogic();

        if (currAction == ActionState.ATTACKING && onGround) {
            xMove = 0;
        }

        moveX();
        moveY();
    }

    private void handleHorizontalMovement() {
        if (!isOnIce()) {
            if (handler.getInputManager().right) {
                xMove = SPEED;
            }
            else if (handler.getInputManager().left) {
                xMove = -SPEED;
            }
            else {
                xMove *= FRICTION;
            }
        }
        else {
            if (handler.getInputManager().right) {
                xMove += ACCELERATION;
                if (xMove < SPEED) xMove = SPEED;
                if (xMove > MAX_SPEED) xMove = MAX_SPEED;
            }
            else if (handler.getInputManager().left) {
                xMove -= ACCELERATION;
                if (xMove > -SPEED) xMove = -SPEED;
                if (xMove < -MAX_SPEED) xMove = -MAX_SPEED;
            }
            else {
                xMove *= FRICTION;
            }
        }
    }

    private void handleJumpLogic() {
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
            }
            else {
                isJumping = false;
            }
        }

        if (!handler.getInputManager().space) {
            isJumping = false;
            if (yMove < 0) {
                yMove *= GRAVITY;
            }
        }
    }

    private void checkInputsForNewAction() {
        if (handler.getInputManager().k && cooldowns[ActionState.EDEN_SHIELD.ordinal()] <= 0 && onGround) {
            currAction = ActionState.EDEN_SHIELD;
            actionTimer = EDEN_SHIELD_DURATION;
            cooldowns[currAction.ordinal()] = currAction.baseCooldown;
            return;
        }

        if (handler.getInputManager().j && cooldowns[ActionState.ATTACKING.ordinal()] <= 0) {
            currAction = ActionState.ATTACKING;
            actionTimer = currAction.getDuration();
            cooldowns[currAction.ordinal()] = currAction.baseCooldown;
            handler.getPadManager().rumble(0.0f, 0.5f, 100);
            return;
        }

        if (handler.getInputManager().l && cooldowns[ActionState.FIERY_JUDGEMENT.ordinal()] <= 0) {
            currAction = ActionState.FIERY_JUDGEMENT;
            actionTimer = currAction.getDuration();
            cooldowns[currAction.ordinal()] = cd;
            handler.getPadManager().rumble(0.8f, 0.8f, 200);
            return;
        }

        if (handler.getInputManager().shift && cooldowns[ActionState.ICE_MORPH.ordinal()] <= 0) {
            currAction = ActionState.ICE_MORPH;
            actionTimer = currAction.getDuration();
            cooldowns[currAction.ordinal()] = currAction.baseCooldown;
        }
    }

    private void updateActionState() {
        switch (currAction) {
            case IDLE:
                checkInputsForNewAction();
                break;
            case ATTACKING:
                handleAttackState();
                break;
            case ICE_MORPH:
                handleIceMorphState();
                break;
            case EDEN_SHIELD:
                handleEdenShieldState();
                break;
            case FIERY_JUDGEMENT:
                handleFieryJudgementState();
                break;
        }
    }

    private void handleAttackState() {
        actionTimer--;

        if (actionTimer <= currAction.active) {
            int vWidth = (int) (bounds.width * 1.5f);
            int vHeight = TILE_SIZE * 3;
            int hWidth = TILE_SIZE * 4;
            int hHeight = (int) (bounds.height * 0.5f);

            switch (currDirection) {
                case RIGHT -> {
                    attackBounds.setSize(hWidth, hHeight);
                    attackBounds.x = (int) (x + bounds.x + bounds.width);
                    attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);
                }
                case UP -> {
                    attackBounds.setSize(vWidth, vHeight);
                    attackBounds.x = (int) (x + bounds.x - (float) (vWidth - bounds.width) / 2);
                    attackBounds.y = (int) (y + bounds.y - vHeight);
                }
                case LEFT -> {
                    attackBounds.setSize(hWidth, hHeight);
                    attackBounds.x = (int) (x + bounds.x - hWidth);
                    attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);
                }
            }

            for (Entity e : handler.getEntities()) {
                if (e instanceof Enemy target) {
                    if (attackBounds.intersects(target.getCollisionBounds(0f, 0f))) {
                        if (target.hurt(attackDmg)) {
                            ++currPoints;
                            handler.getScoreMan().addSuccessfulHit(currRank);

                            if (currDirection == Direction.RIGHT) {
                                target.applyKnockback(KNOCKBACK_FORCE * target.getKnockPercent(), -5.0f);
                            }
                            else if (currDirection == Direction.LEFT) {
                                target.applyKnockback(-KNOCKBACK_FORCE * target.getKnockPercent(), -5.0f);
                            }
                            else {
                                target.applyKnockback(0, -10.0f);
                            }
                        }
                    }
                }
            }
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    private void handleIceMorphState() {
        actionTimer--;

        if (actionTimer <= currAction.active && actionTimer > 0) {
            getDir();
            int centerX = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            int startX = (currDirection != Direction.LEFT) ? centerX - 2 : centerX - 5;
            int endX = (currDirection != Direction.LEFT) ? centerX + 5 : centerX + 2;
            int startY = centerY - 1;
            int endY = centerY + 2;

            for (int tx = startX; tx <= endX; tx++) {
                for (int ty = startY; ty <= endY; ty++) {
                    if (tx >= 0 && tx < handler.getLevel().getWidth() &&
                            ty >= 0 && ty < handler.getLevel().getHeight()) {
                        handler.getLevel().setIce(tx, ty, ICE_TIMER);
                    }
                }
            }
        }

        if (actionTimer <= 0) {
            if (handler.getInputManager().shift) {
                actionTimer = currAction.getDuration();
            }
            else {
                currAction = ActionState.IDLE;
            }
        }
    }

    private void handleEdenShieldState() {
        actionTimer--;

        if (actionTimer <= currAction.active) {
            edenShieldBounds.x = (int) (x + bounds.x - EDEN_SHIELD_PADDING);
            edenShieldBounds.y = (int) (y + bounds.y - EDEN_SHIELD_PADDING);
            edenShieldBounds.width = bounds.width + (EDEN_SHIELD_PADDING * 2);
            edenShieldBounds.height = bounds.height + (EDEN_SHIELD_PADDING * 2);
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    private void handleFieryJudgementState() {
        actionTimer--;

        if (actionTimer == currAction.active) {
            int centerX = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height + 1) / TILE_SIZE;

            spreadFire(centerX, centerY, FIERY_JUDGEMENT_SPREAD);
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    private boolean isOnIce() {
        int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 8) / TILE_SIZE;
        return handler.getLevel().isIce(tx, ty);
    }

    private void spreadFire(int tx, int ty, int remainingSteps) {
        if (remainingSteps <= 0) {
            return;
        }

        if (tx < 0 || ty < 0 || tx >= handler.getLevel().getWidth() || ty >= handler.getLevel().getHeight()) {
            return;
        }

        Tile t = handler.getLevel().getTile(tx, ty);
        if (t == null || !t.isSolid()) {
            return;
        }

        if (handler.getLevel().isFire(tx, ty)) {
            return;
        }

        if (handler.getLevel().isFire(tx, ty) || handler.getLevel().isIce(tx, ty)) return;

        handler.getLevel().setFire(tx, ty, FIRE_TIMER);

        spreadFire(tx + 1, ty, remainingSteps - 1);
        spreadFire(tx - 1, ty, remainingSteps - 1);
        spreadFire(tx, ty + 1, remainingSteps - 1);
        spreadFire(tx, ty - 1, remainingSteps - 1);
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                bounds.width, bounds.height);

        if (currAction == ActionState.EDEN_SHIELD) {
            g.setColor(new Color(0, 255, 0));
            g.fillRect((int) (edenShieldBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (edenShieldBounds.y - handler.getGameCamera().getyOffset()),
                    edenShieldBounds.width, edenShieldBounds.height);
        }

        if (currAction == ActionState.ATTACKING && actionTimer <= currAction.active) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                    attackBounds.width, attackBounds.height);

            g.setColor(Color.BLACK);
            g.drawRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                    attackBounds.width, attackBounds.height);
        }
    }

    public void projectileHit(Projectile p) {
        if (currAction == ActionState.EDEN_SHIELD && edenShieldBounds.intersects(p.getCollisionBounds(0f, 0f))) {
            p.reflect(this);
            currPoints += 3;
            handler.getScoreMan().addReflect();
            handler.getPadManager().rumble(0.6f, 0.6f, 150);
            return;
        }

        if (hurt(p.getDamage())) {
            float knockX = (p.getSpeedX() > 0) ? p.getKnockbackForce() * p.getKnockPercent() : -p.getKnockbackForce() * p.getKnockPercent();
            applyKnockback(knockX, -5f);
        }
    }

    @Override
    public boolean hurt(float dmg) {
        if (invulnerabilityTimer > 0 || currAction == ActionState.EDEN_SHIELD) {
            return false;
        }

        if(currRank != Ranks.D) {
            currRank = Ranks.values()[currRank.ordinal() - 1];
        }

        currPoints = 0;
        hp -= dmg * RESISTANCE;
        invulnerabilityTimer = INVULN_TIME;
        handler.getPadManager().rumble(0.8f, 0.8f, 200);

        if (hp <= 0) {
            die();
        }
        return true;
    }

    @Override
    public void applyKnockback(float forceX, float forceY) {
        super.applyKnockback(forceX, forceY);
        onGround = false;
    }

    @Override
    public void die() {
        currMove = MovementState.DEAD;
        currAction = ActionState.IDLE;
        currRank = Ranks.D;
        currPoints = 0;

        yMove = 0;
        xMove = 0;

        setHp(MAX_HP);
        handler.getPadManager().rumble(0.8f, 0.8f, 300);
        handler.getLevel().requestRespawn();
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public void onInteraction(Entity e) {}
}