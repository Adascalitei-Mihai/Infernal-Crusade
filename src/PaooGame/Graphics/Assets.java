package PaooGame.Graphics;

import PaooGame.Entities.Creatures.Dante;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import static PaooGame.Entities.Creatures.Dante.DEFAULT_WIDTH;

/**
 * Clasa care incarcă fiecare element grafic necesar jocului.
 */
public class Assets {
    public static BufferedImage startMenuBg;
    public static BufferedImage mainMenuBg;
    public static BufferedImage controlsMenuBg;

    public static HashMap<String, BufferedImage[]> Dante;


    /**
     * Functia care initializaza toate elementele grafice globale utilizate.
     */
    public static void Init() {
        InitMenus();
        InitDante();
    }

    public static void InitL1() {
        BufferedImage platforms = ImageLoader.LoadImage("/textures/Tiles/L1/Snow platform tileset.png");
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
