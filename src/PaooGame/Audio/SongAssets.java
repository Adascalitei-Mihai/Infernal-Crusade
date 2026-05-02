package PaooGame.Audio;

import PaooGame.States.AudioElements.AudioManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SongAssets {
    public static Clip menu;
    public static Clip level1;
    public static Clip level2;
    public static Clip level3;
    public static Clip arena;
    public static Clip boss;

    public static void Init() {
        menu = loadClip("/audio/menu.wav");
        level1 = loadClip("/audio/level1.wav");
        level2 = loadClip("/audio/level2.wav");
        level3 = loadClip("/audio/level3.wav");
        arena = loadClip("/audio/arena.wav");
        boss = loadClip("/audio/boss.wav");
    }

    private static Clip loadClip(String path) {
        try {
            InputStream is = AudioManager.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("EROARE: Fișierul audio nu a fost găsit: " + path);
                return null;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            return clip;
        } catch (Exception e) {
            System.err.println("EROARE la încărcarea fișierului audio: " + path);
            e.printStackTrace();
        }

        return null;
    }
}
