package PaooGame.World.Entities;

import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Clasa care tine evidenta si distribuie instructiuni tuturor entitatilor din joc
 */
public class EntityManager {
    private final Dante dante;
    private final ArrayList<Entity> entities;

    /**
     * Campul care se comporta drept zona de asteptare atunci cand entitatile creeaza alte entitati
     */
    private final ArrayList<Entity> entitiesToAdd;

    /**
     * Campul care controleaza unde se duc noile entitati (in entities sau in entitiesToAdd).
     */
    private boolean isUpdating;
    private int enemyCount;

    /**
     * Comparator utilizat pentru a sorta entitatile in functie de coordonata Y, creand un efect de adancime
     */
    private final Comparator<Entity> renderSorter = (a, b) ->
            Float.compare(a.getY() + a.getLogicHeight(), b.getY() + b.getLogicHeight());

    /**
     * Metoda care initializeaza managerul de entitati si personajul principal
     * @param dante Referinta catre jucatorul principal.
     */
    public EntityManager(Dante dante) {
        this.dante = dante;

        entities = new ArrayList<>();
        entitiesToAdd = new ArrayList<>();

        addEntity(dante);
    }

    /**
     * Metoda care actualizeaza toate entitatile active si elimina entitatile inactive din memorie <br>
     * Dupa iterare, integreaza entitatile din lista de asteptare si sorteaza vectorul pentru desenare
     */
    public void Update() {
        isUpdating = true;

        Iterator<Entity> it = entities.iterator();

        while (it.hasNext()) {
            Entity e = it.next();
            e.Update();

            if (!e.isActive()) {
                if (e instanceof Enemy) {
                    --enemyCount;
                }
                it.remove();
            }
        }

        isUpdating = false;

        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();

        entities.sort(renderSorter);
    }

    /**
     * Metoda care deseneaza toate entitatile din lista curenta pe ecran
     * @param g Grafica de desenare
     */
    public void Draw(Graphics g) {
        for (Entity e : entities) {
            e.Draw(g);
        }
    }

    /**
     * Metoda care adauga o noua entitate in functie de {@link #isUpdating}
     * @param e Entitatea care trebuie adaugata
     */
    public void addEntity(Entity e) {
        if (e instanceof Enemy) {
            ++enemyCount;
        }

        if (isUpdating) {
            entitiesToAdd.add(e);
        } else {
            entities.add(e);
        }
    }

    /**
     * Metoda care reseteaza complet managerul, stergand toate entitatile si pastrand doar personajul principal
     */
    public void resetEntities() {
        entities.clear();
        entitiesToAdd.clear();
        enemyCount = 0;
        addEntity(dante);
    }

    public Dante getDante() { return dante; }
    public ArrayList<Entity> getEntities() { return entities; }
    public int getEnemyCount() { return enemyCount; }
}