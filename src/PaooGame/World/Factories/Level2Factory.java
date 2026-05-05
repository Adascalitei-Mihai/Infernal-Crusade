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

public class Level2Factory extends EntityFactory {
    private static final Set<Integer> SOLID_TILES = Set.of(
            154, 153, 155, 156, 157, 158, 152
            );

    public Level2Factory(Handler handler) {
        super(handler);
        InitTiles();
    }

    @Override
    public void InitTiles() {
        if (Tile.tiles[0] == null) {
            new Tile(null, 0, false);
        }

        for(int i = 0; i < NO_TILES; i++) {
            if(VisualAssets.worldTiles2[i] != null) {
                if (SOLID_TILES.contains(i)) {
                    new Tile(VisualAssets.worldTiles2[i], i, true);
                } else {
                    new Tile(VisualAssets.worldTiles2[i], i, false);
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