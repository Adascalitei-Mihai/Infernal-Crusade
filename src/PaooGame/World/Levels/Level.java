package PaooGame.World.Levels;

import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;
import PaooGame.Graphics.TMXParser;
import PaooGame.Handler;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Creatures.Enemies.GroundEnemies.Boss;
import PaooGame.World.Entities.EntityManager;
import PaooGame.World.Factories.EntityFactory;
import PaooGame.World.Tile;
import javax.sound.sampled.Clip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;
import static PaooGame.World.Entities.Creatures.Dante.FIRE_TIMER;
import static PaooGame.World.Entities.Creatures.Dante.ICE_TIMER;
import static PaooGame.World.Tile.TILE_SIZE;

public abstract class Level {
    public static final int EXPOSED_NONE = 0;
    public static final int EXPOSED_EAST = 1;
    public static final int EXPOSED_NORTH = 1 << 1;
    public static final int EXPOSED_WEST = 1 << 2;
    public static final int EXPOSED_SOUTH = 1 << 3;

    protected final Handler handler;
    protected final EntityFactory factory;
    protected final Clip bgSong;

    protected int width, height;
    protected Map<String, int[][]> mapLayers;
    protected String[] layerRenderOrder;
    protected String[] solidLayerNames;
    protected int[][] tiles;

    protected int[][] fireTimers;
    protected int[][] iceTimers;
    protected int[][] exposureMask;

    protected EntityManager entityManager;
    protected Animation fireAnim;
    protected Animation iceAnim;
    protected Arena arena;

    private boolean pendingRespawn;

    public Level(Handler handler, EntityFactory factory, int width, int height, String path, Clip bgSong, String[] layerRenderOrder, String[] solidLayerNames) {
        this.handler = handler;
        this.factory = factory;
        this.width = width;
        this.height = height;
        this.layerRenderOrder = layerRenderOrder;
        this.solidLayerNames = solidLayerNames;

        handler.setLevel(this);
        loadLevel(path);

        calculateExposureMasks();
        setAnimations();

        this.bgSong = bgSong;
        entityManager = new EntityManager(new Dante(handler, 0, 0));
    }

    public abstract void spawn();
    public abstract void setEnemies();

    public void loadLevel(String path) {
        mapLayers = TMXParser.parseTMXLayers(path, Arrays.asList(layerRenderOrder));
        fireTimers = new int[width][height];
        iceTimers = new int[width][height];
    }

