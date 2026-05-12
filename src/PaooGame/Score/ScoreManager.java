package PaooGame.Score;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;

/**
 * Clasa care calculeaza scorul
 */
public class ScoreManager {
    private final Handler handler;

    private int totalScore;
    private int timeSinceCheckpoint;

    private static final int STARTING_SCORE = 1000;
    private static final int SCORE_PER_HIT = 10;
    private static final int SCORE_PER_REFLECT = 50;
    private static final float TIME_PENALTY_PER_SEC = 0.5f;

    /**
     * Constructorul care initializeaza campurile
     * @param handler Handler-ul
     */
    public ScoreManager(Handler handler) { this.handler = handler; }

    /**
     * Metoda care actualizeaza timpul trecut
     */
    public void Update() {
        timeSinceCheckpoint++;
    }

    /**
     * Metoda care adauga puncte pentru lovitura in functie de {@link #SCORE_PER_HIT}, rank si dificultate
     * @param currentRank Rank-ul curent
     */
    public void addSuccessfulHit(Dante.Ranks currentRank) {
        float rankMultiplier = 1.0f + (currentRank.ordinal() * 0.5f);
        int pointsGained = (int) (SCORE_PER_HIT * rankMultiplier * getDifficultyMultiplier());

        totalScore += pointsGained;
    }

    /**
     * Metoda care adauga puncte pentru reflectie de proiectibil in functie de {@link #SCORE_PER_REFLECT} si dificultate
     */
    public void addReflect() {
        int pointsGained = (int) (SCORE_PER_REFLECT * getDifficultyMultiplier());
        totalScore += pointsGained;
    }

    /**
     * Metoda care reseteaza temporizatorul
     */
    public void resetCheckpointTimer() {
        timeSinceCheckpoint = 0;
        totalScore = STARTING_SCORE;
    }

    /**
     * Metoda care foloseste {@link Handler#getDifficulty()}
     * @return Multiplicatorul de rank
     */
    private float getDifficultyMultiplier() {
        return switch (handler.getDifficulty()) {
            case EASY -> 0.5f;
            case HARD -> 1.5f;
            default -> 1.0f;
        };
    }

    /**
     * Metoda care calculeaza scorul, pornind de la {@link #STARTING_SCORE} si scorul acumulat din care se scade o penalizare de timp in functie de {@link #TIME_PENALTY_PER_SEC}
     * @return Scorul final
     */
    public int calculateFinalScore() {
        int timePenalty = (int) ((float) timeSinceCheckpoint / 60 * TIME_PENALTY_PER_SEC);
        int finalScore = totalScore - timePenalty;
        return Math.max(0, finalScore);
    }
}