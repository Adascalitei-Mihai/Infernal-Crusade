package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;
import javax.sound.sampled.Clip;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Clasa care reprezinta un declansator invizibil pentru o arena de lupta
 */
public class ArenaTrigger extends Entity {
    private final Clip bgSong;
    private boolean triggered;
    private boolean finished;
    private final Arena arena;
    /**
     * Campul care controleaza miscarea portilor aferente arenei
     */
    private final Gate[] gates;

    /**
     * Constructorul care initializeaza campurile cu valorile date
     * @param handler Handler-ul
     * @param x Pozitia pe Ox a declansatorului
     * @param y Pozitia pe Oy a declansatorului
     * @param width Latimea zonei de declansare
     * @param height Inaltimea zonei de declansare.
     * @param arena Arena care este controlata
     * @param gates Portile care sunt controlate
     * @param bgSong Melodia de lupta care va fi redata pe durata arenei
     */
    public ArenaTrigger(Handler handler, float x, float y, int width, int height,
                        Arena arena, Gate[] gates, Clip bgSong) {
        super(handler, x, y, width, height);
        this.arena = arena;
        this.gates = gates;
        this.bgSong = bgSong;
    }

    /**
     * Metoda care actualizeaza starea arenei daca aceasta a fost declansata si gestioneaza deschiderea portilor la finalizare
     */
    @Override
    public void Update() {
        if (finished) {
            return;
        }

        if (triggered) {
            if (arena.isFinished()) {
                finished = true;

                for (Gate gate : gates) {
                    gate.open();
                }
            } else {
                arena.Update();
            }
        }
    }

    /**
     * Metoda care deseneaza zona de coliziune a declansatorului exclusiv in modul debug
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect((int) (x - handler.getGameCamera().getxOffset()),
                    (int) (y - handler.getGameCamera().getyOffset()),
                    logicWidth, logicHeight);
        }
    }

    /**
     * Metoda care defineste comportamentul la contactul cu o entitate <br>
     * Daca jucatorul atinge zona pentru prima data, portile se inchid si lupta incepe
     * @param e Entitatea cu care declansatorul a intrat in coliziune.
     */
    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Dante && !triggered) {
            triggered = true;

            for (Gate gate : gates) {
                gate.close();
            }

            arena.start();
            AudioManager.playBGM(bgSong);
        }
    }
}