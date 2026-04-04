package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.Game.Game;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;

import java.awt.*;

public class SpearGate extends Entity {
    private Animation openAnim, closeAnim;
    private int state = 0; // 0: Open, 1: Closing, 2: Closed, 3: Opening

    public SpearGate(Handler handler, float x, float y) {
        super(handler, x, y, 16, 64);

        closeAnim = new Animation(80, Assets.spear_closing);
        openAnim = new Animation(80, Assets.spear_opening);

        this.bounds = new Rectangle(2, 0, 12, 64);
    }

    @Override
    public void Update() {
        if (state == 1) { // CLOSING
            closeAnim.tick();
            if (closeAnim.isFinished()) {
                state = 2;
            }
        } else if (state == 3) { // OPENING
            openAnim.tick();
            if (openAnim.isFinished()) {
                state = 0;
                closeAnim.reset();
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getxOffset());
        int screenY = (int) (y - handler.getyOffset());

        switch (state) {
            case 1 -> g.drawImage(closeAnim.getCurrentFrame(), screenX, screenY, width, height, null);
            case 3 -> g.drawImage(openAnim.getCurrentFrame(), screenX, screenY, width, height, null);
            case 2 -> g.drawImage(Assets.spear_closing[Assets.spear_closing.length - 1], screenX, screenY, width, height, null);
        }
    }

    public void close() { if(state == 0) state = 1; }
    public void open() { if(state == 2) state = 3; }

    public boolean isSolid() { return (state == 1 || state == 2); }

    @Override
    public void onInteraction(Entity e) {}
}
