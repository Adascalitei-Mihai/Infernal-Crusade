package PaooGame.Graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Clasa care poate incarca o imagine
 */
public class ImageLoader {

    /**
     * Metoda care Incarca o imagine intr-un obiect BufferedImage si returneaza o referinta catre acesta
     * @param path Calea relativa pentru localizarea fisierul imagine
     * @return Imaginea incarcata in memorie
     * @throws IllegalArgumentException Daca calea imaginii este invalida
     * @throws RuntimeException Daca imaginea este invalida
     */
    public static BufferedImage LoadImage(String path) throws IllegalArgumentException {
        InputStream resourceStream = ImageLoader.class.getResourceAsStream(path);

        if (resourceStream == null) {
            throw new IllegalArgumentException("[EROARE]: Cale imagine invalida: " + path);
        }
        try {
            return ImageIO.read(resourceStream);
        } catch(IOException e) {
            throw new RuntimeException("[EROARE]: Citirea imaginii: " + path);
        }
    }
}

