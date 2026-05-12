package PaooGame.World.Entities.Creatures;

/**
 * Enumeratie care defineste complet o actiune sau un atac al lui {@link Dante}
 */
public enum DanteAction {
    IDLE(0, 0, 0, 0),
    ATTACKING(6, 15, 10, 45),
    ICE_MORPH(3, 1, 4, 0),
    EDEN_SHIELD(4, 0, 16, 240),
    FIERY_JUDGEMENT(30, 30, 15, 900);

    public final int windup;
    public int active;
    public final int recovery;
    public final int baseCooldown;

    DanteAction(int windup, int active, int recovery, int baseCooldown) {
        this.windup = windup;
        this.active = active;
        this.recovery = recovery;
        this.baseCooldown = baseCooldown;
    }
    public int getDuration() { return windup + active + recovery; }
    public void setActive(int duration) { active = duration; }
}
