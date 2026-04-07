package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tiles.Tile;

import java.awt.*;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class Dante extends Creature {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;

    public static final float MAX_SPEED = 15f;
    public static final float SPEED = 5f;
    private static final float ACCELERATION = 0.2f;
    private static final float FRICTION = 0.6f;

    private static final float GRAVITY = 0.5f;
    private static final float JUMP_IMPULSE = -10.0f;
    private static final float JUMP_ADDAGE = -0.6f;
    private static final int MAX_JUMP_TIME = 20;

    private static final int ICE_TIMER = 600, FIRE_TIMER = 600;
    private int direction = 2;

    private boolean edenShieldActive = false;
    private static final int EDEN_SHIELD_COOLDOWN = 300;
    private int edenShieldTimer = 0;
    private static final int EDEN_SHIELD_DURATION = 15;
    private int edenActiveTimer = 0;

    private static final int FIERY_JUDGEMENT_COOLDOWN = 600;
    private static final int FIERY_JUDGEMENT_SPREAD = 40;
    private int fieryJudgementTimer = 0;

    private int jumpTimer = 0;
    private boolean isJumping;
    private boolean onGround = false;

    private long lastAttackTimer, attackCooldown = 500, attackTimer = attackCooldown;
    private boolean isAttacking = false;
    private Rectangle attackBounds = new Rectangle();

    public Dante(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, 1000);


        bounds.x = 4;
        bounds.width = DEFAULT_WIDTH - 8;

        bounds.y = 1;
        bounds.height = DEFAULT_HEIGHT - 2;
    }

    @Override
    public void Update() {
        applyGravity();
        getDir();

        move();
        jump();
        attack();
        iceMorph();
        edenShield();
        fieryJudgement();
    }

    private void applyGravity() {
        yMove += GRAVITY;
    }
    private void getDir() {
        if (handler.getKeyManager().up) {
            direction = 1;
        }
        else if (handler.getKeyManager().left) {
            direction = 2;
        }
        else {
            direction = 0;
        }
    }


    private boolean isOnIce() {
        int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 8) / TILE_SIZE;
        return handler.getLevel().isIce(tx, ty);
    }
    @Override
    public void moveX() {
        if (!isOnIce()) {
            if (handler.getKeyManager().right) {
                xMove = SPEED;
            }
            else if (handler.getKeyManager().left) {
                xMove = -SPEED;
            }
            else {
                xMove *= FRICTION;
            }
        }
        else {
            if (handler.getKeyManager().right) {
                xMove += ACCELERATION;
                if (xMove < SPEED) xMove = SPEED;
                if (xMove > MAX_SPEED) xMove = MAX_SPEED;
            }
            else if (handler.getKeyManager().left) {
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
    @Override
    public void moveY() {
        if (yMove < 0) {
            int ty = (int) (y + yMove + bounds.y) / TILE_SIZE;
            if (!collisionWithTile((int) (x + bounds.x) / TILE_SIZE, ty) &&
                    !collisionWithTile((int) (x + bounds.x + bounds.width) / TILE_SIZE, ty)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE + TILE_SIZE - bounds.y;
                yMove = 0;
            }
        }
        else if (yMove > 0) {
            int ty = (int) (y + yMove + bounds.y + bounds.height) / TILE_SIZE;
            if (!collisionWithTile((int) (x + bounds.x) / TILE_SIZE, ty) &&
                    !collisionWithTile((int) (x + bounds.x + bounds.width) / TILE_SIZE, ty)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE - bounds.y - bounds.height - 1;
                onGround = true;
                yMove = 0;
            }
        }
    }
    private void jump() {
        if(handler.getKeyManager().space && onGround) {
            yMove = JUMP_IMPULSE;
            onGround = false;
            isJumping = true;
            jumpTimer = 0;
        }

        if (handler.getKeyManager().space && isJumping) {
            if (jumpTimer < MAX_JUMP_TIME) {
                yMove += JUMP_ADDAGE;
                jumpTimer++;
            }
            else {
                isJumping = false;
            }
        }

        if (!handler.getKeyManager().space) {
            isJumping = false;
        }
    }

    private void attack() {
        attackTimer += System.currentTimeMillis() - lastAttackTimer;
        lastAttackTimer = System.currentTimeMillis();

        if (handler.getKeyManager().j && attackTimer >= attackCooldown) {
            isAttacking = true;
            attackTimer = 0;
        }

        if (isAttacking) {
            if (attackTimer >= 200) {
                isAttacking = false;
                return;
            }

            int verticalWidth = (int) (bounds.width * 1.5f);
            int verticalHeight = TILE_SIZE * 2;
            int horizontalWidth = TILE_SIZE * 2;
            int horizontalHeight = (int) (bounds.height * 0.5f);

            switch (direction) {
                case 0:
                    attackBounds.setSize(horizontalWidth, horizontalHeight);
                    attackBounds.x = (int) (x + bounds.x + bounds.width);
                    attackBounds.y = (int) (y + bounds.y + (bounds.height - horizontalHeight) / 2);
                    break;
                case 1:
                    attackBounds.setSize(verticalWidth, verticalHeight);
                    attackBounds.x = (int) (x + bounds.x - (verticalWidth - bounds.width) / 2);
                    attackBounds.y = (int) (y + bounds.y - verticalHeight);
                    break;
                case 2:
                    attackBounds.setSize(horizontalWidth, horizontalHeight);
                    attackBounds.x = (int) (x + bounds.x - horizontalWidth);
                    attackBounds.y = (int) (y + bounds.y + (bounds.height - horizontalHeight) / 2);
                    break;
            }
        }
    }
    private void iceMorph() {
        if (handler.getKeyManager().shift) {

            getDir();
            int centerX = (int) (x + bounds.x + bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            int startX, endX;

            if (direction == 0 || direction == 1) {
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
                    handler.getLevel().setIce(tx, ty, ICE_TIMER);
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
            if (edenActiveTimer <= 0) {
                edenShieldActive = false;
            }
        }

        if (handler.getKeyManager().k && edenShieldTimer <= 0 && onGround) {
            edenShieldActive = true;
            edenActiveTimer = EDEN_SHIELD_DURATION;
            edenShieldTimer = EDEN_SHIELD_COOLDOWN;
        }
    }
    private void fieryJudgement() {
        if (fieryJudgementTimer > 0) {
            fieryJudgementTimer--;
        }

        if (handler.getKeyManager().l && fieryJudgementTimer <= 0) {
            int centerX = (int) (x + bounds.x + bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height + 1) / TILE_SIZE;

            spreadFire(centerX, centerY, FIERY_JUDGEMENT_SPREAD);

            fieryJudgementTimer = FIERY_JUDGEMENT_COOLDOWN;
            System.out.println("Fiery Judgement has been cast!");
        }
    }

    private void spreadFire(int tx, int ty, int remainingSteps) {
        if (remainingSteps <= 0) {
            return;
        }

        Tile t = handler.getLevel().getTile(tx, ty);
        if (t == null || !t.isSolid()) return;

        if (handler.getLevel().isFire(tx, ty)) return;

        handler.getLevel().setFire(tx, ty, FIRE_TIMER);

        spreadFire(tx + 1, ty, remainingSteps - 1);
        spreadFire(tx - 1, ty, remainingSteps - 1);
        spreadFire(tx, ty + 1, remainingSteps - 1);
        spreadFire(tx, ty - 1, remainingSteps - 1);
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.RED);
        if (edenShieldActive) {
            g.setColor(Color.GREEN);
        }
        g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                bounds.width, bounds.height);

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

    @Override
    public void hurt(float dmg) {}

    @Override
    public void die() {}
}
