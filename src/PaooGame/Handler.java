package PaooGame;

import PaooGame.Database.DatabaseManager;

import PaooGame.Game.Game;
import PaooGame.Game.GameCamera;
import PaooGame.Game.Input.KeyManager;
import PaooGame.Game.Input.MouseManager;
import PaooGame.States.StartMenu;
import PaooGame.States.State;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Levels.Level;

import java.awt.*;
import java.util.ArrayList;

public class Handler {
    public static final int NR_CHKPTS = 3;

    private State state;
    private Game game;
    private Level level;
    private DatabaseManager dbMan;

    private int playerID;
    private String playerName;
    private int currLevel, currChkp;

    private int lastChkp;
    private long startTime;
    private int difficulty;

    private int bestScore;
    private int globalScore;

    public Handler(Game game) {
        this.game = game;

        dbMan = new DatabaseManager();
        dbMan.connect("jdbc:sqlite:database/game.db");
        globalScore = dbMan.getGlobalBestScore(NR_CHKPTS);

        setState(new StartMenu(this));
    }

    public void playerLogin(String playerName) {
        dbMan.loadOrCreatePlayer(playerName, this);
        bestScore = dbMan.getTotalBestScore(playerID, NR_CHKPTS);
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
    }

    public void Draw(Graphics g) {
        if(state != null) {
            state.Draw(g);
        }
    }

    public void setCursor(Cursor c) {
        game.getWnd().getCanvas().setCursor(c);
    }

    public KeyManager getKeyManager() { return game.getKeyMan(); }
    public MouseManager getMouseManager() { return game.getMouseMan(); }
    public GameCamera getGameCamera() { return game.getGameCamera(); }

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

    public int getLastChkp() {
        return lastChkp;
    }

    public void setLastChkp(int lastChkp) {
        this.lastChkp = lastChkp;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
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


}