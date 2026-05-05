package PaooGame.Graphics;

import java.awt.image.BufferedImage;

public class Animation {
    private int index;
    private int tickCount;
    private final BufferedImage[] frames;
    private final int[] frameDurations;

    public Animation(int uniformDuration, BufferedImage[] frames) {
        this.frames = frames;
        this.frameDurations = new int[frames.length];

        for (int i = 0; i < frames.length; i++) {
            this.frameDurations[i] = uniformDuration;
        }

        this.index = 0;
        this.tickCount = 0;
    }

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

    public void setFrameDuration(int frameIndex, int duration) {
        if (frameIndex >= 0 && frameIndex < frameDurations.length) {
            frameDurations[frameIndex] = duration;
        } else {
            throw new IllegalArgumentException("Index cadru invalid!");
        }
    }

    public BufferedImage getCurrentFrame() {
        return frames[index];
    }

    public boolean isFinished() {
        return index == frames.length - 1;
    }

    public void reset() {
        index = 0;
        tickCount = 0;
    }
}