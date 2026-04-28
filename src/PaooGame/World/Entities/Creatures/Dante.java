package PaooGame.World.Entities.Creatures;

import PaooGame.Handler;
import PaooGame.World.Entities.Entity;
import PaooGame.World.Tiles.Tile;
import PaooGame.Graphics.Animation; // IMPORT NOU
import PaooGame.Graphics.Assets;    // IMPORT NOU

import java.awt.*;

import static PaooGame.World.Tiles.Tile.TILE_SIZE;

public class Dante extends Creature {
    public static final int DEFAULT_WIDTH = 2 * TILE_SIZE;
    public static final int DEFAULT_HEIGHT = 4 * TILE_SIZE;

    public static final float MAX_SPEED = 15f;
    public static final float SPEED = 5f;
    private static final float ACCELERATION = 0.2f;
    private static final float FRICTION = 0.6f;

    private static final float GRAVITY = 0.5f;
    private static final float JUMP_IMPULSE = -10.0f;
    private static final float JUMP_ADDAGE = -0.6f;
    private static final int MAX_JUMP_TIME = 20;

    private static final int ICE_TIMER = 600, FIRE_TIMER = 600;
    private int direction = 2;

    private boolean isDying = false;
    private int deathTimer = 0;

    // --- VARIABILE EDEN SHIELD ---
    private boolean edenShieldCasting = false; // Faza 1: Se formează scutul
    private boolean edenShieldActive = false;  // Faza 2: Scutul e activ și te protejează

    private int edenShieldTimer = 0; // Timpul cât rulează animația de formare
    private final int EDEN_SHIELD_CAST_DURATION = 42; // ~42 de tick-uri (aprox. 7 poze * 100ms)

    private int edenActiveTimer = 0; // Timpul cât stă scutul pe tine
    private final int EDEN_SHIELD_DURATION; // Setat în funcție de dificultate

    private int edenCooldownTimer = 0; // Timpul de așteptare până la următoarea folosire
    private static final int EDEN_SHIELD_COOLDOWN = 300;

    private Rectangle edenShieldBounds = new Rectangle();

    private static final int FIERY_JUDGEMENT_COOLDOWN = 600;
    private static final int FIERY_JUDGEMENT_SPREAD = 40;
    private int fieryJudgementTimer = 0;

    private int jumpTimer = 0;
    private boolean isJumping;
    private boolean onGround = false;

    private static final int ATTACK_COOLDOWN = 30;
    private static final int ATTACK_DURATION = 12;
    private int attackTimer = 0;
    private int attackActiveTimer = 0;

    private boolean isAttacking = false;
    private Rectangle attackBounds = new Rectangle();

    // ==========================================
    // --- VARIABILE NOI PENTRU ANIMAȚII ---
    // ==========================================
    public static final int IDLE = 0;
    public static final int RUN = 1;
    public static final int JUMP = 2;
    public static final int FALL = 3;
    public static final int ATTACK1 = 4;
    public static final int ATTACK2 = 5;
    public static final int HIT = 6;
    public static final int DEATH = 7;
    public static final int ICE_MORPH = 8;
    public static final int EDEN_SHIELD = 9;
    public static final int FIERY_JUDGEMENT = 10;

    private final Animation[] animations = new Animation[11];
    private int currentState = IDLE;
    // ==========================================

    public Dante(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, 1000);

        EDEN_SHIELD_DURATION = switch (handler.getDifficulty()) {
            case 0 -> 20;
            case 2 -> 8;
            default -> 15;
        };

        bounds.x = 4;
        bounds.width = DEFAULT_WIDTH - 8;

        bounds.y = 1;
        bounds.height = DEFAULT_HEIGHT - 2;

