package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Entities.Projectiles.SoulSpear;
import PaooGame.World.Entities.Spells.SoulPillar;

import java.awt.*;
import java.util.Random;

import static PaooGame.World.Tile.TILE_SIZE;

public class Boss extends GroundEnemy {
    // --- Dimensiuni ---
    public static final int LOGIC_WIDTH = 7 * TILE_SIZE;
    public static final int LOGIC_HEIGHT = 7 * TILE_SIZE;
    public static final Rectangle BOUNDS = new Rectangle(-5, -5, LOGIC_WIDTH + 10, LOGIC_HEIGHT + 10);

    public static final int VISUAL_WIDTH = 20 * TILE_SIZE;
    public static final int VISUAL_HEIGHT = 16 * TILE_SIZE;
    public static final int VISUAL_OFFSET_Y = 24;

    public enum Attacks { MELEE, PROJECTILE, SPELL, SUMMON }

    public enum ActionState {
        IDLE(0, 0, 0, 0),
        MELEE(50, 20, 30, 150),
        PROJECTILE(40, 10, 30, 100),
        SPELL(30, 10, 20, 40),
        SUMMON(60, 20, 40, 300),
        LIFESTEAL(0, 120, 0, 0);

        public final int windup, active, recovery, baseCooldown;

        ActionState(int windup, int active, int recovery, int baseCooldown) {
            this.windup = windup; this.active = active; this.recovery = recovery; this.baseCooldown = baseCooldown;
        }
        public int getDuration() { return windup + active + recovery; }
    }

    public enum AnimationState {
        IDLE, WALK, MELEE, PROJECTILE, SPELL, SUMMON, HURT, LIFESTEAL,
        DEATH1, MOVE_TO_PHASE2, PHASE2_START, DEATH2
    }

    public enum TransitionState {
        NONE, DEATH1, MOVING, STARTING_PHASE_2
    }

    // --- Variabile State Machine ---
    private ActionState currAction = ActionState.IDLE;
    private AnimationState currAnim = AnimationState.IDLE;
    private TransitionState transitionState = TransitionState.NONE;
    private final Animation[] animations = new Animation[AnimationState.values().length];

    private int actionTimer = 0;
    private int attackCooldown = 0;
    private int deathTimer = 0;
    private boolean isDead = false;

    // --- Logica originală a Boss-ului ---
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

    public static final float BASE_SPEED = 2f;
    public static final float AGGRO_SPEED = 3f;
    public static final float MELEE_RANGE = TILE_SIZE * 9;
    public static final float ATTACK_RANGE = TILE_SIZE * 30;
    public static final float AGGRO_RANGE = TILE_SIZE * 36;
    public static final float X_RANGE = TILE_SIZE * 30;

    public static final float MAX_HP = 1000f;
    public static final float KNOCK_PERCENT = 0.05f;

    private Rectangle attackBounds = new Rectangle();
    private static final float ATTACK_DMG = 100f;
    private static final float COLLISION_DMG = 50f;

    private static final int LIFESTEAL_COOLDOWN = 240;
    private int lifestealTimer;
    private float phase2TargetX;

    // Variabile folosite pentru umplerea progresiva a vietii in Phase 2
    private float hpRegenPerTick = 0f;

