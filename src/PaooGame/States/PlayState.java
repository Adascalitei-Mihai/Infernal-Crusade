package PaooGame.States;

import PaooGame.Handler;
import PaooGame.World.Factories.Level1Factory;
import PaooGame.World.Levels.Level;
import PaooGame.World.Levels.Level1;

import java.awt.*;

public class PlayState extends State {
    private Level level;
    private boolean initializedFullscreen = false;
    private boolean debugKeyPressed = false;

    public PlayState(Handler handler) {
        super(handler);

        Level1Factory level1Factory = new Level1Factory(handler);

        level = new Level1(handler);
    }


    @Override
    public void Update() {
        if (!initializedFullscreen) {
            handler.getGame().getWnd().setFullScreen(true);
            handler.getGame().getWnd().hideCursor(true);
            initializedFullscreen = true;
        }

        level.Update();
        handler.getGameCamera().center(level.getEntityManager().getDante());

        if(handler.getKeyManager().escape) {
            handler.getGame().getWnd().setWindowed();
            handler.getKeyManager().resetKeys();
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

    @Override
    public void Draw(Graphics g) {
        level.Draw(g);
    }
}