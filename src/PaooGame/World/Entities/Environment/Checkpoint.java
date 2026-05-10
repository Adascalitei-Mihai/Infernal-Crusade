package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.VisualAssets;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

/**
 * Clasa care permite jucatorului sa isi salveze progresul la interactiune
 */
public class Checkpoint extends Entity {
    private static final int LOGIC_WIDTH = 4 * TILE_SIZE;
    private static final int LOGIC_HEIGHT = 4 * TILE_SIZE;

    private final Animation flutter;
    private final int id;
    private boolean activated;

    /**
     * Constructorul care initializeaza un checkpoint, definindu-i pozitia, id-ul si zona extinsa de coliziune
     *
     * @param handler Handler-ul
     * @param x Pozitia pe Ox
     * @param y Pozitia pe Oy
     * @param id ID-ul specific
     */
    public Checkpoint(Handler handler, float x, float y, int id) {
        super(handler, x, y, LOGIC_WIDTH, LOGIC_HEIGHT);
        flutter = new Animation(10, VisualAssets.chkp);
        this.id = id;
    }

    /**
     * Metoda care actualizeaza animatia checkpoint-ului doar daca acesta a fost activat
     */
    @Override
    public void Update() {
        if (activated) {
            flutter.Update();
        }
    }

    /**
     * Metoda care deseneaza checkpoint-ul pe ecran, luand in calcul decalajul camerei <br>
     * In modul debug, afiseaza un dreptunghi reprezentand zona vizuala a acestuia
     *
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect(screenX, screenY, logicWidth, logicHeight);
        } else {
            if (activated) {
                g.drawImage(flutter.getCurrentFrame(), screenX, screenY, visualWidth, visualHeight, null);
            } else {
                g.drawImage(VisualAssets.chkp[0], screenX, screenY, visualWidth, visualHeight, null);
            }
        }
    }

    /**
     * Metoda care proceseaza interactiunea cu jucatorul <br>
     * Daca jucatorul atinge checkpoint-ul si acesta are un ID mai mare decat progresul curent, noul progres este inregistrat in Handler ({@link Handler#updateCurrChkp(int)})
     * @param e Entitatea care a interactionat cu checkpoint-ul
     */
    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Dante && !activated) {
            activated = true;

            if (id > handler.getCurrChkp()) {
                handler.updateCurrChkp(id);
            }
        }
    }
}