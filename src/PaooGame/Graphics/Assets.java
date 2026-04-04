package PaooGame.Graphics;

import PaooGame.Tiles.Tile;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Clasa care incarcă fiecare element grafic necesar jocului.
 */
public class Assets {
    public static BufferedImage startMenuBg;
    public static BufferedImage mainMenuBg;
    public static BufferedImage controlsMenuBg;

    public static HashMap<String, BufferedImage[]> Dante;
    public static BufferedImage[] worldTiles = new BufferedImage[5000];
    public static BufferedImage bgMuntiSpate, bgMunti, bgPadureCeata, bgPadureClara, bgIarba, bgArena;

    /**
     * Functia care initializaza toate elementele grafice globale utilizate.
     */
    public static void Init() {
        InitMenus();
        InitDante();
    }

    public static void InitL1() {
        bgMuntiSpate = ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer5.png");
        bgMunti = ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer4.png");
        bgPadureCeata = ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer2.png");
        bgPadureClara = ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer1.png");
        bgIarba = ImageLoader.LoadImage("/textures/Maps/L1/fundal_layer3.png");
        bgArena = ImageLoader.LoadImage("/textures/Maps/L1/fundal_arena.png");
        
        SpriteSheet snowSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/platform_snow.png"), 16, 16);
        fillTiles(snowSheet, 1, 11, 121);
        
        SpriteSheet decoSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/deco.png"), 16, 16);
        fillTiles(decoSheet, 122, 48, 2304);
        
        SpriteSheet penguinSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/penguin.png"), 16, 16);
        fillTiles(penguinSheet, 2426, 4, 32);
        
        SpriteSheet allSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/all.png"), 16, 16);
        fillTiles(allSheet, 2458, 64, 2176);

        SpriteSheet checkSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Maps/L1/checkpoint.png"), 16, 16);
        fillTiles(checkSheet, 4634, 4, 16);
    }

    /**
     * Metodă utilitară pentru a umple vectorul worldTiles cu sub-imagini
     */
    private static void fillTiles(SpriteSheet sheet, int startID, int columns, int totalCount) {
        int id = startID;
        for (int i = 0; i < totalCount; i++) {
            int x = i % columns;
            int y = i / columns;
            worldTiles[id] = sheet.crop(x, y);
            id++;
        }
    }
    /**
     * Functia care initializeaza meniurile utilizate.
     */
    public static void InitMenus() {
        startMenuBg = ImageLoader.LoadImage("/textures/UI/Start-Menu.png");
        mainMenuBg = ImageLoader.LoadImage("/textures/UI/Main-Menu.png");
        controlsMenuBg = ImageLoader.LoadImage("/textures/UI/Controls-Menu.png");
    }
    public static void InitDante() {
        Dante = new HashMap<>();

        Dante.put("Idle", loadDanteAnim("Idle.png", 6, 32));
        Dante.put("Run", loadDanteAnim("Run.png", 8, 32));
        Dante.put("Jump", loadDanteAnim("Jump.png", 2, 32));
        Dante.put("Fall", loadDanteAnim("Fall.png", 2, 32));
        Dante.put("Attack1", loadDanteAnim("Attack1.png", 4, 32));
        Dante.put("Attack2", loadDanteAnim("Attack2.png", 4, 32));
        Dante.put("Hit", loadDanteAnim("Hit.png", 3, 32));
        Dante.put("Death", loadDanteAnim("Death.png", 10,32));

        Dante.put("Ice_Morph", loadDanteAnim("Ice_Morph.png", 3, 192));
        Dante.put("Eden_Shield", loadDanteAnim("Eden_Shield.png", 7, 192));
        Dante.put("Fiery_Judgement", loadDanteAnim("Fiery_Judgement.png", 3,192));
    }

    private static BufferedImage[] loadDanteAnim(String fileName, int frameCount, int width) {
        BufferedImage img = ImageLoader.LoadImage("/textures/Entities/Dante/" + fileName);
        if (img == null) return null;

        return new SpriteSheet(img, width, img.getHeight()).sliceRow(0, frameCount);
    }
}
