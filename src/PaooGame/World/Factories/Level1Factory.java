package PaooGame.World.Factories;

import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Cyclops;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Skeleton;
import java.util.Set;

public class Level1Factory extends EntityFactory {
    private static final Set<Integer> SOLID_TILES = Set.of(
            1, 2, 4, 9, 10, 12, 13, 15, 20, 21,
            22, 24, 31, 32, 38, 42, 43, 45, 46, 47,
            56, 57, 58, 67, 68, 69, 656, 657, 658, 720,
            721, 722, 848, 849, 850, 3113, 3114, 3115, 3177, 3178,
            3179, 3241, 3242, 3243, 3305, 3306, 3307, 3647, 3648, 3649,
            3839, 3840, 3841
    );

    public Level1Factory(Handler handler) {
        super(handler);

        this.solidTiles = SOLID_TILES;
        InitTiles(VisualAssets.worldTiles1);
    }

    @Override
    public Creature createMinorEnemy(float x, float y) { return new Skeleton(handler, x, y); }

    @Override
    public Creature createMajorEnemy(float x, float y) { return new Cyclops(handler, x, y); }
}