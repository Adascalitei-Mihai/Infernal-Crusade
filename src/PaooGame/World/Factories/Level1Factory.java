package PaooGame.World.Factories;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.Skeleton;
import PaooGame.World.Entities.Creatures.Enemies.Cyclop;
import PaooGame.World.Entities.Environment.Checkpoint;
import PaooGame.World.Entities.Environment.SpearGate;
import PaooGame.World.Entities.Environment.CameraTrigger;
import PaooGame.World.Tiles.SolidTile;
import PaooGame.World.Tiles.TransparentTile;

import static PaooGame.World.Tiles.Tile.NO_TILES;

public class Level1Factory extends EntityFactory {

    public Level1Factory(Handler handler) {
        super(handler);
        InitTiles();
    }

    @Override
    public void InitTiles() {
        for(int i = 0; i < NO_TILES; i++) {
            if(Assets.worldTiles[i] != null) {
                if ((i >= 1 && i <= 121) || (i >= 2458 && i <= 4633)) {
                    new SolidTile(Assets.worldTiles[i], i);
                } else {
                    new TransparentTile(Assets.worldTiles[i], i);
                }
            }
        }
    }

    @Override
    public Creature createMinorEnemy(float x, float y) {
        return null; // new Skeleton(handler, x, y);
    }

    @Override
    public Creature createMajorEnemy(float x, float y) {
        return null; // new Cyclop(handler, x, y);
    }

    @Override
    public Checkpoint createCheckpoint(float x, float y, int id) {
        return new Checkpoint(handler, x, y, id);
    }

    @Override
    public SpearGate createSpearGate(float x, float y) {
        return new SpearGate(handler, x, y);
    }

    @Override
    public CameraTrigger createTrigger(float x, float y, int centerX, int centerY) {
        return new CameraTrigger(handler, x, y, centerX, centerY);
    }
}