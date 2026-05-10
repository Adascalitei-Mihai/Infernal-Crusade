package PaooGame.World.Entities.Spells;

import PaooGame.Graphics.Animation;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Boss;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Necromancer;
import PaooGame.World.Entities.Entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashSet;

/**
 * Clasa abstracta care reprezinta o vraja sau un atac magic in joc.
 * Gestioneaza stadiile atacului (WINDUP, ACTIVE, RECOVERY), coliziunile si desenarea.
 */
public abstract class Spell extends Entity {
    protected final static float KNOCKBACK_FORCE = 10f;
    protected final static float LIFESTEAL = 100f;

    public enum SpellState {
        WINDUP, ACTIVE, RECOVERY, DEAD
    }

    protected Animation animation;
    protected SpellState currState = SpellState.WINDUP;

    protected int visualWidth, visualHeight;
    protected Creature owner;
    protected float damage;
    protected float knockPercent;

    protected int windupTime, activeTime, recoveryTime;
    protected int stateTimer;

    // Lista entitatilor deja lovite pentru a nu le rani de doua ori in acelasi "burst"
    private final HashSet<Entity> entitiesHit = new HashSet<>();

    /**
     * Metoda care initializeaza atributele de baza si stadiile unei vraji.
     */
    public Spell(Handler handler, float x, float y, int logicWidth, int logicHeight,
                 int visualWidth, int visualHeight, Creature owner, float damage, float knockPercent,
                 int windupTime, int activeTime, int recoveryTime) {
        super(handler, x, y, logicWidth, logicHeight);

        this.visualWidth = visualWidth;
        this.visualHeight = visualHeight;
        this.owner = owner;
        this.damage = damage;
        this.knockPercent = knockPercent;

        this.windupTime = windupTime;
        this.activeTime = activeTime;
        this.recoveryTime = recoveryTime;

        this.stateTimer = windupTime;
    }

    protected abstract void setAnimations();

    @Override
    public void Update() {
        if (currState == SpellState.DEAD) {
            active = false;
            return;
        }

        updateStateTimer();

        // Daca am ajuns in faza activa, fortam verificarea hitbox-urilor
        if (currState == SpellState.ACTIVE) {
            triggerSpellInteractions();
        }

        if (animation != null) {
            animation.Update();
        }
    }

    private void updateStateTimer() {
        stateTimer--;

        if (stateTimer <= 0) {
            switch (currState) {
                case WINDUP:
                    currState = SpellState.ACTIVE;
                    stateTimer = activeTime;
                    // Resetam lista in caz ca vrem o vraja care pulseaza (multy-hit)
                    entitiesHit.clear();
                    break;
                case ACTIVE:
                    currState = SpellState.RECOVERY;
                    stateTimer = recoveryTime;
                    break;
                case RECOVERY:
                    currState = SpellState.DEAD;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Verifica activ toate entitatile din bounds in timpul fazei ACTIVE.
     */
    public void triggerSpellInteractions() {
        Rectangle spellBounds = getCollisionBounds(0f, 0f);

        for (Entity e : handler.getEntities()) {
            if (e.equals(this) || e.equals(owner)) {
                continue;
            }

            if (spellBounds.intersects(e.getCollisionBounds(0f, 0f))) {
                this.onInteraction(e);
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (logicWidth - visualWidth) / 2;
        int drawY = screenY + (logicHeight - visualHeight);

        if (animation != null && animation.getCurrentFrame() != null) {
            g.drawImage(animation.getCurrentFrame(), drawX, drawY, visualWidth, visualHeight, null);
        }

        if (handler.getDebugMode()) {
            // Desenam rosu cand e periculos, violet cand e sigur
            if (currState == SpellState.ACTIVE) {
                g.setColor(new Color(255, 0, 0, 150));
            } else {
                g.setColor(new Color(255, 0, 255, 100));
            }
            g.fillRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);
        }
    }

    /**
     * Metoda care aplica consecintele coliziunii vrajii cu o alta entitate.
     * Acum functioneaza DOAR in faza ACTIVE si protejeaza contra "multihit-ului" accidental.
     */
    @Override
    public void onInteraction(Entity e) {
        // Daca nu suntem in faza de damage, ignoram complet coliziunea!
        if (currState != SpellState.ACTIVE) {
            return;
        }

        // Daca am lovit deja entitatea asta in timpul acestui atac, nu o mai lovim iar!
        if (entitiesHit.contains(e)) {
            return;
        }

        if (e instanceof Necromancer target) {
            target.heal(LIFESTEAL);
            entitiesHit.add(e); // Marcam ca am interactionat
            return;
        }
        if (e instanceof Boss target) {
            target.heal(LIFESTEAL);
            entitiesHit.add(e);
            return;
        }

        if (e instanceof Creature target) {
            if (target.hurt(damage)) {
                float knockX = (target.getX() > this.x) ? KNOCKBACK_FORCE * knockPercent : -KNOCKBACK_FORCE * knockPercent;
                target.applyKnockback(knockX, -10f);
                entitiesHit.add(target); // Il punem pe lista victimelor
            }
        }
    }
}