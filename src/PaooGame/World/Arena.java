package PaooGame.World;

import PaooGame.Handler;
import java.awt.Rectangle;

public class Arena {
    private final Handler handler;

    private final int[][][][] waves;
    private final Rectangle arenaBounds;

    private int currWave = 0;
    private boolean waveDone = false;

    private final static int WAVE_CD = 300;
    private int waveTimer = 0;

    private boolean isRunning = false;
    private int noCreaturesStart;

    public Arena(Handler handler, int[][][][] waves, int xArena, int yArena, int widthArena, int heightArena) {
        this.handler = handler;
        this.waves = waves;
        this.arenaBounds = new Rectangle(xArena, yArena, widthArena, heightArena);
    }

    public void Update() {
        if (!isRunning) return;

        if (!waveDone) {
            if (handler.getCreatureCount() <= noCreaturesStart) {
                waveDone = true;
                System.out.println("Wave gata!");
                waveTimer = WAVE_CD;
            }
        }
        else {
            waveTimer--;
            if (waveTimer <= 0) {
                currWave++;

                if (currWave < waves.length) {
                    spawnWave(currWave);
                    waveDone = false;
                }
                else {
                    isRunning = false;

                    handler.getGameCamera().resetToLevelBounds();
                }
            }
        }
    }

    public void start() {
        if (!isRunning && currWave == 0) {
            isRunning = true;
            noCreaturesStart = handler.getCreatureCount();

            handler.getGameCamera().setCustomBounds(
                    arenaBounds.x, arenaBounds.y, arenaBounds.width, arenaBounds.height
            );

            spawnWave(currWave);
            waveDone = false;
        }
    }

    private void spawnWave(int waveIndex) {
        int[][] minorEnemies = waves[waveIndex][0];
        int[][] majorEnemies = waves[waveIndex][1];

        for (int[] enemyPos : minorEnemies) {
            handler.addEntity(
                    handler.getLevel().getFactory().createMinorEnemy(enemyPos[0], enemyPos[1]));
        }

        for (int[] enemyPos : majorEnemies) {
            handler.addEntity(
                    handler.getLevel().getFactory().createMajorEnemy(enemyPos[0], enemyPos[1]));
        }
    }

    public boolean isFinished() {
        return !isRunning && currWave >= waves.length;
    }
}