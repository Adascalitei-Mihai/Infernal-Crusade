package PaooGame.Graphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import static PaooGame.World.Tile.NO_TILES;

/**
 * Clasa care incarca elementele vizuale
 * Implementeaza Runnable pentru folosirea thread-urilor
 * @see java.awt.image.BufferedImage
 */
public class VisualAssets implements Runnable {
    /**
     * Tuple intern care usureaza crearea animatiilor
     * @param label Denumirea animatiei
     * @param offset Micsorarea pe Oy a cadrelor din imaginea originala
     * @param row Randul din imaginea originala
     * @param col Coloana din imaginea originala
     * @param count Numarul de cadre
     */
    public record AnimationData(String label, int offset, int row, int col, int count) {
        public AnimationData(String label, int count) { this(label, 0, 0, 0, count); }
        public AnimationData(String label, int offset, int count) { this(label, offset, 0, 0, count); }
        public AnimationData(String label, int row, int col, int count) { this(label, 0, row, col, count); }
    }

    public static BufferedImage startMenuBg;
    public static BufferedImage mainMenuBg;
    public static BufferedImage controlsMenuBg;

    public static BufferedImage[] uiHealth;
    public static BufferedImage[] uiRanks;
    public static BufferedImage[] uiCd;

    public static HashMap<String, BufferedImage[]> Dante;
    public static HashMap<String, BufferedImage[]> Skeleton;
    public static HashMap<String, BufferedImage[]> Cyclops;
    public static HashMap<String, BufferedImage[]> Hunter;
    public static HashMap<String, BufferedImage[]> Ghoul;
    public static HashMap<String, BufferedImage[]> Necromancer;
    public static HashMap<String, BufferedImage[]> Boss;

    public static BufferedImage[] Arrow;
    public static BufferedImage[] Screech;
    public static BufferedImage[] SoulSpear;
    public static BufferedImage[] SummonCircle;
    public static BufferedImage[] SoulPillar;

    public static BufferedImage[] bg1, bg2, bg3;
    public static BufferedImage[] worldTiles1 = new BufferedImage[NO_TILES] , worldTiles2 = new BufferedImage[NO_TILES], worldTiles3 = new BufferedImage[NO_TILES];
    public static BufferedImage[] chkp;
    public static BufferedImage[] spear_opening, spear_closing;
    public static BufferedImage[] ice;
    public static BufferedImage[] fire;

    public static int loadPercent;
    public static int TOTAL_LOAD = 34;

    /**
     * Metoda de incarcare a tuturor elementelor vizuale, actualizeaza procentul de elemente incarcat <br>
     * Apeleaza {@link #InitMenus()}, {@link #InitDante()}, {@link #InitEnemies()}, {@link #InitUI()}, {@link #InitEnvironment()}, {@link #InitEffects()}, {@link #InitL1()}, {@link #InitL2()}, {@link #InitL3()}
     */
    public void run() {
        InitMenus();
        ++loadPercent;
        InitDante();
        ++loadPercent;
        InitEnemies(); // +7 loadPercent
        InitUI();
        ++loadPercent;
        InitEnvironment();
        ++loadPercent;
        InitEffects();
        ++loadPercent;

        InitL1(); // +5 loadPercent
        InitL2(); // +17 loadPercent
        InitL3();
    }

