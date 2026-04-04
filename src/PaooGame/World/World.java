package PaooGame.World;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.EntityManager;
import PaooGame.Game.Game;
import PaooGame.World.Tiles.Tile;

import java.awt.*;

public class World {
    private Handler handler;
    private int width, height; // În tile-uri
    private int spawnX, spawnY;
    private int[][] tiles;
    private EntityManager entityManager;

    public World(Handler handler, String path) {
        this.handler = handler;
        entityManager = new EntityManager(new Dante(handler, 0, 0));
        loadWorld(path);

        entityManager.getDante().setX(spawnX);
        entityManager.getDante().setY(spawnY);
    }

    private void loadWorld(String path) {
        // Aici vei folosi un helper (ex: Utils.loadFileAsString)
        // pentru a citi untitled.tmx și a extrage numerele.
    }

    public void Update() {
        entityManager.Update();
    }

    public void Draw(Graphics g) {
        int xStart = (int) Math.max(0, handler.getGame().getGameCamera().getxOffset() / 16);
        int xEnd = (int) Math.min(width, (handler.getGame().getGameCamera().getxOffset() + handler.getGame().getWidth()) / 16 + 1);
        int yStart = (int) Math.max(0, handler.getGame().getGameCamera().getyOffset() / 16);
        int yEnd = (int) Math.min(height, (handler.getGame().getGameCamera().getyOffset() + handler.getGame().getHeight()) / 16 + 1);

        for(int y = yStart; y < yEnd; y++) {
            for(int x = xStart; x < xEnd; x++) {
                getTile(x, y).Draw(g, (int)(x * 16 - handler.getGame().getGameCamera().getxOffset()),
                        (int)(y * 16 - handler.getGame().getGameCamera().getyOffset()));
            }
        }

        entityManager.Draw(g);
    }

    public Tile getTile(int x, int y) { return null; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height;}
    public void setHeight(int height) { this.height = height; }
}
