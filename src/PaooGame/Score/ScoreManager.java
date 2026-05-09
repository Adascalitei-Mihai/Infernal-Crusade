package PaooGame.Score;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;

public class ScoreManager {
    private Handler handler;

    private int totalScore;
    private int timeSinceCheckpoint;

    private static final int STARTING_SCORE = 100;
    private static final int SCORE_PER_HIT = 10;
    private static final int SCORE_PER_REFLECT = 50;
    private static final float TIME_PENALTY_PER_SEC = 0.5f;

    public ScoreManager(Handler handler) {
        this.handler = handler;
    }

    public void Update() {
        totalScore = STARTING_SCORE;
        timeSinceCheckpoint++;
    }

    public void addSuccessfulHit(Dante.Ranks currentRank) {
        float rankMultiplier = 1.0f + (currentRank.ordinal() * 0.5f);
        int pointsGained = (int) (SCORE_PER_HIT * rankMultiplier * getDifficultyMultiplier());

        totalScore += pointsGained;
    }

    public void addReflect() {
        int pointsGained = (int) (SCORE_PER_REFLECT * getDifficultyMultiplier());
        totalScore += pointsGained;
    }

    public void resetCheckpointTimer() {
        timeSinceCheckpoint = 0;
        totalScore = STARTING_SCORE;
    }

    private float getDifficultyMultiplier() {
        return switch (handler.getDifficulty()) {
            case EASY -> 0.5f;
            case HARD -> 1.5f;
            default -> 1.0f;
        };
    }

    public int calculateFinalScore() {
        int timePenalty = (int) ((float) timeSinceCheckpoint / 60 * TIME_PENALTY_PER_SEC);
        int finalScore = totalScore - timePenalty;
        return Math.max(0, finalScore);
    }
}