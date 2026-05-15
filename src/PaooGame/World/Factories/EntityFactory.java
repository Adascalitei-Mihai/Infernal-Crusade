package PaooGame.World.Factories;

import PaooGame.Handler;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Environment.ArenaTrigger;
import PaooGame.World.Entities.Environment.Checkpoint;
import PaooGame.World.Entities.Environment.Gate;
import PaooGame.World.Tile;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.util.Set;
import static PaooGame.World.Tile.NO_TILES;

/**
 * Clasa care implementeaza modelul Abstract Factory
 */
public abstract class EntityFactory {
    protected final Handler handler;
    protected Set<Integer> solidTiles;

    public EntityFactory(Handler handler) {
        this.handler = handler;
    }

    public void InitTiles(BufferedImage[] tiles) {
        if (Tile.TILES[0] == null) {
            new Tile(null, 0, false);
        }

        for(int i = 0; i < NO_TILES; i++) {
            if(tiles[i] != null) {
                if (solidTiles.contains(i)) {
                    new Tile(tiles[i], i, true);
                } else {
                    new Tile(tiles[i], i, false);
                }
            }
        }
    }
    public ArenaTrigger createArenaTrigger(float x, float y, int width, int height,
                                           Arena arena, Gate[] gates, Clip bgSong) {
        return new ArenaTrigger(handler, x, y, width, height, arena, gates, bgSong);
    }

    public Checkpoint createCheckpoint(float x, float y, int id) { return new Checkpoint(handler, x, y, id); }
    public Gate createGate(float x, float y) { return new Gate(handler, x, y); }

    public abstract Creature createMinorEnemy(float x, float y);
    public abstract Creature createMajorEnemy(float x, float y);
}
