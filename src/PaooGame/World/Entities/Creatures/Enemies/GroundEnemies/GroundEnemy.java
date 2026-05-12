package PaooGame.World.Entities.Creatures.Enemies.GroundEnemies;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;
import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa abstracta care reprezinta un inamic terestru
 */
public abstract class GroundEnemy extends Enemy {

    /**
     * Constructorul care initializeaza campurile
     */
    public GroundEnemy(Handler handler, float x, float y, int width, int height, float hp, float weight) {
        super(handler, x, y, width, height, hp, weight);
    }

    /**
     * Metoda care adauga forta gravitationala la miscarea verticala
     */
    @Override
    protected void applyVerticalMovement() { yMove += gravity; }

    /**
     * Metoda care verifica atat coliziunea cu un perete in fata {@link #collisionWithTile(int, int)}, cat si existenta
     * unui bloc transparent dedesubt {@link #checkHorizontalCollision()}
     * @return Daca drumul este blocat sau daca urmeaza o prapastie
     */
    @Override
    protected boolean checkPathBlocked() {
        int nextX = (int) (x + bounds.x + xMove + (direction == 1 ? bounds.width : 0)) / TILE_SIZE;
        int belowY = (int) (y + bounds.y + bounds.height + 1) / TILE_SIZE;

        return !collisionWithTile(nextX, belowY) || checkHorizontalCollision();
    }
}