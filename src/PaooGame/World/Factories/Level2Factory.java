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
            662, 679, 680, 681, 682, 683, 684, 685, 3896, 3897,
            3898, 3899, 3900, 3901, 3902, 3903, 3904, 3905, 3906, 3907,
            3908, 3909, 3910, 3911, 3912, 3913, 3914, 3915, 3916, 3917,
            3918, 3752, 3753, 3754, 3755, 3756, 3757, 3758, 2806, 2807,
            3759, 3760, 3761, 3762, 3763, 3764, 3765, 3766, 3767, 3597,
            3598, 3577, 3578, 3579, 3580, 3581, 3582, 3583, 3584, 3585,
            3586, 3447, 3448, 3427, 3428, 3429, 3430, 3431, 3432, 3433,
            3434, 3435, 3436, 3440, 3441, 3442, 3443, 2863
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