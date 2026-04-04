package PaooGame.World.Factories;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Environment.Checkpoint;
import PaooGame.World.Entities.Environment.SpearGate;
import PaooGame.World.Entities.Environment.CameraTrigger;

public abstract class EntityFactory {
    protected final Handler handler;

    public EntityFactory(Handler handler) {
        this.handler = handler;
    }

    public abstract void InitTiles();
    public abstract Creature createMinorEnemy(float x, float y);
    public abstract Creature createMajorEnemy(float x, float y);

    public abstract Checkpoint createCheckpoint(float x, float y, int id);
    public abstract SpearGate createSpearGate(float x, float y);
    public abstract CameraTrigger createTrigger(float x, float y, int width, int height);
}
