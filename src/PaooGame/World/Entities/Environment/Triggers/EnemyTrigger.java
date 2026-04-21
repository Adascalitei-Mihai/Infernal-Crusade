package PaooGame.World.Entities.Environment.Triggers;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;
import PaooGame.World.Entities.Entity;

public class EnemyTrigger extends Trigger {
    public EnemyTrigger(Handler handler, float x, float y, int width, int height) {
        super(handler, x, y, width, height);
    }

    @Override
    public void Update() {
        triggered = false;
    }

    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Enemy) {
            triggered = true;
        }
    }
}
