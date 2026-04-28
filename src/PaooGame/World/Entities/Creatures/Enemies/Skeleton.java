package PaooGame.World.Entities.Creatures.Enemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Tiles.Tile;
import java.awt.Graphics;

public class Skeleton extends Creature {

    // Stări și Animații
    public static final int IDLE = 0, WALK = 1, ATTACK = 2, DIE = 3, HURT = 4;
    private final Animation[] animations = new Animation[5];
    private int currentState = IDLE;
    private int direction = 2; // 2 = Stânga, 0 = Dreapta

    // Configurare AI
    private float walkSpeed = 1.5f;
    private float chaseSpeed = 2.5f;
    private int detectionRange = 300;
    private int attackRange = 70;
    private boolean isAttacking = false;

    public Skeleton(Handler handler, float x, float y) {
        // Constructorul se numește acum exact ca fișierul: Skeleton
        super(handler, x, y, 96, 64, 100);

        bounds.x = 30;
        bounds.y = 20;
        bounds.width = 36;
        bounds.height = 44;

        animations[IDLE] = new Animation(150, Assets.Skeleton.get("Idle"));
        animations[WALK] = new Animation(120, Assets.Skeleton.get("Walk"));
        animations[ATTACK] = new Animation(100, Assets.Skeleton.get("Attack1"));
        animations[HURT] = new Animation(100, Assets.Skeleton.get("Hurt"));
        animations[DIE] = new Animation(150, Assets.Skeleton.get("Die"));
    }

    @Override
    public void Update() {
        //yMove += 0.5f; // Gravitație

        moveAI();
        move(); // Metoda din Creature care aplică xMove și yMove

        updateAnimationState();
        animations[currentState].tick();
    }

    private void moveAI() {
        float playerX = handler.getLevel().getEntityManager().getDante().getX();
        float playerY = handler.getLevel().getEntityManager().getDante().getY();

        float distanceX = Math.abs((x + bounds.x) - playerX);
        float distanceY = Math.abs((y + bounds.y) - playerY);

        if (distanceX <= attackRange && distanceY < 50) {
            xMove = 0;
            isAttacking = true;
        } else if (distanceX <= detectionRange && distanceY < 100) {
            isAttacking = false;
            if (playerX < x + bounds.x) {
                direction = 2;
                xMove = -chaseSpeed;
            } else {
                direction = 0;
                xMove = chaseSpeed;
            }
        } else {
            isAttacking = false;
            // Patrulare simplă
            if (direction == 0) xMove = walkSpeed;
            else xMove = -walkSpeed;

            // Verificare margine platformă
            checkEdges();
        }
    }

    private void checkEdges() {
        float checkX = x + bounds.x + (direction == 0 ? bounds.width + 5 : -5);
        int tileX = (int) (checkX / Tile.TILE_SIZE);
        int tileBelowY = (int) ((y + bounds.y + bounds.height + 5) / Tile.TILE_SIZE);

        if (!handler.getLevel().getTile(tileX, tileBelowY).isSolid()) {
            direction = (direction == 0) ? 2 : 0;
        }
    }

    private void updateAnimationState() {
        if (isAttacking) currentState = ATTACK;
        else if (Math.abs(xMove) > 0.1f) currentState = WALK;
        else currentState = IDLE;
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        if (direction == 2) {
            g.drawImage(animations[currentState].getCurrentFrame(), screenX + width, screenY, -width, height, null);
        } else {
            g.drawImage(animations[currentState].getCurrentFrame(), screenX, screenY, width, height, null);
        }
    }

    @Override
    public void die() {}

    @Override
    public void onInteraction(PaooGame.World.Entities.Entity e) {}

    @Override
    public void hurt(float dmg) {
        this.hp -= dmg; // Încearcă cu "hp" în loc de "health"

        if(this.hp <= 0) {
            die();
        }
    }
}