    /**
     * Metoda care incarca animatiile si atacurile specifice inamicilor
     */
    private static void InitEnemies() {
        AnimationData[] skeletonFrames = {new AnimationData("Idle", 8), new AnimationData("Walk", 10),
                new AnimationData("Attack", 10), new AnimationData("Die", 13), new AnimationData("Hurt", 5)};
        Skeleton = loadAnim("L1-Skeleton", skeletonFrames);
        ++loadPercent;

        AnimationData[] cyclopsFrames = {new AnimationData("Idle", 0, 0, 8), new AnimationData("Walk", 1, 0, 8),
                new AnimationData("Attack", 2, 0, 8), new AnimationData("Die", 4, 0, 7), new AnimationData("Hurt", 3, 1, 3)};
        Cyclops = loadAnim("L1-Cyclops/SpriteSheet", cyclopsFrames, 140, 93);
        ++loadPercent;

        AnimationData[] hunterFrames = {new AnimationData("Idle", 34, 10), new AnimationData("Walk", 34, 8),
                new AnimationData("Attack", 34, 6), new AnimationData("Die", 34, 10), new AnimationData("Hurt", 34, 3)};
        Hunter = loadAnim("L2-Hunter", hunterFrames);
        BufferedImage arrowImg = ImageLoader.LoadImage("/textures/Entities/L2-Hunter/Arrow.png");
        Arrow = new SpriteSheet(arrowImg, arrowImg.getWidth() / 2, arrowImg.getHeight()).makeSelection(0, 0, 2);
        ++loadPercent;

        AnimationData[] ghoulFrames = {new AnimationData("Idle", 50, 8), new AnimationData("Fly", 50, 8),
                new AnimationData("Attack", 50, 8), new AnimationData("Die", 4), new AnimationData("Hurt", 50, 4)};
        Ghoul = loadAnim("L2-Ghoul", ghoulFrames);
        BufferedImage screechImg = ImageLoader.LoadImage("/textures/Entities/L2-Ghoul/Screech.png");
        Screech = new SpriteSheet(screechImg, screechImg.getWidth() / 8, screechImg.getHeight()).makeSelection(0, 0, 3);
        ++loadPercent;

        AnimationData[] necromancerFrames = {new AnimationData("Idle", 0, 0, 8), new AnimationData("Walk", 1, 0, 8),
                new AnimationData("Attack", 2, 0, 12), new AnimationData("Die", 6, 0, 8),
                new AnimationData("Hurt", 5, 0, 5), new AnimationData("Lifesteal", 4, 0, 12)};
        Necromancer = loadAnim("L3-Necromancer/SpriteSheet", necromancerFrames, 160, 128);
        BufferedImage summonCircleImg = ImageLoader.LoadImage("/textures/Entities/L3-Necromancer/SummonCircle.png");
        SummonCircle = new SpriteSheet(summonCircleImg, summonCircleImg.getWidth() / 5, summonCircleImg.getHeight() - 60).makeSelection(0, 0, 5);
        ++loadPercent;

        AnimationData[] bossFrames = {new AnimationData("Idle", 4, 0, 6), new AnimationData("Walk", 5, 0, 12),
                new AnimationData("Melee", 6, 0, 15), new AnimationData("Projectile", 8, 0, 20),
                new AnimationData("Spell", 7, 0, 16), new AnimationData("Summon", 9, 0, 6),
                new AnimationData("Hurt", 10, 0, 5), new AnimationData("Lifesteal", 11, 0, 10),
                new AnimationData("Death1", 0, 0, 6), new AnimationData("MoveToPhase2", 1, 0, 8),
                new AnimationData("Phase2Start", 3, 0, 32), new AnimationData("Death2", 11, 0, 16)};
        Boss = loadAnim("L3-Boss/SpriteSheet", bossFrames, 288, 160);
        ++loadPercent;
        BufferedImage bossProjectilesImg = ImageLoader.LoadImage("/textures/Entities/L3-Boss/Projectiles.png");
        SoulSpear = new SpriteSheet(bossProjectilesImg, 32, 32).makeSelection(0, 0, 3);
        SoulPillar = new SpriteSheet(bossProjectilesImg, 32, 32).makeSelection(1, 0, 10);
        ++loadPercent;
    }

    /**
     * Metoda care incarca animatiile lui Dante
     */
    private static void InitDante() {
        AnimationData[] frames = {new AnimationData("Idle", 16, 6), new AnimationData("Run", 16, 8),
                new AnimationData("Jump", 16, 2), new AnimationData("Fall", 16, 2), new AnimationData("Hit", 16, 3),
                new AnimationData("Death", 16, 9), new AnimationData("Attack1", 16, 4), new AnimationData("Attack2", 16, 4),
                new AnimationData("Ice_Morph", 3), new AnimationData("Eden_Shield", 3), new AnimationData("Fiery_Judgement", 20, 3)};
        Dante = loadAnim("Dante", frames);
    }

    /**
     * Metoda care incarca animatiile unei entitati, din imagini diferite, folosind {@link ImageLoader} impreuna cu {@link SpriteSheet}
     * @param folder Folderul unde se gasesc imaginile
     * @param frames Metoda de a taia cadrele
     * @return Vectorii de cadre pentru fiecare animatie
     */
    private static HashMap<String, BufferedImage[]> loadAnim(String folder, AnimationData[] frames) {
        HashMap<String, BufferedImage[]> animations = new HashMap<>();
        for (AnimationData anim : frames) {
            try {
                BufferedImage img = ImageLoader.LoadImage("/textures/Entities/" + folder + "/" + anim.label + ".png");

                animations.put(anim.label, new SpriteSheet(img, img.getWidth() / anim.count, img.getHeight() - anim.offset)
                        .makeSelection(anim.row, anim.col, anim.count));
            } catch (Exception e) {
                System.err.println("[EROARE]: Incarcarea imaginilor din " + folder + ": " + e.getMessage());
            }
        }

        return animations;
    }

