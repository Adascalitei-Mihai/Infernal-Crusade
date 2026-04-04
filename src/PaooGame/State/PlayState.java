package PaooGame.State;

import PaooGame.Graphics.Assets;
import PaooGame.Handler;

import java.awt.*;

public class PlayState extends State {
    public PlayState(Handler handler) {
        super(handler);
    }


    @Override
    public void Update() {
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 800, 600);

        int groundY = 400;
        g.setColor(Color.RED);
        g.drawLine(0, groundY, 800, groundY);

        if (Assets.Dante != null) {
            g.drawImage(Assets.Dante.get("Death")[3], 50, groundY - 137 + 5, null);
        }
    }
}