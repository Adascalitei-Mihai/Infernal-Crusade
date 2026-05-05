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

import java.awt.*;
import java.util.ArrayList;

public class Handler {
    public static final int NR_CHKPTS = 3;
    private boolean debugMode = false;

    private State state;
    private Game game;
    private Level level;
    private DatabaseManager dbMan;
    private ScoreManager scoreMan;
    private TransitionManager transMan;

    private int playerID;
    private String playerName;
    private int currLevel, currChkp;

    public enum Difficulty { EASY, NORMAL, HARD }
    private Difficulty currDifficulty;

    private int bestScore;
    private int globalScore;

    public Handler(Game game) {
        this.game = game;

        transMan = new TransitionManager();

        dbMan = new DatabaseManager();
        dbMan.connect("jdbc:sqlite:game.db");
        globalScore = dbMan.getGlobalBestScore(NR_CHKPTS);

        scoreMan = new ScoreManager(this);
        setState(new StartMenu(this));
    }

    public void playerLogin(String playerName) {
        dbMan.loadOrCreatePlayer(playerName, this);
        bestScore = dbMan.getTotalBestScore(playerID, NR_CHKPTS);
    }
    public void updateCurrChkp(int newChkp) {
        int score = scoreMan.calculateFinalScore();
        scoreMan.resetCheckpointTimer();

        dbMan.saveScore(playerID, currLevel, currChkp, newChkp, score);
        currChkp = newChkp;

        dbMan.updatePlayerCheckpoint(playerID, currChkp);
    }

    public void StopGame() {
        if (dbMan != null) {
            dbMan.close();
        }
        game.StopGame();
    }

    public void Update() {
        if(state != null) {
            state.Update();
        }

        transMan.Update();
    }

    public void Draw(Graphics g) {
        if(state != null) {
            state.Draw(g);
        }

        transMan.Draw(g, game.getWidth(), game.getHeight());
    }

    public TransitionManager getTransitionManager() {
        return transMan;
    }

    public void setCursor(Cursor c) {
        game.getWnd().getCanvas().setCursor(c);
    }

    public ScoreManager getScoreMan() {
        return scoreMan;
    }

    public void setScoreMan(ScoreManager scoreMan) {
        this.scoreMan = scoreMan;
    }

    public KeyManager getKeyManager() { return game.getKeyMan(); }
    public GamepadManager getPadManager() { return game.getPadMan(); }
    public InputManager getInputManager() { return game.getInputMan(); }
    public void setInput(InputManager inputMan) { game.setInput(inputMan); }
    public MouseManager getMouseManager() { return game.getMouseMan(); }
    public GameCamera getGameCamera() { return game.getGameCamera(); }
    public int getCreatureCount() { return level.getEntityManager().getCreatureCount(); }
    public void addEntity(Entity e) { level.getEntityManager().addEntity(e); }

    public int getWidth() { return game.getWidth(); }
    public int getHeight() { return game.getHeight(); }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public DatabaseManager getDbMan() {
        return dbMan;
    }

    public void setDbMan(DatabaseManager dbMan) {
        this.dbMan = dbMan;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getCurrLevel() {
        return currLevel;
    }

    public void setCurrLevel(int currLevel) {
        this.currLevel = currLevel;
    }

    public int getCurrChkp() {
        return currChkp;
    }

    public void setCurrChkp(int currChkp) {
        this.currChkp = currChkp;
    }

    public Difficulty getDifficulty() {
        return currDifficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        currDifficulty = difficulty;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getGlobalScore() {
        return globalScore;
    }

    public void setGlobalScore(int globalScore) {
        this.globalScore = globalScore;
    }

    public float getxOffset() {
        return game.getGameCamera().getxOffset();
    }
    public float getyOffset() {
        return game.getGameCamera().getyOffset();
    }
    public ArrayList<Entity> getEntities() {
        return level.getEntityManager().getEntities();
    }
    public Dante getDante() {
        return level.getEntityManager().getDante();
    }

    public boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}