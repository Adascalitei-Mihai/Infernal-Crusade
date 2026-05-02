package PaooGame.States;

import PaooGame.Audio.SongAssets;
import PaooGame.Game.Input.GamepadManager;
import PaooGame.Game.Input.KeyManager;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.States.VisualElements.UI;
import PaooGame.World.Levels.Level;
import PaooGame.World.Levels.Level1;

import java.awt.*;



public class PlayState extends State {
    private Level level;
    private UI overlay;
    private boolean initializedFullscreen = false;
    private boolean debugKeyPressed = false;

    public PlayState(Handler handler) {
        super(handler);

        level = new Level1(handler);
        overlay = new UI(handler);
    }


    @Override
    public void Update() {
        detectInputMode();

        if (!initializedFullscreen) {
            handler.getGame().getWnd().setFullScreen(true);
            handler.getGame().getWnd().hideCursor(true);
            initializedFullscreen = true;
        }

        handler.getScoreMan().Update();
        level.Update();
        handler.getGameCamera().center(level.getEntityManager().getDante());

        if(handler.getInputManager().escape) {
            handler.getGame().getGameCamera().resetToLevelBounds();
            handler.getGame().getWnd().setWindowed();
            handler.getInputManager().reset();
            handler.setState(new MainMenu(handler, "Continue"));
        }

        if(handler.getInputManager().debug) {
            if(!debugKeyPressed) {
                handler.setDebugMode(!handler.getDebugMode());
                debugKeyPressed = true;
            }
        }
        else {
            debugKeyPressed = false;
        }
    }

    private void detectInputMode() {
        if (handler.getKeyManager().up || handler.getKeyManager().down ||
                handler.getKeyManager().left || handler.getKeyManager().right ||
                handler.getKeyManager().space || handler.getKeyManager().escape) {
            if (handler.getInputManager() instanceof GamepadManager) {
                handler.getPadManager().reset();
                handler.setInput(handler.getKeyManager());
                System.out.println("Switched to Keyboard");
            }
        }
        if (handler.getPadManager().up || handler.getPadManager().down ||
                handler.getPadManager().left || handler.getPadManager().right ||
                handler.getPadManager().space) {
            if (handler.getInputManager() instanceof KeyManager) {
                handler.getKeyManager().reset();
                handler.setInput(handler.getPadManager());
                System.out.println("Switched to Gamepad");
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        level.Draw(g);
        overlay.Draw(g);
    }
}