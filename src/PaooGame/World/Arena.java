package PaooGame.World;

import PaooGame.Handler;
import java.awt.Rectangle;

/**
 * Clasa care gestioneaza o arena de lupta
 */
public class Arena {
    private final static int WAVE_CD = 300;

    private final Handler handler;
    /**
     * Campul care retine pozitiile inamicilor <br>
     * Prima dimensiune retine valurile <br>
     * A doua dimensiune este tipul de inamic {@code [0] / [1] => minorEnemy / majorEnemy} <br>
     * A treia dimensiunea retine poztiile inamicilor <br>
     * A patra dimensiune este pozitia inamicului {@code [0] / [1] => x / y}
     */
    private final int[][][][] waves;
    private final Rectangle arenaBounds;

    private int currWave = 0;
    private boolean waveDone = false;
    private int waveTimer = 0;

    private boolean isRunning = false;
    /**
     * Campul care retine numarul de creaturi in momentul de {@link #start()}
     */
    private int noCreaturesStart;

    /**
     * Constructorul care initializeaza campurile
     * @param handler Handler-ul
     * @param waves Matricea 4D care contine coordonatele inamicilor pentru fiecare val
     * @param xArena Coltul din stanga
     * @param yArena Coltul de sus
     * @param widthArena Latimea arenei
     * @param heightArena Inaltimea arenei
     */
    public Arena(Handler handler, int[][][][] waves, int xArena, int yArena, int widthArena, int heightArena) {
        this.handler = handler;
        this.waves = waves;
        this.arenaBounds = new Rectangle(xArena, yArena, widthArena, heightArena);
    }

    /**
     * Metoda care actualizeaza logica arenei la fiecare cadru <br>
     * Verifica numarul inamicilor activi, declanseaza cronometrul pentru urmatorul val
     * si elibereaza camera jucatorului la finalizarea tuturor luptelor
     */
    public void Update() {
        if (!isRunning) {
            return;
        }

        if (!waveDone) {
            if (handler.getCreatureCount() <= noCreaturesStart) {
                waveDone = true;
                waveTimer = WAVE_CD;
            }
        } else {
            waveTimer--;

            if (waveTimer <= 0) {
                currWave++;

                if (currWave < waves.length) {
                    spawnWave(currWave);
                    waveDone = false;
                } else {
                    isRunning = false;
                    handler.getGameCamera().resetToLevelBounds();
                }
            }
        }
    }

    /**
     * Metoda care porneste mecanismul arenei, blocheaza camera jucatorului in limitele specificate ({@link PaooGame.Game.GameCamera#setCustomBounds(float, float, float, float)}) si invoca primul val de inamici
     */
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

    /**
     * Metoda care citeste matricea valului specificat si creeaza entitatile corespunzatoare
     * folosind {@link PaooGame.World.Factories.EntityFactory} a nivelului curent
     * @param waveIndex Indexul valului care trebuie generat pe harta
     */
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

    public boolean isFinished() { return !isRunning && currWave >= waves.length; }
    public boolean isRunning() { return isRunning; }
    public int getCurrWave() { return currWave; }
}