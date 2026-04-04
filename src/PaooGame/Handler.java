package PaooGame;

import PaooGame.Database.DatabaseManager;

import PaooGame.Game.Game;
import PaooGame.Game.GameCamera;
import PaooGame.Game.Input.KeyManager;
import PaooGame.Game.Input.MouseManager;
import PaooGame.State.State;
import PaooGame.World.World;

import java.awt.*;

public class Handler {
    public static final int NR_CHKPTS = 3;

    private static State state;
    private Game game;
    private World world;
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
    }

    public void playerLogin(String playerName) {
        dbMan.loadOrCreatePlayer(playerName, this);
        bestScore = dbMan.getTotalBestScore(playerID, NR_CHKPTS);
    }

    public void StopGame() {
        if (dbMan != null) {
            dbMan.close();
        }
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
        game.getWnd().getFrame().setCursor(c);
    }

    public KeyManager getKeyManager() { return game.getKeyMan(); }
    public MouseManager getMouseManager() { return game.getMouseMan(); }
    public GameCamera getGameCamera() { return game.getGameCamera(); }

    public int getWidth() { return game.getWidth(); }
    public int getHeight() { return game.getHeight(); }

    public static State getState() {
        return state;
    }

    public static void setState(State state) {
        Handler.state = state;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
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
}