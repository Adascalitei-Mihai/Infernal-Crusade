package PaooGame.World.Entities.Creatures;

import PaooGame.Graphics.Animation;
import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.Game.Game;

public abstract class Creature extends Entity {
    protected float hp;
    public Creature(Handler handler, float x, float y, int width, int height, float hp) {
        super(handler, x, y, width, height);
        this.hp = hp;
    }

    public abstract void Move();
    public abstract void hurt(float dmg);
    public void setHp(float hp) { this.hp = hp; }
    public float getHp() { return hp; }
}
