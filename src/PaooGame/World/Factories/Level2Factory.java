package PaooGame.World.Factories;

import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies.Ghoul;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Hunter;
import java.util.Set;

public class Level2Factory extends EntityFactory {
    private static final Set<Integer> SOLID_TILES = Set.of(
            662, 679, 680, 681, 682, 683, 684, 685, 2806, 2807,
            2863, 3427, 3428, 3429, 3430, 3431, 3432, 3433, 3434, 3435,
            3436, 3440, 3441, 3442, 3443, 3447, 3448, 3577, 3578, 3579,
            3580, 3581, 3582, 3583, 3584, 3585, 3586, 3597, 3598, 3752,
            3753, 3754, 3755, 3756, 3757, 3758, 3759, 3760, 3761, 3762,
            3763, 3764, 3765, 3766, 3767, 3896, 3897, 3898, 3899, 3900,
            3901, 3902, 3903, 3904, 3905, 3906, 3907, 3908, 3909, 3910,
            3911, 3912, 3913, 3914, 3915, 3916, 3917, 3918
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