        // ==========================================
        // --- INIȚIALIZARE ANIMAȚII ---
        // ==========================================
        animations[IDLE] = new Animation(100, Assets.Dante.get("Idle"));
        animations[RUN] = new Animation(80, Assets.Dante.get("Run"));
        animations[JUMP] = new Animation(100, Assets.Dante.get("Jump"));
        animations[FALL] = new Animation(100, Assets.Dante.get("Fall"));
        animations[ATTACK1] = new Animation(80, Assets.Dante.get("Attack1"));
        animations[ATTACK2] = new Animation(80, Assets.Dante.get("Attack2"));
        animations[HIT] = new Animation(100, Assets.Dante.get("Hit"));
        animations[DEATH] = new Animation(150, Assets.Dante.get("Death"));
        animations[ICE_MORPH] = new Animation(100, Assets.Dante.get("Ice_Morph"));
        animations[EDEN_SHIELD] = new Animation(100, Assets.Dante.get("Eden_Shield"));
        animations[FIERY_JUDGEMENT] = new Animation(100, Assets.Dante.get("Fiery_Judgement"));
    }

    @Override
    public void Update() {
        // --- BLOCUL DE MOARTE ---
        if (isDying) {
            currentState = DEATH;         // Setăm starea pe animația de moarte
            animations[DEATH].tick();     // Avansăm cadrele

            deathTimer--;
            if (deathTimer <= 0) {
                isDying = false;          // S-a terminat animația
                handler.getLevel().spawn(); // Resetăm nivelul
            }
            return; // `return` oprește Update-ul aici. Dante nu se mai poate mișca.
        }
        // ------------------------

        // ... de aici în jos rămâne exact codul tău normal
        applyGravity();
        getDir();

        move();
        jump();
        attack();
        iceMorph();
        edenShield();
        fieryJudgement();

        updateAnimationState();
        animations[currentState].tick();
    }

    // ==========================================
    // --- METODĂ NOUĂ: SCHIMBĂ ANIMAȚIA ---
    // ==========================================
    private void updateAnimationState() {
        // Prioritate 1: Atacul și magiile
        if (isAttacking) {
            // Dacă atacă în sus (direction == 1), folosește Attack1. Altfel, Attack2.
            if (direction == 1) {
                currentState = ATTACK1;
            } else {
                currentState = ATTACK2;
            }
        }
        else if (edenShieldCasting || edenShieldActive) {
            // Rulează animația de scut pe toată perioada formării și cât e activ
            currentState = EDEN_SHIELD;
        }
        // Prioritate 2: Săritul și căderea
        else if (yMove < 0) {
            currentState = JUMP;
        }
        else if (yMove > 0 && !onGround) {
            currentState = FALL;
        }
        // Prioritate 3: Mersul
        else if (Math.abs(xMove) > 0.2f) {
            currentState = RUN;
        }
        // Default: Stă pe loc
        else {
            currentState = IDLE;
        }
    }

    private void applyGravity() {
        yMove += GRAVITY;
        if (y + DEFAULT_HEIGHT >= (handler.getLevel().getHeight() - 1) * TILE_SIZE) {
            die();
        }
    }

    private void getDir() {
        if (handler.getKeyManager().up) {
            direction = 1;
        }
        else if (handler.getKeyManager().left) {
            direction = 2;
        }
        else if (handler.getKeyManager().right) {
            direction = 0; // Setăm direcția 0 explicit pentru dreapta
        }
    }

    private boolean isOnIce() {
        int tx = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
        int ty = (int) (y + bounds.y + bounds.height + 8) / TILE_SIZE;
        return handler.getLevel().isIce(tx, ty);
    }

    @Override
    public void moveX() {
        if (!isOnIce()) {
            if (handler.getKeyManager().right) {
                xMove = SPEED;
            }
            else if (handler.getKeyManager().left) {
                xMove = -SPEED;
            }
            else {
                xMove *= FRICTION;
            }
        }
        else {
            if (handler.getKeyManager().right) {
                xMove += ACCELERATION;
                if (xMove < SPEED) xMove = SPEED;
                if (xMove > MAX_SPEED) xMove = MAX_SPEED;
            }
            else if (handler.getKeyManager().left) {
                xMove -= ACCELERATION;
                if (xMove > -SPEED) xMove = -SPEED;
                if (xMove < -MAX_SPEED) xMove = -MAX_SPEED;
            }
            else {
                xMove *= FRICTION;
            }
        }

        super.moveX();
    }

    @Override
    public void moveY() {
        if (yMove < 0) {
            int ty = (int) (y + yMove + bounds.y) / TILE_SIZE;
            if (!collisionWithTile((int) (x + bounds.x) / TILE_SIZE, ty) &&
                    !collisionWithTile((int) (x + bounds.x + bounds.width) / TILE_SIZE, ty)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE + TILE_SIZE - bounds.y;
                yMove = 0;
            }
        }
        else if (yMove > 0) {
            int ty = (int) (y + yMove + bounds.y + bounds.height) / TILE_SIZE;
            if (!collisionWithTile((int) (x + bounds.x) / TILE_SIZE, ty) &&
                    !collisionWithTile((int) (x + bounds.x + bounds.width) / TILE_SIZE, ty)) {
                y += yMove;
            }
            else {
                y = ty * TILE_SIZE - bounds.y - bounds.height - 1;
                onGround = true;
                yMove = 0;
            }
        }
    }

    private void jump() {
        if(handler.getKeyManager().space && onGround) {
            yMove = JUMP_IMPULSE;
            onGround = false;
            isJumping = true;
            jumpTimer = 0;
        }

        if (handler.getKeyManager().space && isJumping) {
            if (jumpTimer < MAX_JUMP_TIME) {
                yMove += JUMP_ADDAGE;
                jumpTimer++;
            }
            else {
                isJumping = false;
            }
        }

        if (!handler.getKeyManager().space) {
            isJumping = false;
        }
    }

    private void attack() {
        if (attackTimer > 0) {
            attackTimer--;
        }

        if (isAttacking) {
            attackActiveTimer--;
            if (attackActiveTimer <= 0) {
                isAttacking = false;
                return;
            }
        }

        if (handler.getKeyManager().j && attackTimer <= 0) {
            isAttacking = true;
            attackActiveTimer = ATTACK_DURATION;
            attackTimer = ATTACK_COOLDOWN;
        }

        if (isAttacking) {
            int vWidth = (int) (bounds.width * 1.5f);
            int vHeight = TILE_SIZE * 2;
            int hWidth = TILE_SIZE * 2;
            int hHeight = (int) (bounds.height * 0.5f);

            switch (direction) {
                case 0 -> {
                    attackBounds.setSize(hWidth, hHeight);
                    attackBounds.x = (int) (x + bounds.x + bounds.width);
                    attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);
                }
                case 1 -> {
                    attackBounds.setSize(vWidth, vHeight);
                    attackBounds.x = (int) (x + bounds.x - (float) (vWidth - bounds.width) / 2);
                    attackBounds.y = (int) (y + bounds.y - vHeight);
                }
                case 2 -> {
                    attackBounds.setSize(hWidth, hHeight);
                    attackBounds.x = (int) (x + bounds.x - hWidth);
                    attackBounds.y = (int) (y + bounds.y + (float) (bounds.height - hHeight) / 2);
                }
            }
        }
    }

    private void iceMorph() {
        if (handler.getKeyManager().shift) {

            getDir();
            int centerX = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height) / TILE_SIZE;

            int startX, endX;

            if (direction == 0 || direction == 1) {
                startX = centerX - 2;
                endX = centerX + 5;
            }
            else {
                startX = centerX - 5;
                endX = centerX + 2;
            }

            int startY = centerY - 1;
            int endY = centerY + 2;

            for (int tx = startX; tx <= endX; tx++) {
                for (int ty = startY; ty <= endY; ty++) {
                    if (tx >= 0 && tx < handler.getLevel().getWidth() &&
                            ty >= 0 && ty < handler.getLevel().getHeight()) {
                        handler.getLevel().setIce(tx, ty, ICE_TIMER);
                    }
                }
            }
        }
    }

    private void edenShield() {
        // 1. Scădem timpul de Cooldown
        if (edenCooldownTimer > 0) {
            edenCooldownTimer--;
        }

        // 2. Faza de formare a scutului (Casting)
        if (edenShieldCasting) {
            edenShieldTimer--; // Aici scade timpul "de la prima animație până la ultima"

            if (edenShieldTimer <= 0) {
                edenShieldCasting = false; // S-a terminat animația de formare
                edenShieldActive = true;   // Acum scutul te protejează oficial!
                edenActiveTimer = EDEN_SHIELD_DURATION; // Începe timer-ul dat de dificultate
            }
        }
        // 3. Faza în care scutul este activ
        else if (edenShieldActive) {
            edenActiveTimer--;

            int padding = 30;
            edenShieldBounds.x = (int) (x + bounds.x - padding);
            edenShieldBounds.y = (int) (y + bounds.y - padding);
            edenShieldBounds.width = bounds.width + (padding * 2);
            edenShieldBounds.height = bounds.height + (padding * 2);

            if (edenActiveTimer <= 0) {
                edenShieldActive = false; // S-a consumat scutul
            }
        }

        // 4. Când apeși K să activezi scutul
        if (handler.getKeyManager().k && edenCooldownTimer <= 0 && onGround && !edenShieldCasting && !edenShieldActive) {
            edenShieldCasting = true; // Începe formarea
            edenShieldTimer = EDEN_SHIELD_CAST_DURATION; // Setăm timpul de animație
            edenCooldownTimer = EDEN_SHIELD_COOLDOWN; // Resetăm cooldown-ul global
        }
    }

    private void fieryJudgement() {
        if (fieryJudgementTimer > 0) {
            fieryJudgementTimer--;
        }

        if (handler.getKeyManager().l && fieryJudgementTimer <= 0) {
            int centerX = (int) (x + bounds.x + (float) bounds.width / 2) / TILE_SIZE;
            int centerY = (int) (y + bounds.y + bounds.height + 1) / TILE_SIZE;

            spreadFire(centerX, centerY, FIERY_JUDGEMENT_SPREAD);

            fieryJudgementTimer = FIERY_JUDGEMENT_COOLDOWN;
        }
    }

    private void spreadFire(int tx, int ty, int remainingSteps) {
        if (remainingSteps <= 0) {
            return;
        }

        Tile t = handler.getLevel().getTile(tx, ty);
        if (t == null || !t.isSolid()) return;

        if (handler.getLevel().isFire(tx, ty)) return;

        handler.getLevel().setFire(tx, ty, FIRE_TIMER);

        spreadFire(tx + 1, ty, remainingSteps - 1);
        spreadFire(tx - 1, ty, remainingSteps - 1);
        spreadFire(tx, ty + 1, remainingSteps - 1);
        spreadFire(tx, ty - 1, remainingSteps - 1);
    }

    @Override
    public void Draw(Graphics g) {
        int screenX = (int) (x - handler.getGameCamera().getxOffset());
        int screenY = (int) (y - handler.getGameCamera().getyOffset());

        // ==========================================
        // --- DESENAREA ANIMAȚIEI ---
        // ==========================================
        int imgWidth = 184;
        int imgHeight = 137;

        // Calculăm poziția ca imaginea să fie centrată pe hitbox-ul jucătorului
        // (Aliniem la baza hitbox-ului ca picioarele să fie pe pământ)
        int drawX = screenX + (DEFAULT_WIDTH - imgWidth) / 2;
        int drawY = screenY + (DEFAULT_HEIGHT - imgHeight);

        // Dacă personajul se uită în stânga (direction == 2), întoarcem imaginea (flip orizontal)
        if (direction == 2) {
            g.drawImage(animations[currentState].getCurrentFrame(),
                    drawX + imgWidth, drawY, -imgWidth, imgHeight, null);
        } else {
            // Dacă se uită în dreapta, o desenăm normal
            g.drawImage(animations[currentState].getCurrentFrame(),
                    drawX, drawY, imgWidth, imgHeight, null);
        }
        // ==========================================

        // Am păstrat desenarea hitbox-urilor cu un nivel de transparență
        // ca să poți vedea și personajul, dar și coliziunile pentru debug.
        // Poți șterge liniile de mai jos dacă vrei să vezi DOAR personajul:

        if(handler.getDebugMode()){


        g.setColor(new Color(255, 0, 0, 100)); // Roșu transparent
        g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
                (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                bounds.width, bounds.height);}

        if (edenShieldActive) {
            g.setColor(new Color(0, 255, 0, 100)); // Verde transparent
            g.fillRect((int) (edenShieldBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (edenShieldBounds.y - handler.getGameCamera().getyOffset()),
                    edenShieldBounds.width, edenShieldBounds.height);
        }

        if (isAttacking) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                    attackBounds.width, attackBounds.height);

            g.setColor(Color.BLACK);
            g.drawRect((int) (attackBounds.x - handler.getGameCamera().getxOffset()),
                    (int) (attackBounds.y - handler.getGameCamera().getyOffset()),
                    attackBounds.width, attackBounds.height);
        }
    }

    @Override
    public void onInteraction(Entity e) {}

    @Override
    public void hurt(float dmg) {}

    @Override
    public void die() {
        if (isDying) return; // Ne asigurăm că nu moare de mai multe ori simultan

        isDying = true;
        xMove = 0;
        yMove = 0;

        // Setăm timer-ul să aștepte exact cât durează animația
        deathTimer = 90;
    }
}