package PaooGame.States.AudioElements;

import javax.sound.sampled.Clip;

/**
 * Clasa care manipuleaza melodia de fundal
 */
public class AudioManager {
    private static Clip currentBGM;

    /**
     * Metoda care porneste melodia si o tine in bucla
     * @param s Melodia aleasa
     */
    public static void playBGM(Clip s) {
        if (s == null) {
            System.err.println("[EROARE]: Melodia nu exista în memorie");
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

    /**
     * Metoda care opreste melodia curenta
     */
    public static void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
            currentBGM = null;
        }
    }
}