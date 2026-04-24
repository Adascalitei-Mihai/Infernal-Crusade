package PaooGame.Game.Input;

import PaooGame.Handler;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamepadManager extends InputManager {
    private ControllerManager controllers;
    private ControllerState currState;
    private static final float DEADZONE = 0.3f;

    public GamepadManager(Handler handler) {
        super(handler);
        controllers = new ControllerManager();
        controllers.initSDLGamepad();
    }

    public void Update() {
        currState = controllers.getState(0);

        if (currState.isConnected) {
            up = currState.dpadUp || currState.leftStickY < -DEADZONE;
            down = currState.dpadDown || currState.leftStickY > DEADZONE;
            left = currState.dpadLeft || currState.leftStickX < -DEADZONE;
            right = currState.dpadRight || currState.leftStickX > DEADZONE;

            space = currState.a;

            shift = currState.leftTrigger > 0.5f;

            j = currState.x;
            k = currState.b;
            l = currState.rightTrigger > 0.5f;

            enter = currState.a;
            escape = currState.start;
            debug = currState.back;
        }
    }
    public void terminate() {
        controllers.quitSDLGamepad();
    }
}