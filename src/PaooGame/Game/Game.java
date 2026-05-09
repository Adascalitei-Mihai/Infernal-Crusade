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
 * Clasa principala a intregului proiect
 * Implementeaza Runnable pentru folosirea thread-urilor
 */
public class Game implements Runnable
{
    private Handler handler;
    private GameWindow wnd;
    private GameCamera gameCamera;
    private Graphics g;
    /**
     * Campul care este un triple buffer strategy, folosit si initializat de {@link #renderSimpleLoadingScreen()} si {@link #Draw()}
     */
    private BufferStrategy bs;

    private InputManager inputMan;
    private KeyManager keyMan;
    private GamepadManager padMan;
    private MouseManager mouseMan;

    private final static int MAX_FPS = 60;
    private int currentFPS;
    private boolean runState;

    /**
     * Campul care retine daca thread-urile de incarcare si-au finalizat executia
     */
    private volatile boolean assetsLoaded;

    /**
     * Metoda care initializeaza jocul <br>
     * 1. Creeaza fereastra jocului <br>
     * 2. Lanseaza thread-urile pentru incarcarea video si audio <br>
     * 3. Lanseaza un thread care asigura legaturile corecte:
     * initializeaza field-urile si creeaza conexiunile intre fereastra si input
     */
    private void initGame() {
        wnd = new GameWindow("Infernal Crusaders", 1000, 800);
        wnd.BuildGameWindow(); // 1

        Thread vThread = new Thread(new VisualAssets(), "Visual-Loader");
        Thread aThread = new Thread(new SongAssets(), "Audio-Loader");

        vThread.start();
        aThread.start(); // 2

        new Thread(() -> {
            try {
                vThread.join();
                aThread.join();

                handler = new Handler(this);
                gameCamera = new GameCamera(handler, 0, 0);
                keyMan = new KeyManager(handler);
                padMan = new GamepadManager(handler);
                mouseMan = new MouseManager();
                inputMan = keyMan;

                wnd.getCanvas().addKeyListener(keyMan);
                wnd.getCanvas().addMouseListener(mouseMan);
                wnd.getCanvas().addMouseMotionListener(mouseMan);

                assetsLoaded = true;
                System.out.println("[SYNC_THREAD]: Resurse incarcate asincron!");
            } catch (Exception e) {
                System.err.println("[EROARE]: Incarcare asincrona: " + e.getMessage());
            }
        }, "Sync-Thread").start(); // 3
    }

    /**
     * Metoda care ruleaza jocul <br>
     * 1. Initializeaza jocul <br>
     * 2. Mentine jocul la {@link #MAX_FPS} <br>
     * 3. Aplica {@link #Update()} si {@link #Draw()} daca incarcarea a fost realizata, altfel {@link #renderSimpleLoadingScreen()}
     */
    public void run() {
        initGame(); // 1

        long lastTime = System.nanoTime();
        long timer = 0;
        int ticks = 0;

        final double timeFrame = (double) 1000000000 / MAX_FPS;
        double delta = 0;

        while (runState) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;
            delta += elapsed / timeFrame;
            timer += elapsed;
            lastTime = now;

            if (delta >= 1) {
                if (assetsLoaded) { // 3
                    Update();
                    Draw();
                } else {
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
        } // 2
    }

    /**
     * Metoda care initializeaza {@link #bs}
     */
    private void initBs() {
        if (bs == null) {
            bs = wnd.getCanvas().getBufferStrategy();
            if (bs == null) {
                try {
                    wnd.getCanvas().createBufferStrategy(3);
                } catch (Exception e) {
                    System.err.println("[EROARE]: Afisarea ecranului de incarcare: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Metoda care afiseaza ecranul de incarcare
     * Foloseste {@link VisualAssets#loadPercent}, {@link SongAssets#loadPercent} pentru a obtine raportul de completare din
     * {@link VisualAssets#TOTAL_LOAD} si {@link SongAssets#TOTAL_LOAD}
     */
    private void renderSimpleLoadingScreen() {
        initBs();
        if (bs == null) {
            return;
        }
        g = bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, wnd.getWndWidth(), wnd.getWndHeight());

        int barWidth = 400;
        int barHeight = 30;
        int x = (wnd.getWndWidth() - barWidth) / 2;
        int y = (wnd.getWndHeight() - barHeight) / 2;

        float progress = (float) (VisualAssets.loadPercent + SongAssets.loadPercent) / (VisualAssets.TOTAL_LOAD + SongAssets.TOTAL_LOAD);
        progress = Math.min(1.0f, Math.max(0.0f, progress));

        g.setColor(new Color(50, 50, 50));
        g.fillRect(x, y, barWidth, barHeight);

        g.setColor(new Color(255, 140, 0));
        g.fillRect(x, y, (int) (barWidth * progress), barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, barWidth, barHeight);


        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        String status = "AWAKENING DANTE: " + (int)(progress * 100) + "%";
        int textWidth = g.getFontMetrics().stringWidth(status);
        g.drawString(status, (wnd.getWndWidth() - textWidth) / 2, y + barHeight + 25);

        bs.show();
        g.dispose();
    }

    /**
     * Metoda care lanseaza thread-ul jocului
     */
    public synchronized void StartGame()
    {
        if(!runState)
        {
            runState = true;
            new Thread(this).start();
        }
    }

    /**
     * Metoda care opreste thread-ul <br>
     * 1. Inchide {@link #wnd} si {@link #padMan} <br>
     * 2. Notifica {@link #handler}
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
     * Metoda care actualizeaza {@link #keyMan}, {@link #padMan} si {@link #handler}
     */
    private void Update() {
        keyMan.Update();
        padMan.Update();
        handler.Update();
    }

    /**
     * Metoda care creeaza {@link #g}, o trimite catre {@link #handler}, o afiseaza si apoi o distruge pentru urmatoarea iteratie
     */
    private void Draw() {
        initBs();
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, wnd.getWndWidth(), wnd.getWndHeight());

        handler.Draw(g);

        bs.show();
        g.dispose();
    }

    public KeyManager getKeyMan() { return keyMan; }
    public GamepadManager getPadMan() { return padMan; }
    public InputManager getInputMan() { return inputMan; }
    public void setInput(InputManager inputMan) { this.inputMan = inputMan; }
    public MouseManager getMouseMan() { return mouseMan; }
    public int getWidth() { return wnd.getWndWidth(); }
    public int getHeight() { return wnd.getWndHeight(); }
    public GameWindow getWnd() { return wnd; }
    public GameCamera getGameCamera() { return gameCamera; }
    public int getCurrentFPS() { return currentFPS; }
}