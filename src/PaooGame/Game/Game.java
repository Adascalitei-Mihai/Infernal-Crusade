package PaooGame.Game;

import PaooGame.Game.Input.KeyManager;
import PaooGame.Game.Input.MouseManager;
import PaooGame.Graphics.Assets;

import PaooGame.Handler;

import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * Clasa principala a intregului proiect.
 */
public class Game implements Runnable
{
    private GameWindow wnd;
    private boolean runState;
    private Thread gameThread;

    private BufferStrategy bs;
    private Graphics g;

    private KeyManager keyMan;
    private MouseManager mouseMan;

    private Handler handler;
    private GameCamera gameCamera;
    /**
     * Constructor cu parametrii, acesta: <br>
     * 1. Indica thread-ului ca inca nu ruleaza. <br>
     * 2. Instantiaza managerii pentru taste si mouse.
     * @param title Titlul jocului.
     * @param width Latimea ferestrei.
     * @param height Inaltimea ferestrei.
     */
    public Game(String title, int width, int height)
    {
        wnd = new GameWindow(title, width, height); // 1
        runState = false;
    }

    /**
     * Functie care initializeaza jocul, aceasta: <br>
     * 1. Creeaza fereastra jocului. <br>
     * 2. Adauga asteptari de evenimente pentru panza ferestrei. <br>
     * 3. Initializeaza componentele vizuale. <br>
     * 4. Seteaza starea la meniul de start.
     */
    private void InitGame()
    {
        wnd = new GameWindow("Infernal Crusaders", 1000, 800); // 1
        wnd.BuildGameWindow();

        wnd.getCanvas().addKeyListener(keyMan); // 2
        wnd.getCanvas().addMouseListener(mouseMan);
        wnd.getCanvas().addMouseMotionListener(mouseMan);

        Assets.Init(); // 3
        handler = new Handler(this);

        gameCamera = new GameCamera(handler, 0, 0);
        keyMan = new KeyManager();
        mouseMan = new MouseManager();
    }

    /**
     * Functie ce va rula in thread-ul creat, aceasta: <br>
     * 1. Initializeaza jocul <br>
     * 2. Retine timpul de la ultimul si la actualul frame. <br>
     * 3. Calculeaza durata unui frame in functie de fps-ul ales. <br>
     * 4. Cat timp jocul ruleaza, cand timpul trecut este suficient pentru a mentine 60 fps, apeleaza Update() si Draw()
     */
    public void run()
    {
        InitGame(); // 1

        long oldTime = System.nanoTime(); // 2
        long curentTime;

        final int framesPerSecond = 60; // 3
        final double timeFrame = (double) 1000000000 / framesPerSecond;

        while (runState) // 4
        {
            curentTime = System.nanoTime();

            if((curentTime - oldTime) > timeFrame)
            {
                Update();
                Draw();

                oldTime = curentTime;
            }
        }

    }

    /**
     * Functie care creaza si porneste thread-ul separat. <br>
     * Metoda trebuie sa fie declarata synchronized pentru ca apelul acesteia sa fie semaforizat. <br>
     * Verifica daca jocul nu este inceput, creaza si lanseaza in executie thread-ul care va apela run()
     */
    public synchronized void StartGame()
    {
        if(!runState)
        {
            runState = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /**
     * Functie care opreste thread-ul si inchide fereastra. <br>
        Metoda trebuie sa fie declarata synchronized pentru ca apelul acesteia sa fie semaforizat.
     */
    public synchronized void StopGame() {
        if(!runState) {
            return;
        }
        runState = false;

        if(wnd != null && wnd.getFrame() != null) {
            wnd.getFrame().dispose();
        }

        handler.StopGame();
    }

    /**
     * Functie care actualizeaza jocul, prin ascultari noi de tastatura si cere actualizari de la starea curenta.
     */
    private void Update() {
        keyMan.Update();
        handler.Update();
    }

    /**
     * Functie care deseneaza jocul, aceasta: <br>
     * 1. Creaza strategia in prima iteratie. <br>
     * 2. Creaza grafica jocului pentru cel mai din spate buffer. <br>
     * 3. Stergem tot ce a fost anterior. <br>
     * 4. Cere un desen de la starea curenta. <br>
     * 5. Bufferul din spate este trimis spre monitor. <br>
     * 6. Eliberam grafica folosita pentru a desena.
     */
    private void Draw() {
        bs = wnd.getCanvas().getBufferStrategy();
        if(bs == null) { // 1
            try {
                wnd.getCanvas().createBufferStrategy(3);
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        g = bs.getDrawGraphics(); // 2
        g.clearRect(0, 0, wnd.getWndWidth(), wnd.getWndHeight()); // 3

        handler.Draw(g);

        bs.show(); // 5
        g.dispose(); // 6
    }

    /**
     * Getter pentru managerul de taste.
     * @return Managerul de taste actual.
     */
    public KeyManager getKeyMan() { return keyMan; }

    /**
     * Getter pentru managerul de mouse.
     * @return Managerul de mouse actual.
     */
    public MouseManager getMouseMan() { return mouseMan; }

    /**
     * Getter pentru latimea ferestrei.
     * @return Latimea ferestrei actuale.
     */
    public int getWidth() { return wnd.getWndWidth(); }

    /**
     * Getter pentru inaltimea ferestrei.
     * @return Inaltimea ferestrei actuale.
     */
    public int getHeight() {
        return wnd.getWndHeight();
    }

    public GameWindow getWnd() { return wnd; }

    public GameCamera getGameCamera() {
        return gameCamera;
    }

    public void setGameCamera(GameCamera gameCamera) {
        this.gameCamera = gameCamera;
    }
}