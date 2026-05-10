package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.SoulSpear;
import PaooGame.World.Entities.Spells.SoulPillar;
import PaooGame.World.Entities.Creatures.Enemies.EnemyAction;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care reprezinta inamicul Beelzebub, dispune de SuperArmour si Lifesteal
 */
public class Boss extends GroundEnemy {
    public static final int LOGIC_WIDTH = 6 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 7 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(-5, -5, LOGIC_WIDTH + 10, LOGIC_HEIGHT + 10);
    public static final int VISUAL_WIDTH = 20 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 16 * TILE_SIZE;
    public static final int VISUAL_OFFSET_Y = 24;

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 3f;
    public static final float MELEE_RANGE = TILE_SIZE * 10;
    public static final float ATTACK_RANGE = TILE_SIZE * 20;
    public static final float AGGRO_RANGE = TILE_SIZE * 26;
    public static final float X_RANGE = TILE_SIZE * 30;

    public static final float MAX_HP = 1000f;
    public static final float KNOCK_PERCENT = 0.05f;

    private final EnemyAction meleeAct = new EnemyAction("MELEE", 50, 20, 30, 150, MELEE_RANGE, false);
    private final EnemyAction projAct = new EnemyAction("PROJECTILE", 80, 10, 30, 100, ATTACK_RANGE, true);
    private final EnemyAction spellAct = new EnemyAction("SPELL", 50, 10, 20, 40, ATTACK_RANGE, true);
    private final EnemyAction summonAct = new EnemyAction("SUMMON", 60, 20, 40, 300, ATTACK_RANGE, true);
    private final EnemyAction lifestealAct = new EnemyAction("LIFESTEAL", 0, 120, 0, 240, 0, false);

    public enum TransitionState {
        NONE, DEATH1, MOVING, STARTING_PHASE_2
    }
    private TransitionState transitionState = TransitionState.NONE;

    private static final int[] PROJECTILE_SPELL_PERC = {60, 100};
    private static final int[] PROJECTILE_SPELL_MELEE_PERC = {35, 50, 100};
    private static final int[] PROJECTILE_SPELL_SUMMON_PERC = {60, 90, 100};
    private static final int[] PROJECTILE_SPELL_SUMMON_MELEE_PERC = {25, 50, 60, 100};

    private static final int SPELL_COUNT = 5;
    private int currSpellCount;
    private final int BASE_CREATURE_COUNT;

    private static final float[][] SUMMON_COORDS = {{6020, 150}, {4532, 150}};
    public boolean awaitingSummon;
    public boolean isPhaseTwo;
    public Random rand;

    private static final float ATTACK_DMG = 100f;
    private static final float COLLISION_DMG = 50f;

    private final float phase2TargetX;
    private float hpRegenPerTick = 0f;

    private static final int HITBOX_WIDTH = TILE_SIZE * 6;
    private static final int HITBOX_HEIGHT = TILE_SIZE * 6;
    private final Rectangle attackBounds = new Rectangle(
            0, 0, HITBOX_WIDTH, HITBOX_HEIGHT
    );

    public Boss(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};

        this.rand = new Random();
        this.BASE_CREATURE_COUNT = handler.getLevel().getEntityManager().getEnemyCount();
        this.phase2TargetX = (patrolRange[0] + patrolRange[1]) / 2f;

