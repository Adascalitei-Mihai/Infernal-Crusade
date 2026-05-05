package PaooGame.World.Levels;

import PaooGame.Audio.SongAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Environment.Gate;
import PaooGame.World.Factories.Level1Factory;
import PaooGame.World.Factories.Level2Factory;

import static PaooGame.World.Tile.TILE_SIZE;

public class Level2 extends Level {
    private static final int L1_WIDTH = 200;
    private static final int L1_HEIGHT = 300;

    private static final int[][] CHKP_POS = {
            {1, 20},
            {219, 20},
            {436, 20}
    };


    public Level2(Handler handler) {
        super(handler, new Level2Factory(handler),
                L1_WIDTH, L1_HEIGHT, "/textures/Maps/L2/L2.tmx", SongAssets.level2);

        calculateExposureMasks();
        requestRespawn();
    }

    @Override
    public void spawn() {
        handler.getGameCamera().resetToLevelBounds();
        AudioManager.playBGM(bgSong);

        entityManager.getDante().setX(CHKP_POS[handler.getCurrChkp()][0] * TILE_SIZE);
        entityManager.getDante().setY(CHKP_POS[handler.getCurrChkp()][1] * TILE_SIZE);

        entityManager.resetEntities();

        setEnemies();
        handler.getDante().resetDeath();
        handler.getGameCamera().snapTo(handler.getDante());
    }
}
