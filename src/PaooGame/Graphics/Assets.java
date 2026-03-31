package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/**
 * Clasa care incarcă fiecare element grafic necesar jocului.
 */
public class Assets {
    public static BufferedImage startMenuBg;
    public static BufferedImage mainMenuBg;
    public static BufferedImage controlsMenuBg;

    /**
     * Functia care initializaza toate elementele grafice globale utilizate.
     */
    public static void Init() {
        InitMenus();
    }

    /**
     * Functia care initializeaza meniurile utilizate.
     */
    public static void InitMenus() {
        startMenuBg = ImageLoader.LoadImage("/textures/UI/Start-Menu.png");
        mainMenuBg = ImageLoader.LoadImage("/textures/UI/Main-Menu.png");
        controlsMenuBg = ImageLoader.LoadImage("/textures/UI/Controls-Menu.png");
    }
}
