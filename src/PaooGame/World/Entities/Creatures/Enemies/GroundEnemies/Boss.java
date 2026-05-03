package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.SoulSpear;
import PaooGame.World.Entities.Spells.SoulPillar;

import java.awt.*;
import java.util.Random;

import static PaooGame.World.Tile.TILE_SIZE;

public class Boss extends GroundEnemy {
    public static final int DEFAULT_WIDTH = 7 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 7 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(
            -5, -5, DEFAULT_WIDTH + 10, DEFAULT_HEIGHT + 10
    );

    public enum Attacks {
        MELEE(150),
        PROJECTILE(100),
        SPELL(40),
        SUMMON(300);

        private final int cd;
        Attacks(int cd) {
            this.cd = cd;
        }
    }
    private Attacks currAttack;

    private static final int[] PROJECTILE_SPELL_PERC = {60, 100};
    private static final int[] PROJECTILE_SPELL_MELEE_PERC = {40, 60, 100};
    private static final int[] PROJECTILE_SPELL_SUMMON_PERC = {60, 90, 100};
    private static final int[] PROJECTILE_SPELL_SUMMON_MELEE_PERC = {30, 60, 70, 100};

    private static final int SPELL_COUNT = 5;
    private int currSpellCount;
    private final int BASE_CREATURE_COUNT;

    private static final float[][] SUMMON_COORDS = {
            {6020, 150}, {4532, 150}
    };
    public boolean awaitingSummon;

    public boolean isPhaseTwo;
    public Random rand;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 3f;
    public static final float MELEE_RANGE = TILE_SIZE * 7;
    public static final float ATTACK_RANGE = TILE_SIZE * 30;
    public static final float AGGRO_RANGE = TILE_SIZE * 36;
    public static final float X_RANGE = TILE_SIZE * 30;

    public static final float MAX_HP = 1000f;
    public static final float KNOCK_PERCENT = 0.05f;

    private static final int ATTACK_DURATION = 30;
    private int attackTimer = 0;
    private int attackActiveTimer = 0;

    private Rectangle attackBounds = new Rectangle();
    private static final float ATTACK_DMG = 100f;

    private static final int LIFESTEAL_COOLDOWN = 240;
    private int lifestealTimer;

    private static final float COLLISION_DMG = 50f;

    public Boss(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.attackRange = ATTACK_RANGE;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.rand = new Random();
        this.BASE_CREATURE_COUNT = handler.getLevel().getEntityManager().getCreatureCount();
    }

    @Override
    public void Update() {
        super.Update();
        if (knockbackTimer > 0 || pauseTimer > 0) {
            return;
        }

        if (attackTimer > 0) {
            attackTimer--;

            if (attackTimer == ATTACK_DURATION) {
                isWindingUp = false;
                isAttacking = true;
                attackActiveTimer = ATTACK_DURATION;

                executeAttackHitbox();
            }
        }

        if (isAttacking) {
            attackActiveTimer--;
            if (attackActiveTimer <= 0) {
                isAttacking = false;
                pauseTimer = PAUSE_DURATION;
            }
        }
    }

    public Attacks selectAttack() {
        if (awaitingSummon) {
            awaitingSummon = false;
            return Attacks.SUMMON;
        }
        if (currSpellCount > 0) {
            --currSpellCount;
            return Attacks.SPELL;
        }

        float distance = getDistanceToPlayer();
        int creatureCount = handler.getCreatureCount();
        int randomNumber = rand.nextInt(100);

        if (!isPhaseTwo || creatureCount != BASE_CREATURE_COUNT) {
            if (distance > MELEE_RANGE) {
                if (randomNumber < PROJECTILE_SPELL_PERC[0]) {
                    return Attacks.PROJECTILE;
                }
                return generateSpell();
            }
            else {
                if (randomNumber < PROJECTILE_SPELL_MELEE_PERC[0]) {
                    return Attacks.PROJECTILE;
                }
                else if (randomNumber < PROJECTILE_SPELL_MELEE_PERC[1]) {
                    return generateSpell();
                }
                return Attacks.MELEE;
            }
        }
        else {
            if (distance > MELEE_RANGE) {
                if (randomNumber < PROJECTILE_SPELL_SUMMON_PERC[0]) {
                    return Attacks.PROJECTILE;
                }
                else if (randomNumber < PROJECTILE_SPELL_SUMMON_PERC[1]) {
                    return generateSpell();
                }
                return Attacks.SUMMON;
            }
            else {
                if (randomNumber < PROJECTILE_SPELL_SUMMON_MELEE_PERC[0]) {
                    return Attacks.PROJECTILE;
                }
                else if (randomNumber < PROJECTILE_SPELL_SUMMON_MELEE_PERC[1]) {
                    return generateSpell();
                }
                else if (randomNumber < PROJECTILE_SPELL_SUMMON_MELEE_PERC[2]) {
                    return Attacks.SUMMON;
                }
                return Attacks.MELEE;
            }
        }
    }