    public void Update() {
        entityManager.Update();
        fireAnim.Update();
        iceAnim.Update();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (fireTimers[x][y] > 0) fireTimers[x][y]--;
                if (iceTimers[x][y] > 0) iceTimers[x][y]--;
            }
        }

        if (pendingRespawn) {
            spawn();
            pendingRespawn = false;
        }
    }

    protected boolean isSolidLayer(String layerName) {
        if (solidLayerNames == null) return false;
        for (String solid : solidLayerNames) {
            if (solid.equals(layerName)) {
                return true;
            }
        }
        return false;
    }

    public void Draw(Graphics g) {
        drawBackground(g);

        int xStart = (int) Math.max(0, handler.getxOffset() / TILE_SIZE);
        int xEnd = (int) Math.min(width, (handler.getxOffset() + handler.getWidth()) / TILE_SIZE + 1);
        int yStart = (int) Math.max(0, handler.getyOffset() / TILE_SIZE);
        int yEnd = (int) Math.min(height, (handler.getyOffset() + handler.getHeight()) / TILE_SIZE + 1);

        for (String layerName : layerRenderOrder) {
            int[][] currentLayer = mapLayers.get(layerName);
            if (currentLayer == null) continue;

            for (int y = yStart; y < yEnd; y++) {
                for (int x = xStart; x < xEnd; x++) {
                    if (x < 0 || y < 0 || x >= width || y >= height) continue;

                    int id = currentLayer[x][y];

                    if (id <= 0 || id >= Tile.TILES.length || Tile.TILES[id] == null) continue;

                    Tile t = Tile.TILES[id];
                    int screenX = (int) (x * TILE_SIZE - handler.getxOffset());
                    int screenY = (int) (y * TILE_SIZE - handler.getyOffset());

                    t.Draw(g, screenX, screenY);

                    if (handler.getDebugMode() && isSolidLayer(layerName)) {
                        if (t.isSolid()) {
                            g.setColor(new Color(0, 255, 0, 100));
                        } else {
                            g.setColor(new Color(255, 255, 0, 100));
                        }

                        g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        int xStartFx = Math.max(0, xStart - 1);
        int xEndFx = Math.min(width, xEnd + 1);
        int yStartFx = Math.max(0, yStart - 1);
        int yEndFx = Math.min(height, yEnd + 1);

        for (int y = yStartFx; y < yEndFx; y++) {
            for (int x = xStartFx; x < xEndFx; x++) {
                if (isFire(x, y) || isIce(x, y)) {
                    int mask = exposureMask[x][y];
                    int screenX = (int) (x * TILE_SIZE - handler.getxOffset());
                    int screenY = (int) (y * TILE_SIZE - handler.getyOffset());

                    BufferedImage fxImg = isFire(x, y) ? fireAnim.getCurrentFrame() : iceAnim.getCurrentFrame();
                    int offset = TILE_SIZE / 2;

                    if (mask != 0 && handler.getDebugMode()) {
                        g.setColor(new Color(255, 165, 0, 100));
                        g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    }

                    if ((mask & EXPOSED_NORTH) != 0) drawRotatedEffect(g, fxImg, screenX, screenY, 0, offset);
                    if ((mask & EXPOSED_EAST) != 0) drawRotatedEffect(g, fxImg, screenX, screenY, Math.PI / 2, offset);
                    if ((mask & EXPOSED_WEST) != 0) drawRotatedEffect(g, fxImg, screenX, screenY, Math.PI * 1.5, offset);
                }
            }
        }

        if (handler.getDebugMode()) {
            g.setColor(Color.RED);
            g.setFont(new Font("MONOSPACED", Font.BOLD, 36));
            String fpsText = "FPS: " + handler.getGame().getCurrentFPS();
            g.drawString(fpsText, handler.getWidth() - 200, 50);
        }

        entityManager.Draw(g);
    }

    private void drawRotatedEffect(Graphics g, BufferedImage img, int tileX, int tileY, double angleInRadians, int offset) {
        if (img == null) return;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(tileX + TILE_SIZE / 2.0, tileY + TILE_SIZE / 2.0);
        g2d.rotate(angleInRadians);

        int drawW = TILE_SIZE * 2;
        int drawH = TILE_SIZE * 2;
        int drawX = -TILE_SIZE;
        int drawY = (-TILE_SIZE / 2) + offset - drawH;

        g2d.drawImage(img, drawX, drawY, drawW, drawH, null);
        g2d.dispose();
    }

    protected void calculateExposureMasks() {
        exposureMask = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile currentTile = getTile(x, y);
                if (!currentTile.isSolid()) {
                    exposureMask[x][y] = EXPOSED_NONE;
                    continue;
                }

                int mask = EXPOSED_NONE;
                if (!getTile(x, y - 1).isSolid()) mask |= EXPOSED_NORTH;
                if (!getTile(x + 1, y).isSolid()) mask |= EXPOSED_EAST;
                if (!getTile(x, y + 1).isSolid()) mask |= EXPOSED_SOUTH;
                if (!getTile(x - 1, y).isSolid()) mask |= EXPOSED_WEST;

                exposureMask[x][y] = mask;
            }
        }
    }

    public void requestRespawn() {
        pendingRespawn = true;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y >= height) return Tile.TILES[1];
        if (y < 0) return Tile.TILES[0];

        for (String layerName : solidLayerNames) {
            int[][] currentLayer = mapLayers.get(layerName);
            if (currentLayer != null) {
                int id = currentLayer[x][y];

                if (id > 0 && id < Tile.TILES.length && Tile.TILES[id] != null) {
                    if (Tile.TILES[id].isSolid()) {
                        return Tile.TILES[id];
                    }
                }
            }
        }

        return Tile.TILES[0];
    }

    public boolean isFire(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < height) && fireTimers[x][y] > 0;
    }

    public boolean isIce(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < height) && iceTimers[x][y] > 0;
    }

    public void setFire(int x, int y, int time) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (exposureMask[x][y] > EXPOSED_NONE && !isIce(x, y)) fireTimers[x][y] = time;
        }
    }

    public void setIce(int x, int y, int time) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (exposureMask[x][y] > EXPOSED_NONE && !isFire(x, y)) iceTimers[x][y] = time;
        }
    }

    public void setAnimations() {
        iceAnim = new Animation(ICE_TIMER / 60, VisualAssets.ice);
        fireAnim = new Animation(FIRE_TIMER / 60, VisualAssets.fire);
    }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public EntityFactory getFactory() { return factory; }
    public EntityManager getEntityManager() { return entityManager; }
    public void drawBackground(Graphics g) {}
    public boolean inArena() { return arena != null && arena.isRunning(); }
    public int getCurrWave() { return (arena == null) ? 0 : arena.getCurrWave(); }
    public Boss getBoss() { return arena.getBoss(); }
}