    public Boss(Handler handler, float x, float y) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT, MAX_HP, KNOCK_PERCENT);
        this.bounds = new Rectangle(BOUNDS);

        this.baseSpeed = BASE_SPEED;
        this.aggroSpeed = AGGRO_SPEED;
        this.attackRange = ATTACK_RANGE;
        this.aggroRange = AGGRO_RANGE;
        this.patrolRange = new float[]{x - X_RANGE, x + X_RANGE};
        this.rand = new Random();
        this.BASE_CREATURE_COUNT = handler.getLevel().getEntityManager().getCreatureCount();

        this.phase2TargetX = (patrolRange[0] + patrolRange[1]) / 2f;

        setAnimations();
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
        updateCooldowns();

        if (knockbackTimer > 0 || pauseTimer > 0) {
            currAction = ActionState.IDLE;
            actionTimer = 0;
        } else {
            updateActionState();
        }

        updateAnimationLogic();
    }

    private void handlePhaseTransition() {
        switch (transitionState) {
            case DEATH1:
                changeAnimation(AnimationState.DEATH1);
                animations[currAnim.ordinal()].Update();
                if (animations[currAnim.ordinal()].isFinished()) {
                    transitionState = TransitionState.MOVING;
                }
                break;

            case MOVING:
                changeAnimation(AnimationState.MOVE_TO_PHASE2);

                if (Math.abs(this.x - phase2TargetX) > baseSpeed) {
                    this.direction = (this.x < phase2TargetX) ? 1 : -1;
                    this.x += direction * baseSpeed * 1.5f;
                    animations[currAnim.ordinal()].Update();
                } else {
                    this.x = phase2TargetX;
                    transitionState = TransitionState.STARTING_PHASE_2;

                    // Calculăm cât HP trebuie să regenerăm per tick
                    // Știm din setAnimations că PHASE2_START are viteza 10
                    // Și are 14 cadre (conform bossFrames trimis anterior)
                    // Timp total: 10 * 14 = 140 ticks
                    hpRegenPerTick = MAX_HP / 320f;
                }
                break;

            case STARTING_PHASE_2:
                changeAnimation(AnimationState.PHASE2_START);
                animations[currAnim.ordinal()].Update();

                // Creștem treptat viața
                if (this.hp < MAX_HP) {
                    this.hp += hpRegenPerTick;
                    if (this.hp > MAX_HP) {
                        this.hp = MAX_HP;
                    }
                }

                if (animations[currAnim.ordinal()].isFinished()) {
                    // Terminăm tranziția, începe efectiv Faza 2
                    transitionState = TransitionState.NONE;
                    isPhaseTwo = true;
                    awaitingSummon = true;

                    this.bounds.width = BOUNDS.width;
                    this.bounds.height = BOUNDS.height;

                    // Garanție finală că HP-ul este perfect plin
                    this.hp = MAX_HP;
                }
                break;
        }
    }

    private void updateCooldowns() {
        if (attackCooldown > 0) attackCooldown--;
        if (lifestealTimer > 0) lifestealTimer--;
    }

    private void updateActionState() {
        if (currAction == ActionState.IDLE) return;

        actionTimer--;

        if (actionTimer == (currAction.active + currAction.recovery)) {
            executeAttackHitbox();
        }

        if (actionTimer <= 0) {
            currAction = ActionState.IDLE;
        }
    }

    @Override
    protected void attack() {
        if (attackCooldown > 0 || currAction != ActionState.IDLE) return;

        Attacks nextAtk = selectAttack();

        switch (nextAtk) {
            case MELEE -> currAction = ActionState.MELEE;
            case PROJECTILE -> currAction = ActionState.PROJECTILE;
            case SPELL -> currAction = ActionState.SPELL;
            case SUMMON -> currAction = ActionState.SUMMON;
        }

        actionTimer = currAction.getDuration();
        attackCooldown = currAction.baseCooldown;
    }

    private void executeAttackHitbox() {
        switch (currAction) {
            case MELEE -> melee();
            case PROJECTILE -> projectile();
            case SPELL -> spell();
            case SUMMON -> summon();
            default -> {}
        }
    }

    private void melee() {
        int hWidth = TILE_SIZE * 6;
        int hHeight = (int) (bounds.height * 0.6f);
        attackBounds.setSize(hWidth, hHeight);

        attackBounds.x = (direction == 1) ? (int) (x + bounds.x + bounds.width) : (int) (x + bounds.x - hWidth);
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
                new SoulSpear(handler, spawnCoords[0], spawnCoords[1], targetCoords[0], targetCoords[1], this)
        );
    }

    private void spell() {
        float[] danteCenter = handler.getDante().getCenter();
        float spellX = danteCenter[0] - SoulPillar.LOGIC_WIDTH / 2f;
        float spellY = danteCenter[1] - SoulPillar.LOGIC_HEIGHT / 2f;
        handler.getLevel().getEntityManager().addEntity(new SoulPillar(handler, spellX, spellY, this));
    }

    private void summon() {
        handler.addEntity(new Necromancer(handler, SUMMON_COORDS[0][0], SUMMON_COORDS[0][1]));
        handler.addEntity(new Necromancer(handler, SUMMON_COORDS[1][0], SUMMON_COORDS[1][1]));
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
                return (randomNumber < PROJECTILE_SPELL_PERC[0]) ? Attacks.PROJECTILE : generateSpell();
            } else {
                if (randomNumber < PROJECTILE_SPELL_MELEE_PERC[0]) return Attacks.PROJECTILE;
                if (randomNumber < PROJECTILE_SPELL_MELEE_PERC[1]) return generateSpell();
                return Attacks.MELEE;
            }
        } else {
            if (distance > MELEE_RANGE) {
                if (randomNumber < PROJECTILE_SPELL_SUMMON_PERC[0]) return Attacks.PROJECTILE;
                if (randomNumber < PROJECTILE_SPELL_SUMMON_PERC[1]) return generateSpell();
                return Attacks.SUMMON;
            } else {
                if (randomNumber < PROJECTILE_SPELL_SUMMON_MELEE_PERC[0]) return Attacks.PROJECTILE;
                if (randomNumber < PROJECTILE_SPELL_SUMMON_MELEE_PERC[1]) return generateSpell();
                if (randomNumber < PROJECTILE_SPELL_SUMMON_MELEE_PERC[2]) return Attacks.SUMMON;
                return Attacks.MELEE;
            }
        }
    }

    public Attacks generateSpell() {
        currSpellCount = SPELL_COUNT - 1;
        return Attacks.SPELL;
    }

    private void updateAnimationLogic() {
        AnimationState nextAnim = AnimationState.IDLE;

        if (knockbackTimer > 0) {
            nextAnim = AnimationState.HURT;
        } else if (currAction != ActionState.IDLE) {
            switch(currAction) {
                case MELEE -> nextAnim = AnimationState.MELEE;
                case PROJECTILE -> nextAnim = AnimationState.PROJECTILE;
                case SPELL -> nextAnim = AnimationState.SPELL;
                case SUMMON -> nextAnim = AnimationState.SUMMON;
                case LIFESTEAL -> nextAnim = AnimationState.LIFESTEAL;
            }
        } else if (Math.abs(xMove) > 0.1f) {
            nextAnim = AnimationState.WALK;
        }

        changeAnimation(nextAnim);
        animations[currAnim.ordinal()].Update();
    }

    private void changeAnimation(AnimationState newAnim) {
        if (currAnim != newAnim) {
            currAnim = newAnim;
            if (animations[currAnim.ordinal()] != null) {
                animations[currAnim.ordinal()].reset();
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (LOGIC_WIDTH - VISUAL_WIDTH) / 2;
        int drawY = screenY + (LOGIC_HEIGHT - VISUAL_HEIGHT) + VISUAL_OFFSET_Y;

        if (animations[currAnim.ordinal()] != null && animations[currAnim.ordinal()].getCurrentFrame() != null) {
            if (direction == 1) {
                g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                        drawX + VISUAL_WIDTH, drawY, -VISUAL_WIDTH, VISUAL_HEIGHT, null);
            } else {
                g.drawImage(animations[currAnim.ordinal()].getCurrentFrame(),
                        drawX, drawY, VISUAL_WIDTH, VISUAL_HEIGHT, null);
            }
        }

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);

            if (currAction == ActionState.MELEE) {
                boolean isActive = actionTimer < (currAction.active + currAction.recovery) && actionTimer >= currAction.recovery;
                if (isActive) {
                    g.setColor(new Color(255, 255, 255, 150));
                    g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                            (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                            attackBounds.width, attackBounds.height);
                }
            }
        }
    }

    @Override
    public boolean hurt(float dmg) {
        if (isDead || transitionState != TransitionState.NONE) return false;
        return super.hurt(dmg);
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

    @Override
    public void die() {
        if (transitionState != TransitionState.NONE) return;

        if (isPhaseTwo) {
            if (isDead) return;
            isDead = true;
            currAnim = AnimationState.DEATH2;
            currAction = ActionState.IDLE;
            if(animations[AnimationState.DEATH2.ordinal()] != null) {
                animations[AnimationState.DEATH2.ordinal()].reset();
            }
            deathTimer = 180;
            xMove = 0; bounds.width = 0; bounds.height = 0;
        } else {
            transitionState = TransitionState.DEATH1;
            currAction = ActionState.IDLE;
            xMove = 0;

            bounds.width = 0;
            bounds.height = 0;
        }
    }

    private void handleDeath2() {
        if (deathTimer > 0) {
            deathTimer--;
            animations[AnimationState.DEATH2.ordinal()].Update();
        } else {
            active = false;
        }
    }

    public void heal(float hp) {
        if (isDead || transitionState != TransitionState.NONE) return;

        if (lifestealTimer <= 0) {
            this.hp += hp;
            if (this.hp > MAX_HP) this.hp = MAX_HP;

            currAction = ActionState.LIFESTEAL;
            actionTimer = ActionState.LIFESTEAL.getDuration();
            lifestealTimer = LIFESTEAL_COOLDOWN;
        }
    }

    @Override
    public void setAnimations() {
        try {
            animations[AnimationState.IDLE.ordinal()] = new Animation(10, VisualAssets.Boss.get("Idle"));
            animations[AnimationState.WALK.ordinal()] = new Animation(10, VisualAssets.Boss.get("Walk"));
            animations[AnimationState.HURT.ordinal()] = new Animation(6, VisualAssets.Boss.get("Hurt"));
            animations[AnimationState.LIFESTEAL.ordinal()] = new Animation(ActionState.LIFESTEAL.active / 10, VisualAssets.Boss.get("Lifesteal"));

            animations[AnimationState.DEATH1.ordinal()] = new Animation(10, VisualAssets.Boss.get("Death1"));
            animations[AnimationState.MOVE_TO_PHASE2.ordinal()] = new Animation(8, VisualAssets.Boss.get("MoveToPhase2"));

            animations[AnimationState.PHASE2_START.ordinal()] = new Animation(10, VisualAssets.Boss.get("Phase2Start"));

            animations[AnimationState.DEATH2.ordinal()] = new Animation(10, VisualAssets.Boss.get("Death2"));
            animations[AnimationState.DEATH2.ordinal()].setFrameDuration(VisualAssets.Boss.get("Death2").length - 1, -1);

            animations[AnimationState.MELEE.ordinal()] = new Animation(ActionState.MELEE.windup / 9, VisualAssets.Boss.get("Melee"));
            animations[AnimationState.MELEE.ordinal()].setFrameDuration(9, ActionState.MELEE.active);
            for (int i = 0; i < 5; ++i) {
                animations[AnimationState.MELEE.ordinal()].setFrameDuration(10 + i, ActionState.MELEE.recovery / 5);
            }

            animations[AnimationState.PROJECTILE.ordinal()] = new Animation(ActionState.PROJECTILE.windup / 10, VisualAssets.Boss.get("Projectile"));
            animations[AnimationState.PROJECTILE.ordinal()].setFrameDuration(10, ActionState.PROJECTILE.active);
            for (int i = 0; i < 9; ++i) {
                animations[AnimationState.PROJECTILE.ordinal()].setFrameDuration(11 + i, ActionState.PROJECTILE.recovery / 9);
            }

            animations[AnimationState.SPELL.ordinal()] = new Animation(ActionState.SPELL.windup / 12, VisualAssets.Boss.get("Spell"));
            animations[AnimationState.SPELL.ordinal()].setFrameDuration(12, ActionState.SPELL.active);
            for (int i = 0; i < 3; ++i ) {
                animations[AnimationState.SPELL.ordinal()].setFrameDuration(13 + i, ActionState.SPELL.recovery);
            }

            animations[AnimationState.SUMMON.ordinal()] = new Animation(ActionState.SUMMON.windup / 4, VisualAssets.Boss.get("Summon"));
            animations[AnimationState.SUMMON.ordinal()].setFrameDuration(4, ActionState.SUMMON.active);
            animations[AnimationState.SUMMON.ordinal()].setFrameDuration(5, ActionState.SUMMON.recovery);

        } catch (Exception e) {
            System.out.println("Eroare la setare animatii Boss: " + e.getMessage());
        }
    }
}