package PaooGame.Game;

import PaooGame.Audio.SongAssets;
import PaooGame.Game.Input.GamepadManager;
import PaooGame.Game.Input.InputManager;
import PaooGame.Game.Input.KeyManager;
import PaooGame.Game.Input.MouseManager;
import PaooGame.Graphics.VisualAssets;

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
    private int currentFPS = 0;

    private BufferStrategy bs;
    private Graphics g;

    private InputManager inputMan;
    private KeyManager keyMan;
    private GamepadManager padMan;
    private MouseManager mouseMan;

    private Handler handler;
    private GameCamera gameCamera;

    private volatile boolean assetsLoaded = false;

    /**
     * Functie care initializeaza jocul, aceasta: <br>
     * 1. Creeaza fereastra jocului. <br>
     * 2. Adauga asteptari de evenimente pentru panza ferestrei. <br>
     * 3. Initializeaza componentele vizuale. <br>
     * 4. Seteaza starea la meniul de start.
     */
    private void InitGame() {
        wnd = new GameWindow("Infernal Crusaders", 1000, 800);
        wnd.BuildGameWindow();

        Thread vThread = new Thread(new VisualAssets(), "Visual-Loader");
        Thread aThread = new Thread(new SongAssets(), "Audio-Loader");

        vThread.start();
        aThread.start();

        new Thread(() -> {
            try {
                vThread.join();
                aThread.join();

                handler = new Handler(this);
                gameCamera = new GameCamera(handler, 0, 0);
                keyMan = new KeyManager(handler);
                padMan = new GamepadManager(handler);
                mouseMan = new MouseManager(handler);
                inputMan = keyMan;

                wnd.getCanvas().addKeyListener(keyMan);
                wnd.getCanvas().addMouseListener(mouseMan);
                wnd.getCanvas().addMouseMotionListener(mouseMan);

                assetsLoaded = true;
                System.out.println("Resurse incarcate asincron!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Sync-Thread").start();
    }

    /**
     * Functie ce va rula in thread-ul creat, aceasta: <br>
     * 1. Initializeaza jocul <br>
     * 2. Retine timpul de la ultimul si la actualul frame. <br>
     * 3. Calculeaza durata unui frame in functie de fps-ul ales. <br>
     * 4. Cat timp jocul ruleaza, cand timpul trecut este suficient pentru a mentine 60 fps, apeleaza Update() si Draw()
     */
    public void run() {
        InitGame();

        long lastTime = System.nanoTime();
        long timer = 0;
        int ticks = 0;

        final int framesPerSecond = 60;
        final double timeFrame = (double) 1000000000 / framesPerSecond;
        double delta = 0;

        while (runState) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;
            delta += elapsed / timeFrame;
            timer += elapsed;
            lastTime = now;

            if (delta >= 1) {
                if (assetsLoaded) {
                    Update();
                    Draw();
                }
                else {
                    renderSimpleLoadingScreen();
                }
                ticks++;
                delta--;
            }

            if (timer >= 1000000000) {
                currentFPS = ticks;
                ticks = 0;
                timer = 0;
            }
        }
    }

    private void renderSimpleLoadingScreen() {
        bs = wnd.getCanvas().getBufferStrategy();
        if (bs == null) {
            wnd.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, wnd.getWndWidth(), wnd.getWndHeight());

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, wnd.getWndWidth(), wnd.getWndHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Incarcare...", wnd.getWndWidth() / 2 - 100, wnd.getWndHeight() / 2);

        bs.show();
        g.dispose();
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

        if(padMan != null) {
            padMan.terminate();
        }

        handler.StopGame();
    }

    /**
     * Functie care actualizeaza jocul, prin ascultari noi de tastatura si cere actualizari de la starea curenta.
     */
    private void Update() {
        keyMan.Update();
        padMan.Update();
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

    public GamepadManager getPadMan() { return padMan; }
    public InputManager getInputMan() { return inputMan; }
    public void setInput(InputManager inputMan) { this.inputMan = inputMan; }
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

    public int getCurrentFPS() {
        return currentFPS;
    }
}