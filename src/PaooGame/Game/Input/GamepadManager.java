package PaooGame.Game.Input;

import PaooGame.Handler;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

/**
 * Clasa care asculta input-ul controller-ului
 * @see com.studiohartman.jamepad
 */
public class GamepadManager extends InputManager {
    private final ControllerManager controllers;
    private ControllerState currState;
    private static final float DEADZONE = 0.3f;
    private static final float ACC_POINT = 0.5f;

    /**
     * Constructor care initializeaza {@link #controllers}
     * @param handler Handler-ul
     */
    public GamepadManager(Handler handler) {
        super(handler);
        controllers = new ControllerManager();
        controllers.initSDLGamepad();
    }

    /**
     * Metoda care retine activarea butoanelor <br>
     * Pentru miscare, se asculta dpad-ul si daca suntem in afara {@link #DEADZONE} <br>
     * Pentru abilitatile care folosesc trigger-e, verificam daca suntem in afara {@link #ACC_POINT}
     */
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

    /**
     * Metoda care aplica vibratii controller-ului
     * @param leftMotor Motorul din stanga
     * @param rightMotor Motorul din dreapta
     * @param durationMs Durata (in ms)
     */
    public void rumble(float leftMotor, float rightMotor, int durationMs) {
        if (currState != null && currState.isConnected) {
            controllers.doVibration(0, leftMotor, rightMotor, durationMs);
        }
    }

    /**
     * Metoda care inchide conexiunea
     */
    public void terminate() {
        controllers.quitSDLGamepad();
    }
}