package PaooGame.Audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Clasa care incarca in memorie audio <br>
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

    /**
     * Functia de incarcare a tuturor melodiilor
     */
    public void run() {
        menu = loadClip("/audio/menu.wav");
        level1 = loadClip("/audio/level1.wav");
        level2 = loadClip("/audio/level2.wav");
        level3 = loadClip("/audio/level3.wav");
        arena = loadClip("/audio/arena.wav");
        boss = loadClip("/audio/boss.wav");
    }

    /**
     * Functia care incarca o singura melodie
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