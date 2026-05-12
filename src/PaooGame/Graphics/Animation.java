package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/**
 * Clasa care implementeaza logica animatiilor
 */
public class Animation {
    /**
     * Campul care este vectorul de cadre
     */
    private final BufferedImage[] frames;
    /**
     * Campul care este vectorul de durate (cate frame-uri sa aiba fiecare cadru) <br>
     * Un element egal cu -1 denota un cadru care nu mai este actualizat
     */
    private final int[] frameDurations;
    /**
     * Campul care este indexul curent in vectori
     */
    private int index;
    /**
     * Campul care indica frame-urile petrecute pe cadrul curenta
     */
    private int tickCount;

    /**
     * Constructorul care creeza vectorul de durate si il initializeaza
     * @param uniformDuration Durata uniforma al unui cadru
     * @param frames Vectorul de cadre
     */
    public Animation(int uniformDuration, BufferedImage[] frames) {
        this.frames = frames;
        this.frameDurations = new int[frames.length];

        for (int i = 0; i < frames.length; i++) {
            this.frameDurations[i] = uniformDuration;
        }

        this.index = 0;
        this.tickCount = 0;
    }

    /**
     * Metoda care actualizeaza animatia si o suspenda (daca {@code frameDurations[index] == -1}) sau trece la urmatoarul cadru (cu wrap-around)
     */
    public void Update() {
        if (frameDurations[index] == -1) {
            return;
        }

        tickCount++;

        if (tickCount >= frameDurations[index]) {
            tickCount = 0;
            index++;

            if (index >= frames.length) {
                index = 0;
            }
        }
    }

    /**
     * Metoda care modifica durata unui cadru
     * @param frameIndex Indexul cadrului
     * @param duration Noua durata
     * @throws IllegalArgumentException Daca cadrul are un index invalid
     */
    public void setFrameDuration(int frameIndex, int duration) {
        if (frameIndex >= 0 && frameIndex < frameDurations.length) {
            frameDurations[frameIndex] = duration;
        } else {
            throw new IllegalArgumentException("[ANIMATION]: Index cadru invalid!");
        }
    }

    /**
     * Metoda care setaza animatia in starea initiala
     */
    public void reset() {
        index = 0;
        tickCount = 0;
    }

    public BufferedImage getCurrentFrame() { return frames[index]; }
    public boolean isFinished() { return index == frames.length - 1; }
}