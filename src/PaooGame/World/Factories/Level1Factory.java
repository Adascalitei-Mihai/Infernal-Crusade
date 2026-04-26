package PaooGame.World.Factories;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Cyclops;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Skeleton;
import PaooGame.World.Entities.Environment.Checkpoint;
import PaooGame.World.Entities.Environment.SpearGate;
import PaooGame.World.Tile;

import java.util.Set;

import static PaooGame.World.Tile.NO_TILES;

public class Level1Factory extends EntityFactory {
    private static final Set<Integer> SOLID_TILES = Set.of(
            1, 2, 9, 20, 38, 22, 12, 43, 42, 32, 45, 46, 56, 67, 57, 58, 68, 47, 13, 24, 21, 10, 31, 4, 15, 848, 849, 850, 656, 657, 658, 720, 721, 722
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
            if(Assets.worldTiles[i] != null) {
                if (SOLID_TILES.contains(i)) {
                    new Tile(Assets.worldTiles[i], i, true);
                } else {
                    new Tile(Assets.worldTiles[i], i, false);
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
    public SpearGate createSpearGate(float x, float y) {
        return new SpearGate(handler, x, y);
    }
}