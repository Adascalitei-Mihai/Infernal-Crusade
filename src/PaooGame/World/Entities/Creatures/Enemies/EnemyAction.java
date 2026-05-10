package PaooGame.World.Entities.Creatures.Enemies;

/**
 * Record care defineste complet o actiune sau un atac al unui inamic.
 * Inlocuieste vechiul ActionState si permite adaugarea nelimitata de atacuri.
 */
public record EnemyAction(
        String actionId,       // Identificator unic (ex: "MELEE", "SHOOT", "SUMMON")
        int windup,            // Cadrele de pre-atac (avertizare)
        int active,            // Cadrele in care atacul face damage/spawneaza
        int recovery,          // Cadrele de revenire (vulnerabilitate)
        int baseCooldown,      // Timpul de asteptare pana la o noua utilizare
        float triggerRange,    // Distanta de la care inamicul decide sa foloseasca actiunea
        boolean stopsMovement  // Daca inamicul se opreste din mers cand face asta
) {
    /**
     * Metoda care returneaza durata totala a actiunii in cadre.
     */
    public int getDuration() {
        return windup + active + recovery;
    }
}