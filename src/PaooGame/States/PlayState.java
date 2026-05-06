package PaooGame.States;

import PaooGame.Audio.SongAssets;
import PaooGame.Game.Input.GamepadManager;
import PaooGame.Game.Input.KeyManager;
import PaooGame.Handler;
import PaooGame.States.AudioElements.AudioManager;
import PaooGame.States.VisualElements.UI;
import PaooGame.World.Levels.Level;
import PaooGame.World.Levels.Level1;
import PaooGame.World.Levels.Level2;

import java.awt.*;

public class PlayState extends State {
    private UI overlay;
    private boolean initializedFullscreen = false;
    private boolean debugKeyPressed = false;

    // FLAG VITAL PENTRU TRANZIȚIE!
    private boolean isTransitioning = false;

    // FLAG-URI PENTRU PAUZĂ
    private boolean isPaused = false;
    private boolean pausePressedPrev = false;

    public PlayState(Handler handler) {
        super(handler);
        overlay = new UI(handler);

        // Dacă nu avem un nivel creat încă (ex: abia am dat Play din Meniu)
        if (handler.getLevel() == null) {
            handler.initLevel();
        }
    }

    @Override
    public void Update() {
        detectInputMode();

        // --- LOGICA DE TOGGLE PAUZĂ PE TASTA 'P' ---
        // Presupunem că în InputManager ai adăugat o variabilă booleană 'pause' mapată pe tasta P
        boolean pausePressedCurr = handler.getInputManager().pause;

        if (pausePressedCurr && !pausePressedPrev) {
            isPaused = !isPaused; // Inversăm starea de pauză
        }
        pausePressedPrev = pausePressedCurr;

        // --- UPDATE-UL EFECTIV (doar dacă NU e pauză) ---
        if (!isPaused) {
            if (!initializedFullscreen) {
                handler.getGame().getWnd().setFullScreen(true);
                handler.getGame().getWnd().hideCursor(true);
                initializedFullscreen = true;
            }

            handler.getScoreMan().Update();

            // Protecție suplimentară: Update doar dacă nivelul s-a încărcat corect
            if (handler.getLevel() != null) {
                handler.getLevel().Update();
                handler.getGameCamera().center(handler.getLevel().getEntityManager().getDante());

                // --- LOGICA DE TRANZIȚIE ---
                if (handler.getLevel().inArena() && handler.getLevel().getBoss() != null && !handler.getLevel().getBoss().isActive()) {

                    // Dacă șeful e mort și NU suntem deja în tranziție
                    if (!isTransitioning) {
                        isTransitioning = true; // Setăm flag-ul pe true ca să nu mai intre a 2-a oară!

                        // Salvăm scorul și ultimul checkpoint
                        handler.updateCurrChkp(3);

                        // Salvăm trecerea la nivelul 2
                        handler.updateCurrLevel(2);

                        // Începem tranziția
                        handler.getTransitionManager().start(() -> {
                            handler.initLevel(); // Creează automat Level2 pentru că tocmai am făcut currLevel = 2
                            isTransitioning = false; // Resetăm flag-ul odată ce suntem în Nivelul 2
                        });
                    }
                }
            }
        } // Aici se termină blocul if (!isPaused)

        // Ieșirea în meniul principal (ESC) funcționează și dacă jocul e în pauză
        if(handler.getInputManager().escape) {
            handler.getGame().getGameCamera().resetToLevelBounds();
            handler.getGame().getWnd().setWindowed();
            handler.getInputManager().reset();

            handler.setState(new MainMenu(handler, "Continue"));
        }

        // Debug mode funcționează și în pauză
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
                handler.getKeyManager().space || handler.getKeyManager().escape ||
                handler.getKeyManager().pause) { // Poți adăuga și verificarea pentru 'pause' aici
            if (handler.getInputManager() instanceof GamepadManager) {
                handler.getPadManager().reset();
                handler.setInput(handler.getKeyManager());
            }
        }
        if (handler.getPadManager().up || handler.getPadManager().down ||
                handler.getPadManager().left || handler.getPadManager().right ||
                handler.getPadManager().space || handler.getPadManager().pause) {
            if (handler.getInputManager() instanceof KeyManager) {
                handler.getKeyManager().reset();
                handler.setInput(handler.getPadManager());
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        // 1. Desenăm jocul propriu-zis
        if (handler.getLevel() != null) {
            handler.getLevel().Draw(g);
        }

        // 2. Desenăm interfața (bare de viață, scor etc.)
        overlay.Draw(g);

        // 3. Dacă e pauză, aplicăm filtrul vizual peste tot ecranul
        if (isPaused) {
            // Desenăm un dreptunghi negru, transparent (150 = nivelul de opacitate din 255)
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

            // Desenăm textul centrat
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 50));

            String pauseText = "PAUSED";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(pauseText);

            // Centrarea precisă a textului
            g.drawString(pauseText, (handler.getWidth() - textWidth) / 2, handler.getHeight() / 2);
        }
    }

    public boolean isTransitioning() { return isTransitioning; }
}