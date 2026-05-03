package PaooGame.Graphics;

import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Creatures.Enemies.FlyingEnemies.Ghoul;
import PaooGame.World.Entities.Projectiles.Screech;
import PaooGame.World.Entities.Projectiles.SoulSpear;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferDouble;
import java.util.HashMap;

import static PaooGame.World.Tile.NO_TILES;

/**
 * Clasa care incarcă fiecare element grafic necesar jocului.
 */
public class VisualAssets implements Runnable {
    public record AnimationData(String label, int offset, int row, int col, int count) {
        public AnimationData(String label, int count) {
            this(label, 0, 0, 0, count);
        }
        public AnimationData(String label, int offset, int count) {
            this(label, offset, 0, 0, count);
        }
        public AnimationData(String label, int row, int col, int count) {
            this(label, 0, row, col, count);
        }
    }

    // Meniuri
    public static BufferedImage startMenuBg;
    public static BufferedImage mainMenuBg;
    public static BufferedImage controlsMenuBg;

    // Elemente UI
    public static BufferedImage[] uiHealth;
    public static BufferedImage[] uiRanks;
    public static BufferedImage[] uiCd;

    // Entități (Jucător și Inamici)
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

    // Elemente de hartă
    public static BufferedImage[] bg1, bg2, bg3;
    public static BufferedImage[] worldTiles1 = new BufferedImage[NO_TILES] , worldTiles2 = new BufferedImage[NO_TILES], worldTiles3 = new BufferedImage[NO_TILES];
    public static BufferedImage[] chkp;
    public static BufferedImage[] spear_opening, spear_closing;
    public static BufferedImage[] ice;
    public static BufferedImage[] fire;

    public static int loadPercent;
    public static int TOTAL_LOAD = 17;

    /**
     * Functia care initializaza toate elementele grafice globale utilizate.
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
        InitL2();
        InitL3();
    }

    /**
     * Încarcă animațiile pentru toți inamicii din cele 3 niveluri.
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
                new AnimationData("Hitbox", 6, 0, 15), new AnimationData("Projectile", 8, 0, 20),
                new AnimationData("Spell", 7, 0, 16), new AnimationData("Summon", 9, 0, 6),
                new AnimationData("Hurt", 10, 0, 5), new AnimationData("Lifesteal", 11, 0, 10),
                new AnimationData("Death1", 0, 0, 6), new AnimationData("MoveToPhase2", 1, 0, 8),
                new AnimationData("Phase2Start", 3, 0, 32), new AnimationData("Death2", 11, 0, 16)};
        Boss = loadAnim("L3-Boss/SpriteSheet", bossFrames, 288, 60);
        ++loadPercent;
        BufferedImage bossProjectilesImg = ImageLoader.LoadImage("/textures/Entities/L3-Boss/Projectiles.png");
        SoulSpear = new SpriteSheet(bossProjectilesImg, 32, 32).makeSelection(0, 0, 3);
        SoulPillar = new SpriteSheet(bossProjectilesImg, 32, 32).makeSelection(1, 0, 10);
        ++loadPercent;
    }

    private static void InitDante() {
        AnimationData[] frames = {new AnimationData("Idle", 16, 6), new AnimationData("Run", 16, 8),
            new AnimationData("Jump", 16, 2), new AnimationData("Fall", 16, 2), new AnimationData("Hit", 16, 3),
            new AnimationData("Death", 16, 9), new AnimationData("Attack1", 16, 4), new AnimationData("Attack2", 16, 4),
            new AnimationData("Ice_Morph", 3), new AnimationData("Eden_Shield", 3), new AnimationData("Fiery_Judgement", 3)};
        Dante = loadAnim("Dante", frames);
    }

    private static HashMap<String, BufferedImage[]> loadAnim(String folder, AnimationData[] frames) {
        HashMap<String, BufferedImage[]> animations = new HashMap<>();
        for(AnimationData anim : frames) {
            try {
                BufferedImage img = ImageLoader.LoadImage("/textures/Entities/" + folder + "/" + anim.label + ".png");

                animations.put(anim.label, new SpriteSheet(img, img.getWidth() / anim.count, img.getHeight() - anim.offset)
                        .makeSelection(anim.row, anim.col, anim.count));
            }
            catch (Exception e) {
                System.out.println("Eroare la incarcarea " + folder + ": " + e.getMessage());
            }
        }

        return animations;
    }
    private static HashMap<String, BufferedImage[]> loadAnim(String fileName, AnimationData[] frames, int width, int height) {
        HashMap<String, BufferedImage[]> animations = new HashMap<>();
        for(AnimationData anim : frames) {
            try {
                BufferedImage img = ImageLoader.LoadImage("/textures/Entities/" + fileName + ".png");

                animations.put(anim.label, new SpriteSheet(img, width, height).makeSelection(anim.row, anim.col, anim.count));
            }
            catch (Exception e) {
                System.out.println("Eroare la incarcarea " + fileName + ": " + e.getMessage());
            }
        }

        return animations;
    }
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
            System.out.println("Eroare la incarcarea fundalelor nivelului 1: " + e.getMessage());
        }

        try {
            SpriteSheet snowSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/platform_snow.png"), 16, 16);
            fillTiles(worldTiles1, snowSheet, 1, 11, 121);
            ++loadPercent;

            SpriteSheet decoSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/deco.png"), 16, 16);
            fillTiles(worldTiles1, decoSheet, 122, 48, 2304);
            ++loadPercent;

            SpriteSheet penguinSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/penguin.png"), 16, 16);
            fillTiles(worldTiles1, penguinSheet, 2426, 4, 32);
            ++loadPercent;

            SpriteSheet allSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/all.png"), 16, 16);
            fillTiles(worldTiles1, allSheet, 2458, 64, 2176);
            ++loadPercent;
        }
        catch (Exception e) {
            System.out.println("Eroare la incarcarea dalelor nivelului 1: " + e.getMessage());
        }
    }
    public static void InitL2() {

    }
    public static void InitL3() {

    }

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
        }
        catch (Exception e) {
            System.out.println("Eroare la incarcarea gate-urilor:" + e.getMessage());
        }

        try {
            SpriteSheet checkSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/checkpoint.png"), 32, 32);
            chkp = new BufferedImage[]{
                    checkSheet.crop(0,0),
                    checkSheet.crop(0, 1),
                    checkSheet.crop(1, 0),
                    checkSheet.crop(1, 1)
            };
        }
        catch (Exception e) {
            System.out.println("Eroare la incarcarea checkpoint-urilor: " + e.getMessage());
        }
    }

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
            System.out.println("Eroare la incarcarea UI-ului: " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Eroare la decuparea UI-ului: " + e.getMessage());
        }
    }

    private static void fillTiles(BufferedImage[] worldTiles, SpriteSheet sheet, int startID, int columns, int totalCount) {
        int id = startID;
        for (int i = 0; i < totalCount; i++) {
            int x = i % columns;
            int y = i / columns;
            worldTiles[id] = sheet.crop(x, y);
            id++;
        }
    }

    private static void InitEffects() {
        ice = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/ice.png"), 32, 32).makeSelection(0, 2, 6);
        fire = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/fire.png"), 24, 32).makeSelection(0, 0, 8);
    }

    private static void InitMenus() {
        startMenuBg = ImageLoader.LoadImage("/textures/UI/Start-Menu.png");
        mainMenuBg = ImageLoader.LoadImage("/textures/UI/Main-Menu.png");
        controlsMenuBg = ImageLoader.LoadImage("/textures/UI/Controls-Menu.png");
    }
}