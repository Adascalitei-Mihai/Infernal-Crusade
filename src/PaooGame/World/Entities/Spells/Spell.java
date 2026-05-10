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

/**
 * Clasa abstracta care reprezinta o vraja
 */
public abstract class Spell extends Entity {
    private final static float KNOCKBACK_FORCE = 10f;
    private final static float LIFESTEAL = 100f;

    public enum SpellState {
        WINDUP, ACTIVE, RECOVERY, DEAD
    }

    protected Animation animation;
    private SpellState currState = SpellState.WINDUP;

    private final Creature owner;
    private final float damage;
    private final float knockPercent;

    private final int activeTime, recoveryTime;
    private int stateTimer;
    private boolean hasDealtDamage;

    /**
     * Constructorul care initializeaza campurile
     * @param handler Handler-ul
     * @param x Pozitia pe OX
     * @param y Pozitia pe axa Oy
     * @param logicWidth Latimea logica
     * @param logicHeight Inaltimea logica
     * @param visualWidth Latimea vizuala
     * @param visualHeight Inaltimea vizuala
     * @param owner Proprietarul
     * @param damage DMG
     * @param knockPercent Procentajul aplicat din {@link #KNOCKBACK_FORCE} in urma unei coliziuni
     * @param windupTime Timpul de incarcare
     * @param activeTime Timpul activ
     * @param recoveryTime Timpul de disipare
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

        this.activeTime = activeTime;
        this.recoveryTime = recoveryTime;

        this.stateTimer = windupTime;
    }

    /**
     * Metoda abstracta care forteaza clasele derivate sa isi seteze propriile cadre de animatie
     */
    protected abstract void setAnimations();

    /**
     * Metoda care actualizeaza logica vrajii la fiecare cadru, gestionand
     * tranzitiile intre stadiile masinii de stari si animatia curenta
     */
    @Override
    public void Update() {
        if (currState == SpellState.DEAD) {
            active = false;
            return;
        }

        updateStateTimer();

        if (animation != null) {
            animation.Update();
        }
    }

    /**
     * Metoda care scade cronometrul stadiului curent si avanseaza vraja in urmatorul stadiu atunci cand timpul a expirat <br>
     * Declanseaza efectul daunator o singura data la intrarea in stadiul {@link SpellState#ACTIVE}
     */
    private void updateStateTimer() {
        stateTimer--;

        if (stateTimer <= 0) {
            switch (currState) {
                case WINDUP:
                    currState = SpellState.ACTIVE;
                    stateTimer = activeTime;
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

        if (currState == SpellState.ACTIVE && !hasDealtDamage) {
            triggerSpellInteractions();
            hasDealtDamage = true;
        }
    }

    /**
     * Metoda care verifica coliziunile vrajii cu alte entitati din nivel
     * si declanseaza interactiunea specifica in caz de impact
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

    /**
     * Metoda care deseneaza vraja pe ecran, centrand imaginea pe entitatea aleasa
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        int drawX = screenX + (logicWidth - visualWidth) / 2;
        int drawY = screenY + (logicHeight - visualHeight); // Centrat pe latura de jos (podea)

        if (animation != null && animation.getCurrentFrame() != null) {
            g.drawImage(animation.getCurrentFrame(), drawX, drawY, visualWidth, visualHeight, null);
        }

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 0, 255, 100));
            g.fillRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);
        }
    }

    /**
     * Metoda care aplica consecintele coliziunii vrajii cu o alta entitate {@link Creature#hurt(float)} si {@link Creature#applyKnockback(float, float)} <br>
     * Ofera un efect de vindecare (lifesteal) daca loveste anumite entitati inamice specifice
     * @param e Entitatea cu care se interactioneaza
     */
    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Necromancer target) {
            target.heal(LIFESTEAL);
            return;
        }
        if (e instanceof Boss target) {
            target.heal(LIFESTEAL);
            return;
        }

        if (e instanceof Creature target) {
            if (target.hurt(damage)) {
                float knockX = (target.getX() > this.x) ? KNOCKBACK_FORCE * knockPercent : -KNOCKBACK_FORCE * knockPercent;
                target.applyKnockback(knockX, -10f);
            }
        }
    }
}