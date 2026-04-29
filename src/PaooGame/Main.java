package PaooGame;

import PaooGame.Game.Game;

public class Main
{
    public static void main(String[] args)
    {

        Game paooGame = new Game("PaooGame", 1000, 600);
        paooGame.StartGame();
    }
}
