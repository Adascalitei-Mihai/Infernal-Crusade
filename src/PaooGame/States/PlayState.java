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
            // În loc de arena.isFinished() direct, folosim getter-ul tău din Level (dacă există)
            // sau ce metodă folosești tu pentru a verifica finalul
            if (handler.getLevel().inArena() && /* condiția ta de win */ handler.getLevel().getBoss() != null && !handler.getLevel().getBoss().isActive()) {

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

        if(handler.getInputManager().escape) {
            handler.getGame().getGameCamera().resetToLevelBounds();
            handler.getGame().getWnd().setWindowed();
            handler.getInputManager().reset();

            handler.setState(new MainMenu(handler, "Continue"));
        }

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
        // ... Logica ta de input rămâne perfect identică ...
        if (handler.getKeyManager().up || handler.getKeyManager().down ||
                handler.getKeyManager().left || handler.getKeyManager().right ||
                handler.getKeyManager().space || handler.getKeyManager().escape) {
            if (handler.getInputManager() instanceof GamepadManager) {
                handler.getPadManager().reset();
                handler.setInput(handler.getKeyManager());
            }
        }
        if (handler.getPadManager().up || handler.getPadManager().down ||
                handler.getPadManager().left || handler.getPadManager().right ||
                handler.getPadManager().space) {
            if (handler.getInputManager() instanceof KeyManager) {
                handler.getKeyManager().reset();
                handler.setInput(handler.getPadManager());
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (handler.getLevel() != null) {
            handler.getLevel().Draw(g);
        }
        overlay.Draw(g);
    }

    public boolean isTransitioning() { return isTransitioning; }
}