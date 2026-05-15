package PaooGame.States;

import PaooGame.Game.Input.GamepadManager;
import PaooGame.Game.Input.KeyManager;
import PaooGame.Handler;
import PaooGame.States.VisualElements.UI;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Clasa care reprezinta starea principala de joc (PlayState) <br>
 * Gestioneaza logica nivelului, input-ul, ecranul de pauza si tranzitiile intre niveluri
 */
public class PlayState extends State {
    private final UI overlay;

    private boolean initializedFullscreen = false;
    private boolean debugKeyPressed = false;

    private boolean isTransitioning = false;
    private boolean isPaused = false;
    private boolean pausePressedPrev = false;

    private static final Color PAUSE_COLOR = new Color(0, 0, 0, 150);
    private static final Font PAUSE_FONT = new Font("Monospaced", Font.BOLD, 50);
    private static final String PAUSE_TEXT = "PAUSED";

    /**
     * Constructorul starii de joc <br>
     * Initializeaza interfata utilizatorului si verifica existenta unui nivel
     * Daca nivelul nu exista (ex: la inceperea unui joc nou), il creeaza
     *
     * @param handler Handler-ul
     */
    public PlayState(Handler handler) {
        super(handler);
        overlay = new UI(handler);

        if (handler.getLevel() == null) {
            handler.initLevel();
        }
        handler.getLevel().requestRespawn();
    }

    /**
     * Metoda de actualizare a starii de joc <br>
     * Gestioneaza comutarea tipului de input, pauza, progresul nivelului, tranzitiile in caz de victorie si revenirea la meniul principal
     */
    @Override
    public void Update() {
        detectInputMode();

        boolean pausePressedCurr = handler.getInputManager().pause;
        if (pausePressedCurr && !pausePressedPrev) {
            isPaused = !isPaused;
        }
        pausePressedPrev = pausePressedCurr;

        if (!isPaused) {
            if (!initializedFullscreen) {
                handler.getGame().getWnd().setFullScreen(true);
                handler.getGame().getWnd().hideCursor(true);
                initializedFullscreen = true;
            }

            handler.getScoreMan().Update();

            if (handler.getLevel() != null) {
                handler.getLevel().Update();
                handler.getGameCamera().center(handler.getLevel().getEntityManager().getDante());

                if (handler.getLevel().inArena() && handler.getLevel().getBoss() != null && !handler.getLevel().getBoss().isActive()) {
                    if (!isTransitioning) {
                        isTransitioning = true;

                        handler.updateCurrChkp(2);

                        handler.getTransitionManager().start(() -> {
                            handler.setState(new GameDoneState(handler));
                            isTransitioning = false;
                        });
                    }
                }
            }
        }

        if (handler.getInputManager().escape) {
            handler.getGame().getGameCamera().resetToLevelBounds();
            handler.getGame().getWnd().setWindowed();
            handler.getInputManager().reset();

            handler.setState(new MainMenu(handler, "Continue"));
        }

        if (handler.getInputManager().debug) {
            if (!debugKeyPressed) {
                handler.setDebugMode(!handler.getDebugMode());
                debugKeyPressed = true;
            }
        } else {
            debugKeyPressed = false;
        }
    }

    /**
     * Metoda care detecteaza automat dispozitivul de input activ (Tastatura sau Gamepad)
     * si schimba instanta managerului de input in Handler pentru a permite
     * comutarea in timp real
     */
    private void detectInputMode() {
        if (handler.getKeyManager().up || handler.getKeyManager().down ||
                handler.getKeyManager().left || handler.getKeyManager().right ||
                handler.getKeyManager().space || handler.getKeyManager().escape ||
                handler.getKeyManager().pause) {
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

    /**
     * Metoda care deseneaza elementele grafice ale starii curente: nivelul, interfata (UI)
     * si filtrul ecranului de pauza (daca jocul este oprit).
     *
     * @param g Grafica de desenare
     */
    @Override
    public void Draw(Graphics g) {
        if (handler.getLevel() != null) {
            handler.getLevel().Draw(g);
        }

        overlay.Draw(g);

        if (isPaused) {
            g.setColor(PAUSE_COLOR);
            g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

            g.setColor(Color.WHITE);
            g.setFont(PAUSE_FONT);

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(PAUSE_TEXT);

            g.drawString(PAUSE_TEXT, (handler.getWidth() - textWidth) / 2, handler.getHeight() / 2);
        }
    }
}