package PaooGame.Graphics;

import PaooGame.Entities.Creatures.Dante;
import PaooGame.Entities.EntityManager;
import PaooGame.Game;
import PaooGame.Tiles.Tile;

import java.awt.*;

public class World {
    private Game game;
    private int width, height; // În tile-uri
    private int spawnX, spawnY;
    private int[][] tiles;
    private EntityManager entityManager;

    public World(Game game, String path) {
        this.game = game;
        entityManager = new EntityManager(game, new Dante(game, 0, 0));
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
        int xStart = (int) Math.max(0, game.getGameCamera().getxOffset() / 16);
        int xEnd = (int) Math.min(width, (game.getGameCamera().getxOffset() + game.getWidth()) / 16 + 1);
        int yStart = (int) Math.max(0, game.getGameCamera().getyOffset() / 16);
        int yEnd = (int) Math.min(height, (game.getGameCamera().getyOffset() + game.getHeight()) / 16 + 1);

        for(int y = yStart; y < yEnd; y++) {
            for(int x = xStart; x < xEnd; x++) {
                getTile(x, y).Draw(g, (int)(x * 16 - game.getGameCamera().getxOffset()),
                        (int)(y * 16 - game.getGameCamera().getyOffset()));
            }
        }

        entityManager.Draw(g);
    }

    public Tile getTile(int x, int y) {
    }
}
