package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.Projectile;
import PaooGame.World.Tile;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Dante extends Creature {
    public enum Direction { RIGHT, UP, LEFT }
    private Direction currDirection = Direction.LEFT;

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

    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(
            4, 1, DEFAULT_WIDTH - 8, DEFAULT_HEIGHT - 2
    );

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

    private static final int ATTACK_COOLDOWN = 30;
    private static final int ATTACK_DURATION = 12;
    private static final float BASE_ATTACK_DMG = 50f;
    private static final float KNOCKBACK_FORCE = 15f;
    private int attackTimer = 0;
    private int attackActiveTimer = 0;
    private int attackDmg;
    private boolean isAttacking = false;
    private Rectangle attackBounds = new Rectangle();

    public static final int ICE_TIMER = 600;
    public static final int FIRE_TIMER = 300;
    public static final int FIERY_JUDGEMENT_DMG = 50;

    private static final int EDEN_SHIELD_COOLDOWN = 300;
    private static final int EDEN_SHIELD_PADDING = TILE_SIZE;
    private final int EDEN_SHIELD_DURATION;
    private boolean edenShieldActive = false;
    private int edenShieldTimer = 0;
    private int edenActiveTimer = 0;
    private Rectangle edenShieldBounds = new Rectangle();

    private static final int FIERY_JUDGEMENT_BASE_CD = 600;
    private static final int FIERY_JUDGEMENT_SPREAD = 40;
    private final float FIERY_JUDGEMENT_DIFF_CD_PERCENT;
    private int fieryJudgementTimer = 0;
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

    @Override
    public void Update() {
        updateHealth();
        updateRank();
        applyGravity();
        getDir();

        move();
        jump();
        attack();
        iceMorph();
        edenShield();
        fieryJudgement();
    }
    private void updateHealth() {
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer--;
        }
        else {
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


    private boolean isOnIce() {
        int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 8) / TILE_SIZE;
        return handler.getLevel().isIce(tx, ty);
    }
    @Override
    public void moveX() {
        if (knockbackTimer > 0) {
            knockbackTimer--;
            xMove *= FRICTION;
            super.moveX();
            return;
        }

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

        super.moveX();
    }
    private void jump() {
        if(handler.getInputManager().space && onGround) {
            yMove = JUMP_IMPULSE;
            onGround = false;
            isJumping = true;
            jumpTimer = 0;
        }

        if (handler.getInputManager().space && isJumping) {
            if (jumpTimer < MAX_JUMP_TIME) {
                yMove += JUMP_ADDAGE;
                jumpTimer++;
            }
            else {
                isJumping = false;
            }
        }

        if (!handler.getInputManager().space) {
            isJumping = false;
        }
    }

    private void attack() {
        if (attackTimer > 0) {
            attackTimer--;
        }

        if (isAttacking) {
            attackActiveTimer--;
            if (attackActiveTimer <= 0) {
                isAttacking = false;
                return;
            }
        }

        if (handler.getInputManager().j && attackTimer <= 0 && !handler.getInputManager().shift) {
            isAttacking = true;
            attackActiveTimer = ATTACK_DURATION;
            attackTimer = ATTACK_COOLDOWN;

            handler.getPadManager().rumble(0.0f, 0.5f, 100);
        }

        if (isAttacking) {
            int vWidth = (int) (bounds.width * 1.5f);
            int vHeight = TILE_SIZE * 3;
            int hWidth = TILE_SIZE * 4;
            int hHeight = (int) (bounds.height * 0.5f);

            switch (currDirection) {
                case Direction.RIGHT -> {
                    attackBounds.setSize(hWidth, hHeight);
                    attackBounds.x = (int) (x + bounds.x + bounds.width);
                    attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);
                }
                case Direction.UP -> {
                    attackBounds.setSize(vWidth, vHeight);
                    attackBounds.x = (int) (x + bounds.x - (float) (vWidth - bounds.width) / 2);
                    attackBounds.y = (int) (y + bounds.y - vHeight);
                }
                case Direction.LEFT -> {
                    attackBounds.setSize(hWidth, hHeight);
                    attackBounds.x = (int) (x + bounds.x - hWidth);
                    attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);
                }
            }

            for (Entity e : handler.getLevel().getEntityManager().getEntities()) {
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
    }
    private void iceMorph() {
        if (handler.getInputManager().shift) {
            getDir();
            int centerX = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            int startX, endX;

            if (currDirection != Direction.LEFT) {
                startX = centerX - 2;
                endX = centerX + 5;
            }
            else {
                startX = centerX - 5;
                endX = centerX + 2;
            }

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
    }

    private void edenShield() {
        if (edenShieldTimer > 0) {
            edenShieldTimer--;
        }

        if (edenShieldActive) {
            edenActiveTimer--;

            edenShieldBounds.x = (int) (x + bounds.x - Dante.EDEN_SHIELD_PADDING);
            edenShieldBounds.y = (int) (y + bounds.y - Dante.EDEN_SHIELD_PADDING);
            edenShieldBounds.width = bounds.width + (Dante.EDEN_SHIELD_PADDING * 2);
            edenShieldBounds.height = bounds.height + (Dante.EDEN_SHIELD_PADDING * 2);

            if (edenActiveTimer <= 0) {
                edenShieldActive = false;
            }
        }

        if (handler.getInputManager().k && edenShieldTimer <= 0 && onGround) {
            edenShieldActive = true;
            edenActiveTimer = EDEN_SHIELD_DURATION;
            edenShieldTimer = EDEN_SHIELD_COOLDOWN;
        }
    }
    private void fieryJudgement() {
        if (fieryJudgementTimer > 0) {
            fieryJudgementTimer--;
        }

        if (handler.getInputManager().l && fieryJudgementTimer <= 0) {
            int centerX = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height + 1) / TILE_SIZE;

            spreadFire(centerX, centerY, FIERY_JUDGEMENT_SPREAD);
            fieryJudgementTimer = cd;

            handler.getPadManager().rumble(0.8f, 0.8f, 200);
        }
    }

    private void spreadFire(int tx, int ty, int remainingSteps) {
        if (remainingSteps <= 0) {
            return;
        }

        if (tx < 0 || ty < 0 || tx >= handler.getLevel().getWidth() || ty >= handler.getLevel().getHeight()) {
            return;
        }

        Tile t = handler.getLevel().getTile(tx, ty);
        if (t == null || !t.isSolid()) return;

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

        if (edenShieldActive) {
            g.setColor(new Color(0, 255, 0));
            g.fillRect((int) (edenShieldBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (edenShieldBounds.y - handler.getGameCamera().getyOffset()),
                    edenShieldBounds.width, edenShieldBounds.height);
        }

        if (isAttacking) {
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

    @Override
    public void onInteraction(Entity e) {}

    public void projectileHit(Projectile p) {
        if (edenShieldActive && edenShieldBounds.intersects(p.getCollisionBounds(0f, 0f))) {
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
        if (invulnerabilityTimer > 0 || edenShieldActive) {
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
}
