package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tiles.Tile;

import java.awt.*;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class Skeleton extends GroundEnemy {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;

    private static final int ATTACK_COOLDOWN = 60;
    private static final int ATTACK_DURATION = 15;
    private int attackTimer = 0;
    private int attackActiveTimer = 0;

    private boolean isAttacking = false;
    private Rectangle attackBounds = new Rectangle();
    private final float ATTACK_DMG;

    public Skeleton(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, 100);

        bounds.x = 4;
        bounds.width = DEFAULT_WIDTH - 8;
        bounds.y = 1;
        bounds.height = DEFAULT_HEIGHT - 2;

        this.baseSpeed = 2.0f;
        this.aggroSpeed = 3.0f;
        this.attackRange = TILE_SIZE * 2;
        this.aggroRange = TILE_SIZE * 18;
        this.patrolRange = new float[]{x - Tile.TILE_SIZE * 5, x + Tile.TILE_SIZE * 5};

        ATTACK_DMG = switch (handler.getDifficulty()) {
            case EASY -> 25;
            case HARD -> 100;
            default -> 50;
        };
    }

    @Override
    public void Update() {
        if (attackTimer > 0) {
            attackTimer--;
        }

        if (isAttacking) {
            attackActiveTimer--;
            if (attackActiveTimer <= 0) {
                isAttacking = false;
            }
        }

        super.Update();
        move();

        if (currState == EnemyState.ATTACKING) {
            attack();
        }
    }

    @Override
    protected void attack() {
        if (attackTimer > 0) {
            return;
        }

        isAttacking = true;
        attackActiveTimer = ATTACK_DURATION;
        attackTimer = ATTACK_COOLDOWN;

        int hWidth = TILE_SIZE * 2;
        int hHeight = (int) (bounds.height * 0.5f);

        attackBounds.setSize(hWidth, hHeight);

        if (direction == 1) {
            attackBounds.x = (int) (x + bounds.x + bounds.width);
        }
        else {
            attackBounds.x = (int) (x + bounds.x - hWidth);
        }
        attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.ORANGE);
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
    public void onInteraction(Entity e) {
        //handler.getDante().hurt(ATTACK_DMG);
    }
}