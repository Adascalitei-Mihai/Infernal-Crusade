package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Projectile;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;

import java.awt.*;

import static PaooGame.States.VisualElements.TransitionManager.TRANSITION_TIME;
import static PaooGame.World.Tile.TILE_SIZE;

public class Dante extends Creature {
    public static final int LOGIC_WIDTH = 2 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 4 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(
            4, 1, LOGIC_WIDTH - 8, LOGIC_HEIGHT - 2
    );

    public static final int VISUAL_WIDTH = 6 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 6 * TILE_SIZE;

    public enum Direction {
        RIGHT,
        UP,
        LEFT
    }
    private Direction currDirection = Direction.RIGHT;

    public static final float MAX_HP = 1000;
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
    private boolean slipperyMovement = false;

    public enum MovementState {
        IDLE, RUNNING, JUMPING, FALLING, HURT, DEAD
    }
    private MovementState currMove = MovementState.IDLE;

    public enum ActionState {
        IDLE(0, 0, 0, 0),
        ATTACKING(6, 15, 10, 45),
        ICE_MORPH(3, 1, 4, 0),
        EDEN_SHIELD(4, 0, 16, 240),
        FIERY_JUDGEMENT(30, 30, 15, 600);

        public final int windup;
        public int active;
        public final int recovery;
        public final int baseCooldown;

        ActionState(int windup, int active, int recovery, int baseCooldown) {
            this.windup = windup;
            this.active = active;
            this.recovery = recovery;
            this.baseCooldown = baseCooldown;
        }
        public int getDuration() { return windup + active + recovery; }
        public void setActive(int duration) { active = duration; }
    }

    private ActionState currAction = ActionState.IDLE;
    private int actionTimer = 0;
    private final int[] cooldowns = new int[ActionState.values().length];

    public enum Ranks {
        D(3), C(5), B(7), A(15), S(-1);
        private final int points;
        Ranks(int points) { this.points = points; }
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

    private static final int EDEN_SHIELD_PADDING = TILE_SIZE * 2;
    private final Rectangle edenShieldBounds = new Rectangle();

    public static final int FIRE_TIMER = 300;
    public static final int FIERY_JUDGEMENT_DMG = 50;
    private static final int FIERY_JUDGEMENT_BASE_CD = 600;
    private static final int FIERY_JUDGEMENT_SPREAD = 40;
    private final float FIERY_JUDGEMENT_DIFF_CD_PERCENT;
    private int cd;

    public enum AnimationState {
        IDLE, RUN, JUMP, FALL, ATTACK1, ATTACK2, HIT, DEATH, ICE_MORPH, EDEN_SHIELD, FIERY_JUDGEMENT
    }
    private final Animation[] animations = new Animation[AnimationState.values().length];
    private AnimationState currAnim = AnimationState.IDLE;
    private int deathTimer = 0;

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
        ActionState.EDEN_SHIELD.setActive(switch (handler.getDifficulty()) {
            case EASY -> 20;
            case HARD -> 8;
            default -> 15;
        });
        FIERY_JUDGEMENT_DIFF_CD_PERCENT = switch (handler.getDifficulty()) {
            case EASY -> 0.8f;
            case HARD -> 1.2f;
            default -> 1f;
        };

        this.bounds = new Rectangle(BOUNDS);
        setAnimations();
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
        if (y + LOGIC_HEIGHT >= (handler.getLevel().getHeight() - 1) * TILE_SIZE) {
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
        super.Update();

        updateHealth();
        updateRank();
        applyGravity();
        getDir();

        if (currMove == MovementState.DEAD) {
            applyGravity();
            move();

            if (deathTimer > 0) {
                deathTimer--;
                animations[AnimationState.DEATH.ordinal()].Update();
            }
            else {
                handler.getTransitionManager().start(() -> {
                    handler.getLevel().requestRespawn();
                });

                hp = MAX_HP;
                currMove = MovementState.IDLE;
                currAnim = AnimationState.IDLE;
                xMove = 0;
                yMove = 0;
                knockbackTimer = 0;
            }
            return;
        }

        updateCooldowns();
        updateMovementState();

        if (currMove != MovementState.HURT) {
            updateActionState();
        }
        else {
            currAction = ActionState.IDLE;
            actionTimer = 0;
        }

        AnimationState prevAnim = currAnim;
        updateAnimationState();

        if (currAnim != prevAnim) {
            animations[currAnim.ordinal()].reset();
        }

        animations[currAnim.ordinal()].Update();
    }

