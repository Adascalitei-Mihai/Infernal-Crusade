package PaooGame.World.Levels;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.TMXParser;
import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.EntityManager;
import PaooGame.World.Factories.EntityFactory;
import PaooGame.World.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

import static PaooGame.World.Tile.TILE_SIZE;

public abstract class Level {
    protected final Handler handler;
    protected final EntityFactory factory;

    protected int width, height;
    protected int[][] tiles;
    protected int[][] fireTimers;
    protected int[][] iceTimers;
    protected EntityManager entityManager;
    private boolean pendingRespawn;

    public static final int EXPOSED_NONE  = 0;
    public static final int EXPOSED_EAST = 1;
    public static final int EXPOSED_NORTH  = 1 << 1;
    public static final int EXPOSED_WEST = 1 << 2;
    public static final int EXPOSED_SOUTH  = 1 << 3;
    protected int[][] exposureMask;

    // Contor folosit pentru a animația de foc
    private int effectTick = 0;

    public Level(Handler handler, EntityFactory factory, int width, int height, String path) {
        this.handler = handler;
        this.factory = factory;

        this.width = width;
        this.height = height;

        handler.setLevel(this);
        loadLevel(path);

        calculateExposureMasks();

        entityManager = new EntityManager(new Dante(handler, 0, 0));
    }

    public abstract void spawn();
    public abstract void setEnemies();

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

        // Creștem contorul pentru animația efectelor
        effectTick++;

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

        if(pendingRespawn) {
            spawn();
            pendingRespawn = false;
        }
    }

    public void Draw(Graphics g) {
        drawBackground(g);

        int xStart = (int) Math.max(0, handler.getxOffset() / TILE_SIZE);
        int xEnd = (int) Math.min(width, (handler.getxOffset() + handler.getWidth()) / TILE_SIZE + 1);
        int yStart = (int) Math.max(0, handler.getyOffset() / TILE_SIZE);
        int yEnd = (int) Math.min(height, (handler.getyOffset() + handler.getHeight()) / TILE_SIZE + 1);

        // Pasul 1: Desenăm Tile-urile hărții
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
                        }
                        else {
                            g.setColor(new Color(255, 255, 0, 100));
                        }

                        g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);

                        g.setColor(Color.BLACK);
                        g.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // ... (restul funcției Draw până la Pasul 2)

        int xStartFx = Math.max(0, xStart - 1);
        int xEndFx = Math.min(width, xEnd + 1);
        int yStartFx = Math.max(0, yStart - 1);
        int yEndFx = Math.min(height, yEnd + 1);

        for(int y = yStartFx; y < yEndFx; y++) {
            for(int x = xStartFx; x < xEndFx; x++) {
                if (isFire(x, y) || isIce(x, y)) {
                    int mask = exposureMask[x][y];
                    // Acum dăm mai departe fix coordonatele blocului solid care arde/îngheață
                    int screenX = (int)(x * TILE_SIZE - handler.getxOffset());
                    int screenY = (int)(y * TILE_SIZE - handler.getyOffset());

                    BufferedImage fxImg;
                    if (isFire(x, y)) {
                        int frameIndex = (effectTick / 5) % 8;
                        fxImg = Assets.fire[frameIndex];
                    } else {
                        fxImg = Assets.ice;
                    }

                    int offset = TILE_SIZE / 4; // Offset-ul tău calculat (40 * 2 / 16) pentru a intra în tile

                    // Transmitem direct screenX și screenY (marginea stânga-sus a tile-ului solid)
                    if ((mask & EXPOSED_NORTH) != 0) {
                        drawRotatedEffect(g, fxImg, screenX, screenY, 0, offset);
                    }
                    if ((mask & EXPOSED_EAST) != 0) {
                        drawRotatedEffect(g, fxImg, screenX, screenY, Math.PI / 2, offset);
                    }
                    if ((mask & EXPOSED_SOUTH) != 0) {
                        drawRotatedEffect(g, fxImg, screenX, screenY, Math.PI, offset);
                    }
                    if ((mask & EXPOSED_WEST) != 0) {
                        drawRotatedEffect(g, fxImg, screenX, screenY, Math.PI * 1.5, offset);
                    }
                }
            }
        }

        // Desenăm UI-ul de Debug pentru FPS la final
        if (handler.getDebugMode()) {
            g.setColor(Color.RED);
            g.setFont(new Font("MONOSPACED", Font.BOLD, 36));
            String fpsText = "FPS: " + handler.getGame().getCurrentFPS();
            g.drawString(fpsText, handler.getWidth() - 200, 50);
        }

        // Pasul 3: Desenăm Entitățile (Dante, inamici etc) PESTE mediu
        entityManager.Draw(g);
    }

    /**
     * Rotește și desenează un efect dublu pe înălțime lipit de o margine a tile-ului curent.
     */
    private void drawRotatedEffect(Graphics g, BufferedImage img, int tileX, int tileY, double angleInRadians, int offset) {
        if (img == null) return;

        Graphics2D g2d = (Graphics2D) g.create();

        // 1. Mutăm pivotul de rotație EXACT în centrul blocului solid care este afectat.
        g2d.translate(tileX + TILE_SIZE / 2.0, tileY + TILE_SIZE / 2.0);

        // 2. Rotim mediul. Dacă e Est (PI/2), tot ce desenăm în SUS va ajunge automat în DREAPTA.
        g2d.rotate(angleInRadians);

        // 3. Setăm dimensiunile (înălțime dublă)
        int drawW = TILE_SIZE;
        int drawH = TILE_SIZE * 2;

        // 4. Calculăm coordonatele relativ la pivotul care e în centrul blocului:
        // X-ul e centrat (deci jumătate în stânga)
        int drawX = -TILE_SIZE / 2;

        // Y-ul: Vrem ca baza flăcării să fie pe marginea de sus a blocului (-TILE_SIZE / 2).
        // Adăugăm offset-ul ca să o scufundăm puțin în bloc.
        // Pentru că imaginea se desenează din colțul ei de sus-stânga, trebuie să scădem înălțimea completă (drawH).
        int drawY = (-TILE_SIZE / 2) + offset - drawH;

        // Desenăm efectul
        g2d.drawImage(img, drawX, drawY, drawW, drawH, null);

        // Curățăm
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
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        return fireTimers[x][y] > 0;
    }

    public boolean isIce(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        return iceTimers[x][y] > 0;
    }

    public void setFire(int x, int y, int time) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return ;
        }

        if (exposureMask[x][y] != EXPOSED_NONE && !isIce(x, y)) {
            fireTimers[x][y] = time;
        }
    }

    public void setIce(int x, int y, int time) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return ;
        }

        if (exposureMask[x][y] > EXPOSED_NONE && !isFire(x, y)) {
            iceTimers[x][y] = time;
        }
    }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height;}
    public void setHeight(int height) { this.height = height; }

    public EntityFactory getFactory() {
        return factory;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    public void drawBackground(Graphics g) {}
}