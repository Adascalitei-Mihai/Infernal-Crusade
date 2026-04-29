package PaooGame.World.Factories;

import PaooGame.Handler;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Environment.ArenaTrigger;
import PaooGame.World.Entities.Environment.Checkpoint;
import PaooGame.World.Entities.Environment.Gate;

public abstract class EntityFactory {
    protected final Handler handler;

    public EntityFactory(Handler handler) {
        this.handler = handler;
    }

    public abstract void InitTiles();
    public ArenaTrigger createArenaTrigger(float x, float y, int width, int height,
                                           Arena arena, Gate[] gates) {
        return new ArenaTrigger(handler, x, y, width, height, arena, gates);
    }

    public abstract Creature createMinorEnemy(float x, float y);
    public abstract Creature createMajorEnemy(float x, float y);

    public abstract Checkpoint createCheckpoint(float x, float y, int id);
    public abstract Gate createGate(float x, float y);
}