    private void updateAnimationState() {
        if (currAction != ActionState.IDLE) {
            switch (currAction) {
                case ATTACKING -> {
                    if (currDirection == Direction.UP) currAnim = AnimationState.ATTACK1;
                    else currAnim = AnimationState.ATTACK2;
                }
                case EDEN_SHIELD -> currAnim = AnimationState.EDEN_SHIELD;
                case ICE_MORPH -> currAnim = AnimationState.ICE_MORPH;
                case FIERY_JUDGEMENT -> currAnim = AnimationState.FIERY_JUDGEMENT;
            }
            return;
        }

        switch (currMove) {
            case HURT -> currAnim = AnimationState.HIT;
            case JUMPING -> currAnim = AnimationState.JUMP;
            case FALLING -> currAnim = AnimationState.FALL;
            case RUNNING -> currAnim = AnimationState.RUN;
            default -> currAnim = AnimationState.IDLE;
        }
    }

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

        move();
    }

    private void handleHorizontalMovement() {
        if (!slipperyMovement) {
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
            }
            else {
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

    private void checkInputsForNewAction() {
        if (handler.getInputManager().k && cooldowns[ActionState.EDEN_SHIELD.ordinal()] <= 0 && onGround) {
            currAction = ActionState.EDEN_SHIELD;
            actionTimer = currAction.getDuration();
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

        if (handler.getInputManager().l && cooldowns[ActionState.FIERY_JUDGEMENT.ordinal()] <= 0 && onGround) {
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

        boolean isActiveFrame = actionTimer < (currAction.active + currAction.recovery)
                && actionTimer >= currAction.recovery;

        if (isActiveFrame) {
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
                            currPoints++;
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
        if (actionTimer > 0) {
            actionTimer--;
        }

        boolean isActiveFrame = actionTimer < (currAction.active + currAction.recovery)
                && actionTimer >= currAction.recovery;

        if (isActiveFrame) {
            getDir();
            int centerX = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            int startX = (currDirection != Direction.LEFT) ? centerX - 2 : centerX - 5;
            int endX = (currDirection != Direction.LEFT) ? centerX + 5 : centerX + 2;
            int startY = centerY - 1;
            int endY = centerY + 2;

            for (int tx = startX; tx <= endX; tx++) {
                for (int ty = startY; ty <= endY; ty++) {
                    handler.getLevel().setIce(tx, ty, ICE_TIMER);
                }
            }

            if (handler.getInputManager().shift && actionTimer == currAction.recovery) {
                actionTimer++;
            }
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    private void handleEdenShieldState() {
        actionTimer--;

        boolean isActiveFrame = actionTimer < (currAction.active + currAction.recovery)
                && actionTimer >= currAction.recovery;

        if (isActiveFrame) {
            edenShieldBounds.x = (int) (x + bounds.x - EDEN_SHIELD_PADDING);
            edenShieldBounds.y = (int) (y + bounds.y - EDEN_SHIELD_PADDING);
            edenShieldBounds.width = bounds.width + (EDEN_SHIELD_PADDING * 2);
            edenShieldBounds.height = bounds.height + (EDEN_SHIELD_PADDING * 2);
        } else {
            edenShieldBounds.width = 0;
            edenShieldBounds.height = 0;
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    private void handleFieryJudgementState() {
        actionTimer--;

        if (actionTimer == currAction.active + currAction.recovery - 1) {
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

        if (handler.getLevel().isFire(tx, ty) || handler.getLevel().isIce(tx, ty)) {
            return;
        }

        handler.getLevel().setFire(tx, ty, FIRE_TIMER);

        if (handler.getLevel().isFire(tx, ty)) {
            spreadFire(tx + 1, ty, remainingSteps - 1);
            spreadFire(tx - 1, ty, remainingSteps - 1);
            spreadFire(tx, ty + 1, remainingSteps - 1);
            spreadFire(tx, ty - 1, remainingSteps - 1);
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT);

        if (currDirection == Direction.LEFT) {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX + VISUAL_WIDTH, drawY, -VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }
        else {
            g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                    drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);
        }

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);

            boolean isActiveFrame = actionTimer < (currAction.active + currAction.recovery)
                    && actionTimer >= currAction.recovery;

            if (currAction == ActionState.ATTACKING && isActiveFrame) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                        (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                        attackBounds.width, attackBounds.height);

                g.setColor(Color.BLACK);
                g.drawRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                        (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                        attackBounds.width, attackBounds.height);
            }
            if (currAction == ActionState.EDEN_SHIELD && edenShieldBounds.width > 0) {
                g.setColor(new Color(0, 255, 0, 100));
                g.fillRect((int) (edenShieldBounds.x - handler.getGameCamera().getxOffset()),
                        (int) (edenShieldBounds.y - handler.getGameCamera().getyOffset()),
                        edenShieldBounds.width, edenShieldBounds.height);
            }
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
        if (currMove == MovementState.DEAD) return;
        super.applyKnockback(forceX, forceY);
        onGround = false;
    }

    @Override
    public void die() {
        if (currMove == MovementState.DEAD) return;

        currMove = MovementState.DEAD;
        currAction = ActionState.IDLE;
        currRank = Ranks.D;
        currPoints = 0;

        xMove = 0;
        yMove = 0;
        hp = 0;
        handler.getPadManager().rumble(0.8f, 0.8f, 300);

        deathTimer = TRANSITION_TIME;
        currAnim = AnimationState.DEATH;
    }

    @Override
    public void setAnimations() {
        try {
            animations[AnimationState.IDLE.ordinal()] = new Animation(6, VisualAssets.Dante.get("Idle"));
            animations[AnimationState.RUN.ordinal()] = new Animation(5, VisualAssets.Dante.get("Run"));
            animations[AnimationState.JUMP.ordinal()] = new Animation(6, VisualAssets.Dante.get("Jump"));

            animations[AnimationState.FALL.ordinal()] = new Animation(6, VisualAssets.Dante.get("Fall"));
            animations[AnimationState.FALL.ordinal()].setFrameDuration(VisualAssets.Dante.get("Fall").length - 1, -1);

            int atkWindup = ActionState.ATTACKING.windup / 2;
            animations[AnimationState.ATTACK1.ordinal()] = new Animation(atkWindup, VisualAssets.Dante.get("Attack1"));
            animations[AnimationState.ATTACK1.ordinal()].setFrameDuration(2, ActionState.ATTACKING.active);
            animations[AnimationState.ATTACK1.ordinal()].setFrameDuration(3, ActionState.ATTACKING.recovery);

            animations[AnimationState.ATTACK2.ordinal()] = new Animation(atkWindup, VisualAssets.Dante.get("Attack2"));
            animations[AnimationState.ATTACK2.ordinal()].setFrameDuration(2, ActionState.ATTACKING.active);
            animations[AnimationState.ATTACK2.ordinal()].setFrameDuration(3, ActionState.ATTACKING.recovery);

            animations[AnimationState.HIT.ordinal()] = new Animation(6, VisualAssets.Dante.get("Hit"));

            animations[AnimationState.DEATH.ordinal()] = new Animation(TRANSITION_TIME * 2 / VisualAssets.Dante.get("Death").length, VisualAssets.Dante.get("Death"));
            animations[AnimationState.DEATH.ordinal()].setFrameDuration(VisualAssets.Dante.get("Death").length - 1, -1);

            animations[AnimationState.ICE_MORPH.ordinal()] = new Animation(6, VisualAssets.Dante.get("Ice_Morph"));
            animations[AnimationState.ICE_MORPH.ordinal()].setFrameDuration(VisualAssets.Dante.get("Ice_Morph").length - 1, -1);

            animations[AnimationState.EDEN_SHIELD.ordinal()] = new Animation(ActionState.EDEN_SHIELD.windup, VisualAssets.Dante.get("Eden_Shield"));
            animations[AnimationState.EDEN_SHIELD.ordinal()].setFrameDuration(1, ActionState.EDEN_SHIELD.active);
            animations[AnimationState.EDEN_SHIELD.ordinal()].setFrameDuration(2, ActionState.EDEN_SHIELD.recovery);

            animations[AnimationState.FIERY_JUDGEMENT.ordinal()] = new Animation(ActionState.FIERY_JUDGEMENT.windup, VisualAssets.Dante.get("Fiery_Judgement"));
            animations[AnimationState.FIERY_JUDGEMENT.ordinal()].setFrameDuration(1, ActionState.FIERY_JUDGEMENT.active);
            animations[AnimationState.FIERY_JUDGEMENT.ordinal()].setFrameDuration(2, ActionState.FIERY_JUDGEMENT.recovery);
        }
        catch (Exception e) {
            System.out.println("Eroare la afisare Dante: " + e.getMessage());
        }
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public void onInteraction(Entity e) {}

    public float getCooldownProgress() { return (float) cooldowns[4] / cd;}
    public Ranks getCurrRank() { return currRank; }
    public void resetDeath() {
        animations[AnimationState.DEATH.ordinal()].reset();
    }
}