package PaooGame;

import PaooGame.Database.DatabaseManager;
import PaooGame.Game.Game;
import PaooGame.Game.GameCamera;
import PaooGame.Game.Input.GamepadManager;
import PaooGame.Game.Input.InputManager;
import PaooGame.Game.Input.KeyManager;
import PaooGame.Game.Input.MouseManager;
import PaooGame.States.VisualElements.TransitionManager;
import PaooGame.Score.ScoreManager;
import PaooGame.States.StartMenu;
import PaooGame.States.State;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Levels.Level;
import PaooGame.World.Levels.Level1;
import PaooGame.World.Levels.Level2;
import PaooGame.World.Levels.Level3;

import java.awt.*;
import java.util.ArrayList;

/**
 * Clasa care centralizeaza accesul la resurse, input, baza de date si starea curenta
 */
public class Handler {
    public static final int NR_CHKPTS = 9;
    public enum Difficulty { EASY, NORMAL, HARD }

    private boolean debugMode = false;
    private Difficulty currDifficulty = Difficulty.NORMAL;

    private Game game;
    private State state;
    private Level level;
    private final DatabaseManager dbMan;
    private final ScoreManager scoreMan;
    private final TransitionManager transMan;

    private int playerID;
    private int currLevel, currChkp;
    private int bestScore, globalScore;

    /**
     * Constructorul care initializeaza campurile, conexiunea cu baza de date si starea initiala {@link StartMenu}
     * @param game Jocul
     */
    public Handler(Game game) {
        this.game = game;
        this.transMan = new TransitionManager();
        this.dbMan = new DatabaseManager();
        this.scoreMan = new ScoreManager(this);

        dbMan.connect("jdbc:sqlite:game.db");
        setState(new StartMenu(this));
    }

    /**
     * Metoda care instantiaza nivelul corespunzator progresului curent
     */
    public void initLevel() {
        level = switch (currLevel) {
            case 2 -> new Level2(this);
            case 3 -> new Level3(this);
            default -> new Level1(this);
        };
    }

    /**
     * Metoda care gestioneaza autentificarea jucatorului si incarca scorurile
     */
    public void playerLogin(String playerName) {
        dbMan.loadOrCreatePlayer(playerName, this);
        updateScores();
    }

    public void updateScores() {
        bestScore = dbMan.getTotalBestScore(playerID, NR_CHKPTS);
        globalScore = dbMan.getGlobalBestScore(NR_CHKPTS);
    }

    /**
     * Metoda care actualizeaza checkpoint-ul curent si salveaza scorul in baza de date
     * @param newChkp Checkpoint-ul nou
     */
    public void updateCurrChkp(int newChkp) {
        int score = scoreMan.calculateFinalScore();
        scoreMan.resetCheckpointTimer();

        dbMan.saveScore(playerID, currLevel, currChkp, newChkp, score);
        currChkp = newChkp;
        dbMan.updatePlayerCheckpoint(playerID, currChkp);
    }

    public void resetEnd() {
        dbMan.updatePlayerCheckpoint(playerID, 1);
    }

    /**
     * Metoda care actualizeaza nivelul curent si reseteaza checkpoint-ul
     * @param newLevel Nivelul nou
     */
    public void updateCurrLevel(int newLevel) {
        currLevel = newLevel;
        currChkp = 0;
        dbMan.updatePlayerProgress(playerID, newLevel, 0);
    }

    /**
     * Metoda care inchide conexiunea cu baza de date si opreste thread-ul principal de joc
     */
    public void StopGame() {
        if (dbMan != null) {
            dbMan.close();
        }
        game.StopGame();
    }

    /**
     * Metoda care transmite {@link State#Update()} si {@link TransitionManager#Update()}
     */
    public void Update() {
        if (state != null) {
            state.Update();
        }
        transMan.Update();
    }

    /**
     * Metoda care transmite {@link State#Draw(Graphics)} si {@link TransitionManager#Draw(Graphics, int, int)}
     * @param g Grafica de desenat
     */
    public void Draw(Graphics g) {
        if (state != null) {
            state.Draw(g);
        }
        transMan.Draw(g, game.getWidth(), game.getHeight());
    }

    public TransitionManager getTransitionManager() { return transMan; }
    public ScoreManager getScoreMan() { return scoreMan; }
    public DatabaseManager getDbMan() { return dbMan; }

    public KeyManager getKeyManager() { return game.getKeyMan(); }
    public GamepadManager getPadManager() { return game.getPadMan(); }
    public InputManager getInputManager() { return game.getInputMan(); }
    public void setInput(InputManager inputMan) { game.setInput(inputMan); }
    public MouseManager getMouseManager() { return game.getMouseMan(); }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public GameCamera getGameCamera() { return game.getGameCamera(); }
    public float getxOffset() { return game.getGameCamera().getxOffset(); }
    public float getyOffset() { return game.getGameCamera().getyOffset(); }
    public int getWidth() { return game.getWidth(); }
    public int getHeight() { return game.getHeight(); }
    public void setCursor(Cursor c) { game.getWnd().getCanvas().setCursor(c); }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
    public ArrayList<Entity> getEntities() { return level.getEntityManager().getEntities(); }
    public Dante getDante() { return level.getEntityManager().getDante(); }
    public int getCreatureCount() { return level.getEntityManager().getEnemyCount(); }
    public void addEntity(Entity e) { level.getEntityManager().addEntity(e); }

    public int getPlayerID() { return playerID; }
    public void setPlayerID(int playerID) { this.playerID = playerID; }
    public int getCurrLevel() { return currLevel; }
    public void setCurrLevel(int currLevel) { this.currLevel = currLevel; }
    public int getCurrChkp() { return currChkp; }
    public void setCurrChkp(int currChkp) { this.currChkp = currChkp; }
    public Difficulty getDifficulty() { return currDifficulty; }
    public void setDifficulty(Difficulty difficulty) { this.currDifficulty = difficulty; }
    public int getBestScore() { return bestScore; }
    public int getGlobalScore() { return globalScore; }

    public boolean getDebugMode() { return debugMode; }
    public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }
}