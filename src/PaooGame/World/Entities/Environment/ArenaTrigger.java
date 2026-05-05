package PaooGame.World.Entities.Environment;

import PaooGame.Audio.SongAssets;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.World.Arena;
import PaooGame.World.Entities.Creatures.Dante;
import PaooGame.World.Entities.Entity;

import javax.sound.sampled.Clip;
import java.awt.*;

public class ArenaTrigger extends Entity {
    private final Clip bgSong;
    private boolean triggered;
    private boolean finished;
    private final Arena arena;
    private final Gate[] gates;

    public ArenaTrigger(Handler handler, float x, float y, int width, int height,
                        Arena arena, Gate[] gates, Clip bgSong) {
        super(handler, x, y, width, height);
        this.arena = arena;
        this.gates = gates;
        this.bgSong = bgSong;
    }

    @Override
    public void Update() {
        if (finished) return;

        if (triggered) {
            if (arena.isFinished()) {
                finished = true;

                for (int i = 0; i < gates.length; ++i) {
                    gates[i].open();
                }
            }
            else {
                arena.Update();
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (handler.getDebugMode()) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect((int) (x - handler.getGameCamera().getxOffset()),
                    (int) (y - handler.getGameCamera().getyOffset()), width, height);
        }
    }

    @Override
    public void onInteraction(Entity e) {
        if (e instanceof Dante && !triggered) {
            triggered = true;

            for (int i = 0; i < gates.length; ++i) {
                gates[i].close();
            }

            arena.start();
            AudioManager.playBGM(bgSong);
        }
    }
}