    /**
     * Metoda care incarca animatiile unei entitati, din aceeasi imagine, folosind {@link ImageLoader} impreuna cu {@link SpriteSheet}
     * @param fileName Imaginea totala
     * @param frames Metoda de a taia cadrele
     * @param width Latimea unui cadru
     * @param height Inaltimea unui cadru
     * @return Vectorii de cadre pentru fiecare animatie
     */
    private static HashMap<String, BufferedImage[]> loadAnim(String fileName, AnimationData[] frames, int width, int height) {
        HashMap<String, BufferedImage[]> animations = new HashMap<>();
        for (AnimationData anim : frames) {
            try {
                BufferedImage img = ImageLoader.LoadImage("/textures/Entities/" + fileName + ".png");

                animations.put(anim.label, new SpriteSheet(img, width, height).makeSelection(anim.row, anim.col, anim.count));
            } catch (Exception e) {
                System.err.println("[EROARE]: Incarcarea imaginii: " + fileName + ": " + e.getMessage());
            }
        }

        return animations;
    }

    /**
     * Metoda care incarca nivelul 1, fundalele in {@link #bg1} si dalele in {@link #worldTiles1} (folosind {@link TMXParser#parseTMXTilesets(String)} impreuna cu {@link #fillTiles(BufferedImage[], SpriteSheet, int, int, int)})
     */
    private static void InitL1() {
        try {
            bg1 = new BufferedImage[]{
                    ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer5.png"),
                    ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer4.png"),
                    ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer2.png"),
                    ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer1.png"),
                    ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer3.png"),
                    ImageLoader.LoadImage("/textures/Maps/L1/fundal_arena.png")
            };
            ++loadPercent;
        }
        catch (Exception e) {
            System.err.println("[EROARE] Incarcarea fundalelor nivelului 1: " + e.getMessage());
        }

        try {
            List<TMXParser.TilesetData> tilesets = TMXParser.parseTMXTilesets("/textures/Maps/L1/L1.tmx");

            for (TMXParser.TilesetData ts : tilesets) {
                SpriteSheet sheet = new SpriteSheet(ImageLoader.LoadImage(ts.imagePath), ts.tileWidth, ts.tileHeight);
                fillTiles(worldTiles1, sheet, ts.firstGid, ts.columns, ts.tileCount);
                ++loadPercent;
            }

        } catch (Exception e) {
            System.err.println("[EROARE] Incarcarea automata a dalelor nivelului 1 din TMX: " + e.getMessage());
        }
    }

    /**
     * Metoda care incarca nivelul 2, fundalele in {@link #bg2} si dalele in {@link #worldTiles2} (folosind {@link TMXParser#parseTMXTilesets(String)} impreuna cu {@link #fillTiles(BufferedImage[], SpriteSheet, int, int, int)})
     */
    public static void InitL2() {
        try {
            bg2 = new BufferedImage[]{
                    ImageLoader.LoadImage("/textures/Maps/L2/cer.png"),
                    ImageLoader.LoadImage("/textures/Maps/L2/ceata.png"),
                    ImageLoader.LoadImage("/textures/Maps/L2/arena1.png"),
                    ImageLoader.LoadImage("/textures/Maps/L2/arena2.png"),
                    ImageLoader.LoadImage("/textures/Maps/L2/arena3.png")
            };
            ++loadPercent;
        } catch (Exception e) {
            System.err.println("[EROARE] Incarcarea fundalelor nivelului 2: " + e.getMessage());
        }

        try {
            List<TMXParser.TilesetData> tilesets = TMXParser.parseTMXTilesets("/textures/Maps/L2/L2.tmx");

            for (TMXParser.TilesetData ts : tilesets) {
                SpriteSheet sheet = new SpriteSheet(ImageLoader.LoadImage(ts.imagePath), ts.tileWidth, ts.tileHeight);
                fillTiles(worldTiles2, sheet, ts.firstGid, ts.columns, ts.tileCount);
                ++loadPercent;
            }

        } catch (Exception e) {
            System.err.println("[EROARE] Incarcarea automata a dalelor nivelului 2 din TMX: " + e.getMessage());
        }
    }

