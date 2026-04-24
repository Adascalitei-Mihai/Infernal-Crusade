package PaooGame.States;

import PaooGame.Handler;
import PaooGame.World.Levels.Level;
import PaooGame.World.Levels.Level1;

import java.awt.*;



public class PlayState extends State {
    public enum InputMode { KEYBOARD, GAMEPAD }

    private InputMode currentMode = InputMode.KEYBOARD;
    private Level level;
    private boolean initializedFullscreen = false;
    private boolean debugKeyPressed = false;

    public PlayState(Handler handler) {
        super(handler);

        level = new Level1(handler);
    }


    @Override
    public void Update() {
        detectInputMode();

        if (!initializedFullscreen) {
            handler.getGame().getWnd().setFullScreen(true);
            handler.getGame().getWnd().hideCursor(true);
            initializedFullscreen = true;
        }

        level.Update();
        handler.getGameCamera().center(level.getEntityManager().getDante());

        if(handler.getKeyManager().escape) {
            handler.getGame().getGameCamera().resetToLevelBounds();
            handler.getGame().getWnd().setWindowed();
            handler.getKeyManager().reset();
            handler.setState(new MainMenu(handler, "Continue"));
        }

        if(handler.getKeyManager().debug) {
            if(!debugKeyPressed) {
                handler.setDebugMode(!handler.getDebugMode());
                debugKeyPressed = true;
            }
        } else {
            debugKeyPressed = false;
        }
    }

    private void detectInputMode() {
        if (handler.getKeyManager().up || handler.getKeyManager().down ||
                handler.getKeyManager().left || handler.getKeyManager().right ||
                handler.getKeyManager().space) {
            if (currentMode != InputMode.KEYBOARD) {
                currentMode = InputMode.KEYBOARD;
                System.out.println("Switched to Keyboard");
            }
        }
//        if (handler.getPadManager().space ||
//                Math.abs(handler.getPadManager().leftStickX) > 0.2f ||
//                Math.abs(handler.getPadManager().leftStickY) > 0.2f) {
//            if (currentMode != InputMode.GAMEPAD) {
//                currentMode = InputMode.GAMEPAD;
//                System.out.println("Switched to Gamepad");
//            }
//        }
    }

    @Override
    public void Draw(Graphics g) {
        level.Draw(g);
    }
}