        setAnimations();
        hasSuperArmor = true;
    }

    @Override
    public void Update() {
        if (isDead) {
            handleDeath2();
            return;
        }

        if (transitionState != TransitionState.NONE) {
            handlePhaseTransition();
            return;
        }

        super.Update();
    }

    @Override
    protected void makeDecision() {
        if (currentAction != null) {
            super.makeDecision();
            return;
        }

        EnemyAction nextAction = determineNextAttack();

        if (nextAction != null) {
            direction = isPlayerToTheRight() ? 1 : -1;

            startAction(nextAction);
            applyVerticalMovement();
            if (currentAction.stopsMovement()) {
                xMove = 0;
            }
            return;
        }

        super.makeDecision();
    }

    private EnemyAction determineNextAttack() {
        if (awaitingSummon && isReady(summonAct)) { awaitingSummon = false; return summonAct; }
        if (currSpellCount > 0 && isReady(spellAct)) { --currSpellCount; return spellAct; }

        float distance = getDistanceToPlayer();
        int creatureCount = handler.getCreatureCount();
        int r = rand.nextInt(100);

        if (distance > ATTACK_RANGE) {
            return null;
        }

        if (!isPhaseTwo || creatureCount != BASE_CREATURE_COUNT) {
            if (distance > MELEE_RANGE) {
                if (r < PROJECTILE_SPELL_PERC[0] && isReady(projAct)) return projAct;
                if (isReady(spellAct)) return generateSpell();
            } else {
                if (r < PROJECTILE_SPELL_MELEE_PERC[0] && isReady(projAct)) return projAct;
                if (r < PROJECTILE_SPELL_MELEE_PERC[1] && isReady(spellAct)) return generateSpell();
                if (isReady(meleeAct)) return meleeAct;
            }
        } else {
            if (distance > MELEE_RANGE) {
                if (r < PROJECTILE_SPELL_SUMMON_PERC[0] && isReady(projAct)) return projAct;
                if (r < PROJECTILE_SPELL_SUMMON_PERC[1] && isReady(spellAct)) return generateSpell();
                if (isReady(summonAct)) return summonAct;
            } else {
                if (r < PROJECTILE_SPELL_SUMMON_MELEE_PERC[0] && isReady(projAct)) return projAct;
                if (r < PROJECTILE_SPELL_SUMMON_MELEE_PERC[1] && isReady(spellAct)) return generateSpell();
                if (r < PROJECTILE_SPELL_SUMMON_MELEE_PERC[2] && isReady(summonAct)) return summonAct;
                if (isReady(meleeAct)) return meleeAct;
            }
        }
        return null;
    }

    private boolean isReady(EnemyAction action) { return actionCooldowns.getOrDefault(action.actionId(), 0) <= 0; }

    private EnemyAction generateSpell() {
        currSpellCount = SPELL_COUNT - 1;
        return spellAct;
    }

    @Override
    protected void executeActionHitbox(String actionId) {
        switch (actionId) {
            case "MELEE" -> executeMelee();
            case "PROJECTILE" -> executeProjectile();
            case "SPELL" -> executeSpell();
            case "SUMMON" -> executeSummon();
            default -> {}
        }
    }

    private void executeMelee() { executeMeleeAttack(attackBounds, HITBOX_WIDTH, HITBOX_HEIGHT, ATTACK_DMG, 120f, -20f, 15); }

    private void executeProjectile() {
        float[] spawnCoords = getCenter(), targetCoords = handler.getDante().getCenter();
        handler.getLevel().getEntityManager().addEntity(new SoulSpear(handler, spawnCoords[0], spawnCoords[1], targetCoords[0], targetCoords[1], this));
    }

    private void executeSpell() {
        float[] danteCenter = handler.getDante().getCenter();
        float spellX = danteCenter[0] - SoulPillar.LOGIC_WIDTH / 2f;
        float spellY = danteCenter[1] - SoulPillar.LOGIC_HEIGHT / 2f;
        handler.getLevel().getEntityManager().addEntity(new SoulPillar(handler, spellX, spellY, this));
    }

    private void executeSummon() {
        handler.addEntity(new Necromancer(handler, SUMMON_COORDS[0][0], SUMMON_COORDS[0][1]));
        handler.addEntity(new Necromancer(handler, SUMMON_COORDS[1][0], SUMMON_COORDS[1][1]));
    }

    private void handlePhaseTransition() {
        switch (transitionState) {
            case DEATH1 -> {
                currentAnimId = "DEATH1";
                animations.get(currentAnimId).Update();
                if (animations.get(currentAnimId).isFinished()) transitionState = TransitionState.MOVING;
            }
            case MOVING -> {
                currentAnimId = "MOVE_TO_PHASE2";
                if (Math.abs(this.x - phase2TargetX) > baseSpeed) {
                    this.direction = (this.x < phase2TargetX) ? 1 : -1;
                    this.x += direction * baseSpeed * 1.5f;
                    animations.get(currentAnimId).Update();
                } else {
                    this.x = phase2TargetX;
                    transitionState = TransitionState.STARTING_PHASE_2;
                    hpRegenPerTick = MAX_HP / 100f;
                    animations.get("PHASE2_START").reset();
                }
            }
            case STARTING_PHASE_2 -> {
                currentAnimId = "PHASE2_START";
                animations.get(currentAnimId).Update();

                if (this.hp < MAX_HP) {
                    this.hp += hpRegenPerTick;
                    if (this.hp > MAX_HP) this.hp = MAX_HP;
                }

                if (animations.get(currentAnimId).isFinished()) {
                    transitionState = TransitionState.NONE;
                    isPhaseTwo = true;
                    awaitingSummon = true;
                    this.bounds.width = BOUNDS.width;
                    this.bounds.height = BOUNDS.height;
                    this.hp = MAX_HP;
                    currentAnimId = "IDLE";
                }
            }
            default -> {}
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT) + VISUAL_OFFSET_Y;

        Animation currentAnim = animations.get(currentAnimId);

        if (currentAnim != null && currentAnim.getCurrentFrame() != null) {
            if (direction == 1) {
                g.drawImage(currentAnim.getCurrentFrame(), drawX + VISUAL_WIDTH, drawY, -VISUAL_WIDTH, VISUAL_HEIGHT, null);
            } else {
                g.drawImage(currentAnim.getCurrentFrame(), drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);
            }
        }

        if (handler.getDebugMode() && !isDead) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);

            if ("MELEE".equals(currentAnimId) && currentAction != null && hasExecutedActiveFrame) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                        (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                        attackBounds.width, attackBounds.height);
            }
        }
    }
    @Override
    public void onInteraction(Entity e) {
        if (isDead || transitionState != TransitionState.NONE) return;

        if (e instanceof Dante dante) {
            if (dante.hurt(COLLISION_DMG)) {
                float knockX = (this.x > dante.getX()) ? -12.0f : 12.0f;
                dante.applyKnockback(knockX, -20.0f);
                pauseTimer = PAUSE_DURATION;
            }
        }
    }

    public void heal(float hp) {
        if (isDead || transitionState != TransitionState.NONE) {
            return;
        }

        if (actionCooldowns.getOrDefault("LIFESTEAL", 0) <= 0) {
            this.hp += hp;
            if (this.hp > MAX_HP) {
                this.hp = MAX_HP;
            }
            startAction(lifestealAct);
        }
    }

    @Override
    public void die() {
        if (transitionState != TransitionState.NONE) {
            return;
        }

        if (isPhaseTwo) {
            if (isDead) {
                return;
            }
            isDead = true;
            currentAnimId = "DEATH2";
            currentAction = null;
            animations.get("DEATH2").reset();
            deathTimer = 180;
        } else {
            transitionState = TransitionState.DEATH1;
            currentAction = null;
        }
        xMove = 0;
        bounds.width = 0;
        bounds.height = 0;
    }

    private void handleDeath2() {
        if (deathTimer > 0) {
            deathTimer--;
            animations.get("DEATH2").Update();
        } else {
            active = false;
        }
    }

    @Override
    public boolean hurt(float dmg) {
        if (isDead || transitionState != TransitionState.NONE) {
            return false;
        }

        if (isWindingUp()) {
            if (invulnerabilityTimer > 0) {
                return false;
            }

            hp -= dmg;
            invulnerabilityTimer = INVULN_TIME;

            if (hp <= 0) {
                die();
            }
            return true;
        }

        return super.hurt(dmg);
    }

    @Override
    public void setAnimations() {
        try {
            animations.put("IDLE", new Animation(10, VisualAssets.Boss.get("Idle")));
            animations.put("WALK", new Animation(10, VisualAssets.Boss.get("Walk")));
            animations.put("HURT", new Animation(6, VisualAssets.Boss.get("Hurt")));
            animations.put("LIFESTEAL", new Animation(lifestealAct.active() / 10, VisualAssets.Boss.get("Lifesteal")));

            animations.put("DEATH1", new Animation(10, VisualAssets.Boss.get("Death1")));
            animations.put("MOVE_TO_PHASE2", new Animation(8, VisualAssets.Boss.get("MoveToPhase2")));
            animations.put("PHASE2_START", new Animation(10, VisualAssets.Boss.get("Phase2Start")));

            Animation death2Anim = new Animation(10, VisualAssets.Boss.get("Death2"));
            death2Anim.setFrameDuration(VisualAssets.Boss.get("Death2").length - 1, -1);
            animations.put("DEATH2", death2Anim);

            Animation meleeAnim = new Animation(meleeAct.windup() / 9, VisualAssets.Boss.get("Melee"));
            meleeAnim.setFrameDuration(9, meleeAct.active());
            for (int i = 0; i < 5; ++i) meleeAnim.setFrameDuration(10 + i, meleeAct.recovery() / 5);
            animations.put("MELEE", meleeAnim);

            Animation projAnim = new Animation(projAct.windup() / 10, VisualAssets.Boss.get("Projectile"));
            projAnim.setFrameDuration(10, projAct.active());
            for (int i = 0; i < 9; ++i) projAnim.setFrameDuration(11 + i, projAct.recovery() / 9);
            animations.put("PROJECTILE", projAnim);

            Animation spellAnim = new Animation(spellAct.windup() / 12, VisualAssets.Boss.get("Spell"));
            spellAnim.setFrameDuration(12, spellAct.active());
            for (int i = 0; i < 3; ++i) spellAnim.setFrameDuration(13 + i, spellAct.recovery());
            animations.put("SPELL", spellAnim);

            Animation summonAnim = new Animation(summonAct.windup() / 4, VisualAssets.Boss.get("Summon"));
            summonAnim.setFrameDuration(4, summonAct.active());
            summonAnim.setFrameDuration(5, summonAct.recovery());
            animations.put("SUMMON", summonAnim);

        } catch (Exception e) {
            System.err.println("[EROARE]: La setarea animatiilor Boss: " + e.getMessage());
        }
    }
}