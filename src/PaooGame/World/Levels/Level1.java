package PaooGame.World.Levels;

import PaooGame.Handler;
import PaooGame.World.Factories.EntityFactory;
import PaooGame.World.Factories.Level1Factory;

public class Level1 extends Level {
    private final EntityFactory factory;
    private final static int L1_WIDTH = 500;
    private final static int L1_HEIGHT = 29;

    public Level1(Handler handler) {
        super(handler, L1_WIDTH, L1_HEIGHT, 0, 0, "res/textures/Maps/L1/L1.tmx");
        factory = new Level1Factory(handler);
    }
}
