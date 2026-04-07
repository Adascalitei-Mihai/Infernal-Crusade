package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tiles.Tile;

import java.awt.*;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class Dante extends Creature {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;

    public static final float MAX_SPEED = 5f;
    private static final float FRICTION = 0.9f;
    private static final float ICE_FRICTION = 0.98f;
    private static final float ACCELERATION = 1.2f;
    private static final float GRAVITY = 0.5f;
    private static final float JUMP_IMPULSE = -10.0f;
    private static final float JUMP_ADDAGE = -0.6f;
    private static final int MAX_JUMP_TIME = 20;

    private int jumpTimer = 0;
    private boolean isJumping;

    private long lastAttackTimer, attackCooldown = 500, attackTimer = attackCooldown;
    private int attackDir = 0;
    private boolean isAttacking = false;
    private Rectangle attackBounds = new Rectangle();

    private boolean onGround = false;

    public Dante(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, 1000);


        bounds.x = 4;
        bounds.width = DEFAULT_WIDTH - 8;

        bounds.y = 1;
        bounds.height = DEFAULT_HEIGHT - 2;
    }

    @Override
    public void Update() {
        applyPhysics();
        getInput();
        checkAttack();
        move();
    }

    private void checkAttack() {
        attackTimer += System.currentTimeMillis() - lastAttackTimer;
        lastAttackTimer = System.currentTimeMillis();

        if (handler.getKeyManager().j && attackTimer >= attackCooldown) {
            isAttacking = true;
            attackTimer = 0;

            if (handler.getKeyManager().up) {
                attackDir = 0;
            } else if (handler.getKeyManager().left) {
                attackDir = 1;
            } else if (handler.getKeyManager().right) {
                attackDir = 2;
            } else {
                attackDir = 2;
            }
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

            switch (attackDir) {
                case 0:
                    attackBounds.setSize(verticalWidth, verticalHeight);
                    attackBounds.x = (int) (x + bounds.x - (verticalWidth - bounds.width) / 2);
                    attackBounds.y = (int) (y + bounds.y - verticalHeight);
                    break;
                case 1:
                    attackBounds.setSize(horizontalWidth, horizontalHeight);
                    attackBounds.x = (int) (x + bounds.x - horizontalWidth);
                    attackBounds.y = (int) (y + bounds.y + (bounds.height - horizontalHeight) / 2);
                    break;
                case 2:
                    attackBounds.setSize(horizontalWidth, horizontalHeight);
                    attackBounds.x = (int) (x + bounds.x + bounds.width);
                    attackBounds.y = (int) (y + bounds.y + (bounds.height - horizontalHeight) / 2);
                    break;
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.RED);
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

    private void getInput() {
        if(handler.getKeyManager().right) {
            xMove += ACCELERATION;
        }
        else if(handler.getKeyManager().left) {
            xMove -= ACCELERATION;
        }
        else {
            xMove *= getFrictionCoefficient();
        }

        if(xMove > MAX_SPEED) {
            xMove = MAX_SPEED;
        }
        if(xMove < -MAX_SPEED) {
            xMove = -MAX_SPEED;
        }

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

    private float getFrictionCoefficient() {
        if (handler.getLevel() == null) {
            return FRICTION;
        }

        int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 1) / TILE_SIZE;

        Tile t = handler.getLevel().getTile(tx, ty);

        if (t != null && t.isIce()) {
            return ICE_FRICTION;
        }

        return FRICTION;
    }

    private void applyPhysics() {
        yMove += GRAVITY;
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
}
