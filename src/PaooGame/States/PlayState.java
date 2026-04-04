package PaooGame.States;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;
import PaooGame.World.Factories.Level1Factory;
import PaooGame.World.Levels.Level;
import PaooGame.World.Levels.Level1;

import java.awt.*;

public class PlayState extends State {
    private Level level;

    public PlayState(Handler handler) {
        super(handler);

        Level1Factory level1Factory = new Level1Factory(handler);

        // 2. Construim nivelul (care va rula loadLevel intern)
        level = new Level1(handler);
    }


    @Override
    public void Update() {
        level.Update();

        handler.getGameCamera().center(level.getEntityManager().getDante());
    }

    @Override
    public void Draw(Graphics g) {
        level.Draw(g);
    }
}