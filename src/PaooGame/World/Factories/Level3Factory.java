package PaooGame.World.Factories;

import PaooGame.Graphics.VisualAssets;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Boss;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Necromancer;
import java.util.Set;

/**
 * Clasa care initializeaza tile-urile si poate crea inamici pentru {@link PaooGame.World.Levels.Level3}
 */
public class Level3Factory extends EntityFactory {
    private static final Set<Integer> SOLID_TILES = Set.of(
            16, 49, 59, 75, 77, 109, 110, 111, 126, 127,
            128, 144, 145, 1390, 1391, 1392, 1393, 1394, 1398,
            1400, 1401, 1402, 1403, 1406, 1407, 1408
    );

    public Level3Factory(Handler handler) {
        super(handler);

        this.solidTiles = SOLID_TILES;
        InitTiles(VisualAssets.worldTiles3);
    }

    @Override
    public Creature createMinorEnemy(float x, float y) { return new Necromancer(handler, x, y); }

    @Override
    public Creature createMajorEnemy(float x, float y) {return new Boss(handler, x, y); }
}