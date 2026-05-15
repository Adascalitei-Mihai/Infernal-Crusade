package PaooGame.World.Factories;

import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies.Ghoul;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Hunter;
import java.util.Set;

/**
 * Clasa care initializeaza tile-urile si poate crea inamici pentru {@link PaooGame.World.Levels.Level2}
 */
public class Level2Factory extends EntityFactory {
    private static final Set<Integer> SOLID_TILES = Set.of(
            594, 604, 605, 679, 680, 2806, 2807, 3410, 3411, 3415,
            3416, 3417, 3418, 3419, 3420, 3421, 3422, 3423, 3424, 3425,
            3560, 3561, 3565, 3566, 3567, 3568, 3569, 3570, 3571, 3572,
            3573, 3574, 3575, 3735, 3736, 3739, 3740, 3741, 3742, 3743,
            3744, 3745, 3746, 3747, 3748, 3749, 3750, 3751, 3752, 3753,
            3754, 3755, 3884, 3885, 3886, 3887, 3888, 3889, 3890, 3891,
            3892, 3893, 3894, 3895, 3896, 3897, 3898, 3899, 3900, 3901,
            3902, 3903, 3904, 3905, 3906, 4140
    );

    public Level2Factory(Handler handler) {
        super(handler);

        this.solidTiles = SOLID_TILES;
        InitTiles(VisualAssets.worldTiles2);
    }

    @Override
    public Creature createMinorEnemy(float x, float y) { return new Hunter(handler, x, y); }

    @Override
    public Creature createMajorEnemy(float x, float y) {return new Ghoul(handler, x, y); }
}