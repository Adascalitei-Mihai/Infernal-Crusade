package PaooGame.Graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static PaooGame.World.Tile.NO_TILES;

/**
 * Clasa care incarcă fiecare element grafic necesar jocului.
 */
public class Assets {
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
    public static HashMap<String, BufferedImage[]> Ghoul;
    public static HashMap<String, BufferedImage[]> Hunter;
    public static HashMap<String, BufferedImage[]> Necromancer;
    public static HashMap<String, BufferedImage[]> Boss;

    // Elemente de hartă
    public static BufferedImage[] worldTiles = new BufferedImage[NO_TILES];
    public static BufferedImage[] bg;
    public static BufferedImage[] chkp;
    public static BufferedImage[] spear_opening, spear_closing;
    public static BufferedImage ice;
    public static BufferedImage[] fire;

    /**
     * Functia care initializaza toate elementele grafice globale utilizate.
     */
    public static void Init() {
        InitMenus();
        InitDante();
        InitEnemies();
        InitUI();
        InitEffects();

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

        InitL1();
    }

    /**
     * Încarcă animațiile pentru toți inamicii din cele 3 niveluri.
     */
    public static void InitEnemies() {
        // NIVEL 1 - Skeleton
        Skeleton = new HashMap<>();
        Skeleton.put("Idle",   loadAnim("L1-Skeleton", "Skeleton_01_White_Idle.png", 8));
        Skeleton.put("Walk",   loadAnim("L1-Skeleton", "Skeleton_01_White_Walk.png", 10));
        Skeleton.put("Attack1",loadAnim("L1-Skeleton", "Skeleton_01_White_Attack1.png", 10));
        Skeleton.put("Attack2",loadAnim("L1-Skeleton", "Skeleton_01_White_Attack2.png", 9));
        Skeleton.put("Die",    loadAnim("L1-Skeleton", "Skeleton_01_White_Die.png", 13));
        Skeleton.put("Hurt",   loadAnim("L1-Skeleton", "Skeleton_01_White_Hurt.png", 5));

    }

    public static void InitDante() {
        Dante = new HashMap<>();
        Dante.put("Idle", loadDanteAnim("Idle.png", 6));
        Dante.put("Run", loadDanteAnim("Run.png", 8));
        Dante.put("Jump", loadDanteAnim("Jump.png", 2));
        Dante.put("Fall", loadDanteAnim("Fall.png", 2));
        Dante.put("Hit", loadDanteAnim("Hit.png", 3));
        Dante.put("Death", loadDanteAnim("Death.png", 9));
        Dante.put("Attack1", loadDanteAnim("Attack1.png", 4));
        Dante.put("Attack2", loadDanteAnim("Attack2.png", 4));

        Dante.put("Ice_Morph", loadDanteAbility("Ice_Morph.png", 3));
        Dante.put("Eden_Shield", loadDanteAbility("Eden_Shield.png", 7));
        Dante.put("Fiery_Judgement", loadDanteAbility("Fiery_Judgement.png", 3));
    }

    /**
     * Metodă generică pentru a încărca orice animație de entitate (Inamici).
     */
    private static BufferedImage[] loadAnim(String folder, String fileName, int frameCount) {
        String path = "/textures/Entities/" + folder + "/" + fileName;
        BufferedImage img = ImageLoader.LoadImage(path);

        if (img == null) {
            System.err.println("EROARE: Nu s-a putut încărca imaginea: " + path);
            return new BufferedImage[0];   // returnăm array gol ca să nu crape
        }

        return new SpriteSheet(img, 96, img.getHeight()).sliceRow(0, frameCount);
    }

    private static BufferedImage[] loadDanteAnim(String fileName, int frameCount) {
        BufferedImage img = ImageLoader.LoadImage("/textures/Entities/Dante/" + fileName);
        if (img == null) return null;
        return new SpriteSheet(img, 184, img.getHeight() - 16).sliceRow(0, frameCount);
    }
    private static BufferedImage[] loadDanteAbility(String fileName, int frameCount) {
        BufferedImage img = ImageLoader.LoadImage("/textures/Entities/Dante/" + fileName);
        if (img == null) return null;
        return new SpriteSheet(img, 340, img.getHeight() - 32).sliceRow(0, frameCount);
    }

    public static void InitL1() {
        bg = new BufferedImage[]{
                ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer5.png"),
                ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer4.png"),
                ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer2.png"),
                ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer1.png"),
                ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer3.png"),
                ImageLoader.LoadImage("/textures/Maps/L1/fundal_arena.png")
        };

        SpriteSheet snowSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/platform_snow.png"), 16, 16);
        fillTiles(snowSheet, 1, 11, 121);

        SpriteSheet decoSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/deco.png"), 16, 16);
        fillTiles(decoSheet, 122, 48, 2304);

        SpriteSheet penguinSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/penguin.png"), 16, 16);
        fillTiles(penguinSheet, 2426, 4, 32);

        SpriteSheet allSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/all.png"), 16, 16);
        fillTiles(allSheet, 2458, 64, 2176);

        SpriteSheet checkSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/checkpoint.png"), 32, 32);
        chkp = new BufferedImage[]{
                checkSheet.crop(0,0),
                checkSheet.crop(0, 1),
                checkSheet.crop(1, 0),
                checkSheet.crop(1, 1)
        };
    }

    public static void InitUI() {
        BufferedImage uiSheetImg = ImageLoader.LoadImage("/textures/UI/Overlay.png");

        if (uiSheetImg == null) {
            System.err.println("EROARE: Overlay.png nu a fost găsit la calea specificată!");
            return;
        }

        try {
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

        } catch (Exception e) {
            System.err.println("EROARE FATALĂ la decuparea UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void fillTiles(SpriteSheet sheet, int startID, int columns, int totalCount) {
        int id = startID;
        for (int i = 0; i < totalCount; i++) {
            int x = i % columns;
            int y = i / columns;
            worldTiles[id] = sheet.crop(x, y);
            id++;
        }
    }

    private static void InitEffects() {
        ice = ImageLoader.LoadImage("/textures/Maps/ice.png");
        fire = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/fire.png"), 24, 32).sliceRow(0, 8);
    }

    public static void InitMenus() {
        startMenuBg = ImageLoader.LoadImage("/textures/UI/Start-Menu.png");
        mainMenuBg = ImageLoader.LoadImage("/textures/UI/Main-Menu.png");
        controlsMenuBg = ImageLoader.LoadImage("/textures/UI/Controls-Menu.png");
    }
}