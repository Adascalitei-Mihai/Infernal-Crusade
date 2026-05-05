package PaooGame.World.Entities;

import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class EntityManager {
    private final Dante dante;
    private final ArrayList<Entity> entities;

    private final ArrayList<Entity> entitiesToAdd;
    private boolean isUpdating = false;
    private int creatureCount;

    private final Comparator<Entity> renderSorter = (a, b) ->
            Float.compare(a.getY() + a.getHeight(), b.getY() + b.getHeight());

    public EntityManager(Dante dante) {
        this.dante = dante;

        entities = new ArrayList<>();
        entitiesToAdd = new ArrayList<>();

        addEntity(dante);
    }

    public void Update() {
        isUpdating = true;

        Iterator<Entity> it = entities.iterator();

        while (it.hasNext()) {
            Entity e = it.next();
            e.Update();

            if (!e.isActive()) {
                if(e instanceof Creature) {
                    --creatureCount;
                }
                it.remove();
            }
        }

        isUpdating = false;
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();

        entities.sort(renderSorter);
    }

    public void Draw(Graphics g) {
        for (Entity e : entities) {
            e.Draw(g);
        }
    }

    public void addEntity(Entity e) {
        if (e instanceof Enemy) {
            ++creatureCount;
        }

        if (isUpdating) {
            entitiesToAdd.add(e);
        }
        else {
            entities.add(e);
        }
    }

    public void resetEntities() {
        entities.clear();
        entitiesToAdd.clear();
        addEntity(dante);
    }

    public Dante getDante() { return dante; }
    public ArrayList<Entity> getEntities() { return entities; }
    public int getCreatureCount() { return creatureCount; }
}