    /**
     * Metoda care incarca nivelul 3, fundalele in {@link #bg3} si dalele in {@link #worldTiles3}
     * (folosind {@link TMXParser#parseTMXTilesets(String)} impreuna cu {@link #fillTiles})
     */
    public static void InitL3() {
        try {
            bg3 = new BufferedImage[]{
                    ImageLoader.LoadImage("/textures/Maps/L3/background Arena.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/redBg.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/redBg.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/redBg.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/arena trans.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/Background layerARENA.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/back layerARENA.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/middle layerARENA.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/front layerARENA.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/Background layer.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/front layer rev.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/back layer.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/middle layer.png"),
                    ImageLoader.LoadImage("/textures/Maps/L3/front layer.png"),
            };
            ++loadPercent;
        } catch (Exception e) {
            System.err.println("[EROARE] Incarcarea fundalelor nivelului 3: " + e.getMessage());
        }

        try {
            List<TMXParser.TilesetData> tilesets = TMXParser.parseTMXTilesets("/textures/Maps/L3/L3.tmx");

            for (TMXParser.TilesetData ts : tilesets) {
                BufferedImage sheetImg = ImageLoader.LoadImage(ts.imagePath);
                if (sheetImg != null) {
                    SpriteSheet sheet = new SpriteSheet(sheetImg, ts.tileWidth, ts.tileHeight);
                    fillTiles(worldTiles3, sheet, ts.firstGid, ts.columns, ts.tileCount);
                }
                ++loadPercent;
            }

        } catch (Exception e) {
            System.err.println("[EROARE] Incarcarea automata a dalelor nivelului 3 din TMX: " + e.getMessage());
        }
    }

    /**
     * Metoda care incarca portile si checkpoint-urile
     */
    private static void InitEnvironment() {
        try {
            SpriteSheet spearSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/spear.png"), 16, 64);
            spear_closing = new BufferedImage[]{
                    spearSheet.crop(0, 0), spearSheet.crop(1, 0),
                    spearSheet.crop(2, 0), spearSheet.crop(5, 0),
                    spearSheet.crop(6, 0)
            };
            spear_opening = new BufferedImage[]{
                    spearSheet.crop(6, 0), spearSheet.crop(5, 0),
                    spearSheet.crop(2, 0), spearSheet.crop(1, 0),
                    spearSheet.crop(0, 0)
            };
        } catch (Exception e) {
            System.err.println("[EROARE]: Incarcarea portilor: " + e.getMessage());
        }

        try {
            SpriteSheet checkSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/checkpoint.png"), 32, 32);
            chkp = new BufferedImage[]{
                    checkSheet.crop(0,0),
                    checkSheet.crop(0, 1),
                    checkSheet.crop(1, 0),
                    checkSheet.crop(1, 1)
            };
        } catch (Exception e) {
            System.err.println("[EROARE]: Incarcarea checkpoint-urilor: " + e.getMessage());
        }
    }

    /**
     * Metoda care incarca elementele de UI
     */
    private static void InitUI() {
        try {
            BufferedImage uiSheetImg = ImageLoader.LoadImage("/textures/UI/Overlay.png");
            uiHealth = new BufferedImage[7];
            for(int i = 0; i < 7; i++) {
                uiHealth[i] = uiSheetImg.getSubimage(550 + (int) (i * 45.5f), 60, 40, 16);
            }

            uiRanks = new BufferedImage[5];
            uiRanks[4] = uiSheetImg.getSubimage(76, 245, 77, 40);
            for(int i = 0; i < 4; i++) {
                uiRanks[3 - i] = uiSheetImg.getSubimage(75 + (i * 46), 204, 46, 32);
            }

            uiCd = new BufferedImage[5];
            for(int i = 0; i < 5; i++) {
                uiCd[i] = uiSheetImg.getSubimage(272 + (i * 46), 152, 46, 48);
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("[EROARE] Incarcarea UI-ului: " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("[EROARE] Decuparea UI-ului: " + e.getMessage());
        }
    }

    /**
     * Metoda care umple dalele nivelului
     * @param worldTiles Nivelul ales
     * @param sheet Sursa dalelor
     * @param startID Start ID-ul generat de TiledMapEditor
     * @param columns Numarul de coloane
     * @param totalCount Numarul total de dale
     */
    private static void fillTiles(BufferedImage[] worldTiles, SpriteSheet sheet, int startID, int columns, int totalCount) {
        int id = startID;
        for (int i = 0; i < totalCount; i++) {
            int x = i % columns;
            int y = i / columns;
            worldTiles[id] = sheet.crop(x, y);
            id++;
        }
    }

    /**
     * Metoda care incarca efectele de gheata si de foc
     */
    private static void InitEffects() {
        ice = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/ice.png"), 32, 32).makeSelection(0, 2, 6);
        fire = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/fire.png"), 24, 32).makeSelection(0, 0, 8);
    }

    /**
     * Metoda care incarca fundalele meniurilor
     */
    private static void InitMenus() {
        startMenuBg = ImageLoader.LoadImage("/textures/UI/Start-Menu.png");
        mainMenuBg = ImageLoader.LoadImage("/textures/UI/Main-Menu.png");
        controlsMenuBg = ImageLoader.LoadImage("/textures/UI/Controls-Menu.png");
    }
}