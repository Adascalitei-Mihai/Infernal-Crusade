package PaooGame.World.Entities;

import PaooGame.World.Entities.Creatures.Creature;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.Game.Game;
import PaooGame.World.Entities.Creatures.Enemies.Enemy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class EntityManager {
    private final Dante dante;
    private final ArrayList<Entity> entities;

    private final Comparator<Entity> renderSorter = (a, b) ->
            Float.compare(a.getY() + a.getHeight(), b.getY() + b.getHeight());

    public EntityManager(Dante dante) {
        this.dante = dante;

        entities = new ArrayList<>();
        addEntity(dante);

    }

    public void Update() {
        Iterator<Entity> it = entities.iterator();

        while (it.hasNext()) {
            Entity e = it.next();
            e.Update();

            if (!e.isActive()) {
                it.remove();
            }
        }

        entities.sort(renderSorter);
    }

    public void Draw(Graphics g) {
        for (Entity e : entities) {
            e.Draw(g);
        }
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public Dante getDante() { return dante; }
    public ArrayList<Entity> getEntities() { return entities; }
}