package PaooGame.World.Levels;

import PaooGame.Graphics.TMXParser;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.EntityManager;
import PaooGame.World.Tiles.Tile;

import java.awt.*;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public abstract class Level {
    protected final Handler handler;
    protected int width, height;
    protected int[][] tiles;
    protected int[][] fireTimers;
    protected int[][] iceTimers;
    protected EntityManager entityManager;

    public Level(Handler handler, int width, int height, int spawnX, int spawnY, String path) {
        this.handler = handler;
        this.width = width;
        this.height = height;

        handler.setLevel(this);

        entityManager = new EntityManager(new Dante(handler, 0, 0));
        loadLevel(path);

        entityManager.getDante().setX(spawnX);
        entityManager.getDante().setY(spawnY);
    }

    public void loadLevel(String path) {
        String content = TMXParser.loadFileAsString(path);

        String csvData = content.substring(content.indexOf("<data encoding=\"csv\">") + 21,
                content.indexOf("</data>"));

        this.tiles = TMXParser.decodeCSV(csvData, width, height);
        fireTimers = new int[tiles.length][tiles[0].length];
        iceTimers = new int[tiles.length][tiles[0].length];
    }

    public void Update() {
        entityManager.Update();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (fireTimers[x][y] > 0) {
                    fireTimers[x][y] -= 1;
                }

                if (iceTimers[x][y] > 0) {
                    iceTimers[x][y] -= 1;
                }
            }
        }
    }

    public void Draw(Graphics g) {
        drawBackground(g);

        int xStart = (int) Math.max(0, handler.getxOffset() / TILE_SIZE);
        int xEnd = (int) Math.min(width, (handler.getxOffset() + handler.getWidth()) / TILE_SIZE + 1);
        int yStart = (int) Math.max(0, handler.getyOffset() / TILE_SIZE);
        int yEnd = (int) Math.min(height, (handler.getyOffset() + handler.getHeight()) / TILE_SIZE + 1);

        for(int y = yStart; y < yEnd; y++) {
            for(int x = xStart; x < xEnd; x++) {
                Tile t = getTile(x, y);
                if(t != null) {

                    int screenX = (int)(x * TILE_SIZE - handler.getxOffset());
                    int screenY = (int)(y * TILE_SIZE - handler.getyOffset());
                    t.Draw(g, screenX, screenY);

                    if(handler.getDebugMode()) {
                        if(t.isSolid()) {
                            g.setColor(new Color(0, 255, 0, 100));
                        } else {
                            g.setColor(new Color(255, 255, 0, 100));
                        }

                        g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);

                        g.setColor(Color.BLACK);
                        g.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);

                        if (isFire(x, y) || isIce(x, y)) {
                            g.setColor(new Color(255, 100, 0, 150));
                            g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                        }

                        g.setColor(Color.RED);
                        g.setFont(new Font("MONOSPACED", Font.BOLD, 36));
                        String fpsText = "FPS: " + handler.getGame().getCurrentFPS();
                        g.drawString(fpsText, handler.getWidth() - 200, 50);


                    }
                }
            }
        }

        entityManager.Draw(g);
    }

    public Tile getTile(int x, int y) {
        if(x < 0 || x >= width || y >= height)
            return Tile.tiles[1];

        if(y < 0)
            return Tile.tiles[0];

        int id = tiles[x][y];

        if(id <= 0 || id >= Tile.tiles.length || Tile.tiles[id] == null) {
            return Tile.tiles[0];
        }

        return Tile.tiles[id];
    }
    public boolean isFire(int x, int y) {
        return fireTimers[x][y] > 0;
    }
    public boolean isIce(int x, int y) {
        return iceTimers[x][y] > 0;
    }
    public void setFire(int x, int y, int time) {
        fireTimers[x][y] = time;
    }
    public void setIce(int x, int y, int time) {
        iceTimers[x][y] = time;
    }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height;}
    public void setHeight(int height) { this.height = height; }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    public void drawBackground(Graphics g) {}
}
