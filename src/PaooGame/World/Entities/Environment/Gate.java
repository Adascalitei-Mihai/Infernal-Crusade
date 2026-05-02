package PaooGame.World.Entities.Environment;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.Graphics.Animation;
import PaooGame.Graphics.Assets;

import java.awt.*;

import static PaooGame.World.Tile.TILE_SIZE;

public class Gate extends Entity {
    public static final int DEFAULT_WIDTH = TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;

    public enum GateState {
        OPEN,
        CLOSING,
        CLOSED,
        OPENING
    }

    private final Animation openAnim, closeAnim;
    private GateState currentState = GateState.OPEN;

    public Gate(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        closeAnim = new Animation(30, Assets.spear_closing);
        openAnim = new Animation(30, Assets.spear_opening);

        this.bounds = new Rectangle(2, 0, 12, 64);
    }

    @Override
    public void Update() {
        if (currentState == GateState.CLOSING) {
            closeAnim.Update();
            if (closeAnim.isFinished()) {
                currentState = GateState.CLOSED;
            }
        }
        else if (currentState == GateState.OPENING) {
            openAnim.Update();
            if (openAnim.isFinished()) {
                currentState = GateState.OPEN;
                closeAnim.reset();
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getxOffset());
        int screenY = (int) (y - handler.getyOffset());

        switch (currentState) {
            case CLOSING -> g.drawImage(closeAnim.getCurrentFrame(), screenX, screenY, width, height, null);
            case OPENING -> g.drawImage(openAnim.getCurrentFrame(), screenX, screenY, width, height, null);
            case CLOSED -> g.drawImage(Assets.spear_closing[Assets.spear_closing.length - 1], screenX, screenY, width, height, null);
            case OPEN -> {}
        }
    }

    public void close() {
        if (currentState == GateState.OPEN) {
            currentState = GateState.CLOSING;
            openAnim.reset();
        }
    }

    public void open() {
        if (currentState == GateState.CLOSED) {
            currentState = GateState.OPENING;
        }
    }

    public boolean isSolid() {
        return (currentState == GateState.CLOSING || currentState == GateState.CLOSED);
    }

    @Override
    public void onInteraction(Entity e) {}
}