package PaooGame;

import PaooGame.Database.DatabaseManager;
import PaooGame.Entities.Creatures.Dante;
import PaooGame.Graphics.GameCamera;
import PaooGame.Graphics.GameWindow;
import PaooGame.Input.KeyManager;
import PaooGame.Input.MouseManager;
import PaooGame.World;

public class Handler {
    private Game game;
    private World world;

    public Handler(Game game) {
        this.game = game;
    }

    // --- METODE PROXY PENTRU GAME (Cele cerute de tine) ---

    public GameWindow getWnd() { return game.getWnd(); }

    public void setPlayerName(String name) { game.setPlayerName(name); }
    public String getPlayerName() { return game.getPlayerName(); }

    public void setCurrChkp(int chkp) { game.setCurrChkp(chkp); }
    public int getCurrChkp() { return game.getCurrChkp(); }

    public void setCurrLevel(int currLevel) { game.setCurrLevel(currLevel); }
    public int getCurrLevel() { return game.getCurrLevel(); }

    public void setPlayerID(int playerID) { game.setPlayerID(playerID); }
    public int getPlayerID() { return game.getPlayerID(); }

    public DatabaseManager getDbManager() { return game.getDbManager(); }

    public void setLastChkp(int lastChkp) { game.setLastChkp(lastChkp); }
    public int getLastChkp() { return game.getLastChkp(); }

    public void setStartTime(long startTime) { game.setStartTime(startTime); }
    public long getStartTime() { return game.getStartTime(); }

    public void setDifficulty(int difficulty) { game.setDifficulty(difficulty); }
    public int getDifficulty() { return game.getDifficulty(); }

    public void setBestScore(int bestScore) { game.setBestScore(bestScore); }
    public int getBestScore() { return game.getBestScore(); }

    public void setGlobalScore(int globalScore) { game.setGlobalScore(globalScore); }
    public int getGlobalScore() { return game.getGlobalScore(); }

    public void setDante(Dante dante) { game.setDante(dante); }
    public Dante getDante() { return game.getDante(); }

    // --- METODE UTILITARE STANDARD ---

    public void StopGame() { game.StopGame(); }

    public KeyManager getKeyManager() { return game.getKeyManager(); }
    public MouseManager getMouseManager() { return game.getMouseManager(); }
    public GameCamera getGameCamera() { return game.getGameCamera(); }

    public int getWidth() { return game.getWidth(); }
    public int getHeight() { return game.getHeight(); }

    public World getWorld() { return world; }
    public void setWorld(World world) { this.world = world; }

    public Game getGame() { return game; }
}