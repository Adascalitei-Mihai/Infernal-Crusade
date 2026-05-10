package PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;

/**
 * Clasa abstracta care reprezinta un inamic zburator
 */
public abstract class FlyingEnemy extends Enemy {
    protected float hoverAngle;
    protected float startY;
    protected float yRange;
    protected float hoverSpeed;

    /**
     * Constructorul care initializeaza campurile si ii elimina forta gravitationala
     */
    public FlyingEnemy(Handler handler, float x, float y, int width, int height, float hp, float weight,
                       float yRange, float hoverSpeed) {
        super(handler, x, y, width, height, hp, weight);
        this.startY = y;
        this.yRange = yRange;
        this.hoverSpeed = hoverSpeed;

        this.gravity = 0f;
    }

    /**
     * Metoda care calculeaza miscarea verticala pe baza unei traiectorii sinusoidale pentru a simula plutirea in aer
     */
    @Override
    protected void applyVerticalMovement() {
        hoverAngle += hoverSpeed;
        if (hoverAngle > Math.PI * 2) {
            hoverAngle -= (float) (Math.PI * 2);
        }

        float targetY = startY + (float) Math.sin(hoverAngle) * yRange;
        float diffY = targetY - this.y;
        float maxRecoverySpeed = baseSpeed * 0.8f;

        if (diffY > maxRecoverySpeed) {
            yMove = maxRecoverySpeed;
        } else {
            yMove = Math.max(diffY, -maxRecoverySpeed);
        }
    }

    /**
     * Metoda care verifica blocajele din fata inamicului zburator {@link #checkHorizontalCollision()}
     * @return Daca se loveste de un perete
     */
    @Override
    protected boolean checkPathBlocked() {
        return checkHorizontalCollision();
    }
}