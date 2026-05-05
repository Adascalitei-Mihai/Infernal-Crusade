package PaooGame.Graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Clasa ce contine o metoda statica pentru incarcarea unei imagini in memorie.
 */
public class ImageLoader {

    /**
     * Incarca o imagine intr-un obiect BufferedImage si returneaza o referinta catre acesta.
     * @param path Calea relativa pentru localizarea fisierul imagine.
     * @return Imaginea incarcata in memorie.
     */
    public static BufferedImage LoadImage(String path) throws IllegalArgumentException {
        InputStream resourceStream = ImageLoader.class.getResourceAsStream(path);

        if (resourceStream == null) {
            throw new IllegalArgumentException("Eroare: path imagine invalid: " + path);
        }
        try {
            return ImageIO.read(resourceStream);
        }
        catch(IOException e) {
            throw new RuntimeException("Eroare la citirea imaginii: " + path);
        }
    }
}

