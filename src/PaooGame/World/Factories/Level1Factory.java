package PaooGame.World.Factories;

import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Cyclops;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Skeleton;
import PaooGame.World.Entities.Environment.Checkpoint;
import PaooGame.World.Entities.Environment.Gate;
import PaooGame.World.Tile;

import java.util.Set;

import static PaooGame.World.Tile.NO_TILES;

public class Level1Factory extends EntityFactory {
    private static final Set<Integer> SOLID_TILES = Set.of(
            1, 2, 9, 20, 38, 22, 12, 43, 42, 32, 45, 46, 56, 67, 57, 58, 68, 47, 13, 24, 21, 10, 31, 4, 15, 848, 849, 850, 656, 657, 658, 720, 721, 722, 69, 3305, 3306, 3307, 3113, 3114, 3115, 3177, 3178, 3179, 3839, 3840, 3841, 3647, 3648, 3649
    );

    public Level1Factory(Handler handler) {
        super(handler);
        InitTiles();
    }

    @Override
    public void InitTiles() {
        if (Tile.tiles[0] == null) {
            new Tile(null, 0, false);
        }

        for(int i = 0; i < NO_TILES; i++) {
            if(VisualAssets.worldTiles1[i] != null) {
                if (SOLID_TILES.contains(i)) {
                    new Tile(VisualAssets.worldTiles1[i], i, true);
                } else {
                    new Tile(VisualAssets.worldTiles1[i], i, false);
                }
            }
        }
    }

    @Override
    public Creature createMinorEnemy(float x, float y) {
        return new Skeleton(handler, x, y);
    }

    @Override
    public Creature createMajorEnemy(float x, float y) {
        return new Cyclops(handler, x, y);
    }

    @Override
    public Checkpoint createCheckpoint(float x, float y, int id) {
        return new Checkpoint(handler, x, y, id);
    }

    @Override
    public Gate createGate(float x, float y) {
        return new Gate(handler, x, y);
    }
}