package PaooGame.States.AudioElements;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class AudioManager {
    private static Clip currentBGM;

    public static void playBGM(Clip s) {
        if (s == null) {
            System.err.println("EROARE: Melodia nu există în memorie!");
            return;
        }

        if (currentBGM == s && currentBGM.isRunning()) {
            return;
        }

        stopBGM();

        currentBGM = s;
        currentBGM.setFramePosition(0);
        currentBGM.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
            currentBGM = null;
        }
    }

    public static void playSound(Clip s) {
        if (s != null) {
            if (s.isRunning()) {
                s.stop();
            }
            s.setFramePosition(0);
            s.start();
        }
    }
}