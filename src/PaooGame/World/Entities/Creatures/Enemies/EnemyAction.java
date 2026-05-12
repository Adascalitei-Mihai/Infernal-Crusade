package PaooGame.World.Entities.Creatures.Enemies;

/**
 * Record care defineste complet o actiune sau un atac al unui {@link Enemy}
 * @param actionId Identificator unic (ex: "MELEE", "SHOOT", "SUMMON")
 * @param windup Cadrele de pre-atac (avertizare)
 * @param active Cadrele in care atacul face damage/spawneaza
 * @param recovery Cadrele de revenire (vulnerabilitate)
 * @param baseCooldown Timpul de asteptare pana la o noua utilizare
 * @param triggerRange Distanta de la care inamicul decide sa foloseasca actiunea
 * @param stopsMovement Daca inamicul se opreste din mers cand face actiunea
 */
public record EnemyAction(
        String actionId,
        int windup,
        int active,
        int recovery,
        int baseCooldown,
        float triggerRange,
        boolean stopsMovement
) {
    /**
     * Metoda care returneaza durata totala a actiunii in cadre.
     */
    public int getDuration() { return windup + active + recovery; }
}