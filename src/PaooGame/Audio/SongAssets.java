package PaooGame.Audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Clasa care incarca elementele audio <br>
 * @see javax.sound.sampled
 * Implementeaza Runnable pentru folosirea thread-urilor
 */
public class SongAssets implements Runnable {
    public static Clip menu;
    public static Clip level1;
    public static Clip level2;
    public static Clip level3;
    public static Clip arena;
    public static Clip boss;

    public static int loadPercent;
    public static int TOTAL_LOAD = 6;

    /**
     * Metoda de incarcare a tuturor melodiilor, actualizeaza procentul de elemente incarcat <br>
     * Apeleaza {@link #loadClip(String)}
     */
    public void run() {
        menu = loadClip("/audio/menu.wav");
        ++loadPercent;
        level1 = loadClip("/audio/level1.wav");
        ++loadPercent;
        level2 = loadClip("/audio/level2.wav");
        ++loadPercent;
        level3 = loadClip("/audio/level3.wav");
        ++loadPercent;
        arena = loadClip("/audio/arena.wav");
        ++loadPercent;
        boss = loadClip("/audio/boss.wav");
        ++loadPercent;
    }

    /**
     * Metoda care incarca o singura melodie
     * @param path Locatia melodiei
     * @return Obiectul specific melodiei
     */
    private static Clip loadClip(String path) {
        try {
            InputStream is = SongAssets.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("[EROARE]: Fisierul audio nu a fost gasit: " + path);
                return null;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            return clip;
        } catch (Exception e) {
            System.err.println("[EROARE]: Incarcarea fisierului audio: " + e.getMessage());
        }

        return null;
    }
}