package PaooGame.World.Entities.Spells;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Necromancer;
import PaooGame.World.Entities.Entity;

import java.awt.*;

public abstract class Spell extends Entity {
    protected final static float KNOCKBACK_FORCE = 10f;

    protected Creature owner;
    protected float damage;
    protected float knockPercent;

    protected int summonTimer;
    protected int activeTimer;

    protected boolean isSummoning = true;
    protected boolean isActivePhase = false;

    public Spell(Handler handler, float x, float y, int width, int height,
                 Creature owner, float damage, float knockPercent, int summonTime, int activeTime) {
        super(handler, x, y, width, height);

        this.owner = owner;
        this.damage = damage;
        this.knockPercent = knockPercent;

        this.summonTimer = summonTime;
        this.activeTimer = activeTime;
    }

    @Override
    public void Update() {
        if (summonTimer > 0) {
            summonTimer--;

            if (summonTimer <= 0) {
                isSummoning = false;
                isActivePhase = true;
            }
            return;
        }

        if (isActivePhase && activeTimer > 0) {
            activeTimer--;

            triggerSpellInteractions();

            if (activeTimer <= 0) {
                isActivePhase = false;
                this.active = false;
            }
        }
    }

    public void triggerSpellInteractions() {
        Rectangle spellBounds = getCollisionBounds(0f, 0f);

        for (Entity e : handler.getEntities()) {
            if (e.equals(this) || e instanceof Necromancer) {
                continue;
            }

            if (spellBounds.intersects(e.getCollisionBounds(0f, 0f))) {
                this.onInteraction(e);
            }
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if (isSummoning) {
            return;
        }

        if (e instanceof Creature target) {
            if(target.hurt(damage)) {
                float knockX = (target.getX() > this.x) ? KNOCKBACK_FORCE * knockPercent  : -KNOCKBACK_FORCE * knockPercent;
                target.applyKnockback(knockX, -10f);
            }
        }
    }
}