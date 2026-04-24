package PaooGame.Game.Input;

import PaooGame.Handler;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamepadManager {
    private final Handler handler;

    private ControllerManager controllers;
    private ControllerState currState;

    public boolean up, down, left, right, space,
            shift, j, k, l,
            enter, escape, debug;
    public float leftStickX, leftStickY;

    public GamepadManager(Handler handler) {
        this.handler = handler;
        controllers = new ControllerManager();
        controllers.initSDLGamepad();
    }

    public void Update() {
        currState = controllers.getState(0);

        if (currState.isConnected) {
            up = currState.dpadUp;
            down = currState.dpadDown;
            left = currState.dpadLeft;
            right = currState.dpadRight;

            space = currState.a;

            shift = currState.leftTrigger > 0.5f;

            j = currState.x;
            k = currState.b;
            l = currState.rightTrigger > 0.5f;

            enter = currState.a;
            escape = currState.start;
            debug = currState.back;

            leftStickX = currState.leftStickX;
            leftStickY = currState.leftStickY;
        }
    }
    public void reset() {
        up = down = left = right = space = shift = j = k = l = enter = escape = debug = false;
        leftStickX = 0;
        leftStickY = 0;
    }

    public void terminate() {
        controllers.quitSDLGamepad();
    }
}