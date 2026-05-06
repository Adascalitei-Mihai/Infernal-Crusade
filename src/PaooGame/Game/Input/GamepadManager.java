package PaooGame.Game.Input;

import PaooGame.Handler;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamepadManager extends InputManager {
    private ControllerManager controllers;
    private ControllerState currState;
    private static final float DEADZONE = 0.3f;
    private static final float ACC_POINT = 0.5f;

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

            shift = currState.leftTrigger > ACC_POINT;
            j = currState.x;
            k = currState.b;
            l = currState.rightTrigger > ACC_POINT;

            enter = currState.a;
            pause = currState.y;
            escape = currState.start;
            debug = currState.back;
        }
    }
    public void rumble(float leftMotor, float rightMotor, int durationMs) {
        if (currState != null && currState.isConnected) {
            controllers.doVibration(0, leftMotor, rightMotor, durationMs);
        }
    }

    public void terminate() {
        controllers.quitSDLGamepad();
    }
}