    public Attacks generateSpell() {
        currSpellCount = SPELL_COUNT - 1;
        return Attacks.SPELL;
    }

    @Override
    protected void attack() {
        if (attackTimer > 0) {
            return;
        }

        currAttack = selectAttack();
        attackTimer = currAttack.cd;
        isWindingUp = true;
    }

    private void executeAttackHitbox() {
        switch (currAttack) {
            case MELEE:
                melee();
                break;
            case PROJECTILE:
                projectile();
                break;
            case SPELL:
                spell();
                break;
            case SUMMON:
                summon();
                break;
        }
    }

    private void melee() {
        int hWidth = TILE_SIZE * 6;
        int hHeight = (int) (bounds.height * 0.6f);

        attackBounds.setSize(hWidth, hHeight);

        if (direction == 1) {
            attackBounds.x = (int) (x + bounds.x + bounds.width);
        }
        else {
            attackBounds.x = (int) (x + bounds.x - hWidth);
        }
        attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);

        Dante dante = handler.getDante();
        if (attackBounds.intersects(dante.getCollisionBounds(0f, 0f))) {
            if (dante.hurt(ATTACK_DMG)) {
                float knockX = (direction == 1) ? 120.0f : -120.0f;
                dante.applyKnockback(knockX, -20.0f);

                pauseTimer = PAUSE_DURATION;
            }
        }
    }
    private void projectile() {
        float[] spawnCoords = getCenter(), targetCoords = handler.getDante().getCenter();

        handler.getLevel().getEntityManager().addEntity(
                new SoulSpear(handler, spawnCoords[0], spawnCoords[1],
                        targetCoords[0], targetCoords[1], this)
        );
    }
    private void spell() {
        float[] danteCenter = handler.getDante().getCenter();

        float spellX = danteCenter[0] - SoulPillar.LOGIC_WIDTH / 2f;
        float spellY = danteCenter[1] - SoulPillar.LOGIC_WIDTH / 2f;

        handler.getLevel().getEntityManager().addEntity(
                new SoulPillar(handler, spellX, spellY, this)
        );
    }
    private void summon() {
        handler.addEntity(
                new Necromancer(handler, SUMMON_COORDS[0][0], SUMMON_COORDS[0][1])
        );
        handler.addEntity(
                new Necromancer(handler, SUMMON_COORDS[1][0], SUMMON_COORDS[1][1])
        );
    }

    @Override
    public void Draw(Graphics g) {
        if (isWindingUp) {
            g.setColor(Color.MAGENTA);
        }
        else {
            g.setColor(Color.ORANGE);
        }

        g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                bounds.width, bounds.height);

        if (isAttacking && currAttack == Attacks.MELEE) {
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
        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;

                dante.applyKnockback(knockX, -20.0f);
                pauseTimer = PAUSE_DURATION;
            }
        }
    }

    @Override
    public void die() {
        if(isPhaseTwo) {
            active = false;
            return;
        }

        hp = MAX_HP;
        awaitingSummon = true;
        isPhaseTwo = true;
    }

    public void heal(float hp) {
        if (lifestealTimer == 0) {
            this.hp += hp;
            lifestealTimer = LIFESTEAL_COOLDOWN;
        }
    }

    @Override
    public void setAnimations() {}
}