package gdd.sprite;

import gdd.AudioPlayer;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss extends Enemy {

    public static final String NAME = "MOTHER RADAM";
    private static final int MAX_HP = 160;
    private static final int TARGET_H = 150;
    private static final int ANIM_PERIOD = 14;

    // Sheet rows
    private static final int ROW_TRIPLE = 0;
    private static final int ROW_CRESCENT = 1;
    private static final int ROW_METEOR = 2;
    private static final int ROW_IDLE = 3;
    private static final int COLS = 4;
    private static final int ROWS = 4;

    private Image[][] frames;
    private int row = ROW_IDLE;
    private int animFrame = 0;
    private int animTick = 0;

    private boolean attacking = false;
    private int idleTimer = 30;

    // Drifts around the right half of the screen, changing heading now and then
    private int vx = -1;
    private int vy = 1;
    private int driftTimer = 0;

    private int meteorSoundTimer = 0; // frames left before the meteor sound plays
    private int lastPx = 0;
    private int lastPy = 0;

    private final List<BossProjectile> pending = new ArrayList<>();
    private final Random rng = new Random();

    public Boss(int x, int y) {
        super(x, y);
        health = MAX_HP;
        maxHealth = MAX_HP;

        frames = Sheet.slice(IMG_BOSS, COLS, ROWS, TARGET_H, true); // flip to face left
        if (frames == null) {
            setImage(makePlaceholder());
        } else {
            setImage(frames[ROW_IDLE][0]);
        }

        int w = getImage().getWidth(null);
        int h = getImage().getHeight(null);
        // Enters on the right, then wanders from there
        this.x = BOARD_WIDTH - w - 20;
        this.y = (BOARD_HEIGHT - h) / 2;
    }

    private Image makePlaceholder() {
        int s = TARGET_H;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(150, 40, 90));
        g.fillOval(10, 20, s - 20, s - 40);
        g.dispose();
        return img;
    }

    public int getPhase() {
        double f = getHealthFraction();
        if (f > 0.80) {
            return 1;
        }
        if (f > 0.50) {
            return 2;
        }
        return 3;
    }

    private Image currentFrame() {
        if (frames == null) {
            return getImage();
        }
        int f = Math.min(animFrame, frames[row].length - 1);
        return frames[row][f];
    }

    // Advance animation; returns true when a non-looping run finishes
    private boolean advanceAnim(boolean loop) {
        animTick++;
        if (animTick >= ANIM_PERIOD) {
            animTick = 0;
            animFrame++;
            if (animFrame >= COLS) {
                if (loop) {
                    animFrame = 0;
                } else {
                    animFrame = COLS - 1;
                    return true;
                }
            }
        }
        return false;
    }

    public void update(int px, int py) {
        lastPx = px;
        lastPy = py;

        wander();

        // The meteors are still warning on screen when they spawn, so wait
        // 2 seconds before playing the falling sound
        if (meteorSoundTimer > 0) {
            meteorSoundTimer--;
            if (meteorSoundTimer == 0) {
                AudioPlayer.playSound("src/audio/meteor.wav", 0f);
            }
        }

        if (attacking) {
            if (advanceAnim(false)) {
                fire(row);              // spawn projectiles at the end of the animation
                attacking = false;
                idleTimer = attackGap();
                row = ROW_IDLE;
                animFrame = 0;
                animTick = 0;
            }
        } else {
            row = ROW_IDLE;
            advanceAnim(true);
            if (--idleTimer <= 0) {
                row = chooseAttack();
                attacking = true;
                animFrame = 0;
                animTick = 0;

                // The meteor attack has a long wind-up, so play the summon
                // sound while the boss is charging it
                if (row == ROW_METEOR) {
                    AudioPlayer.playSound("src/audio/summon.wav", -2f);
                }
            }
        }
        setImage(currentFrame());
    }

    private void wander() {
        if (--driftTimer <= 0) {
            driftTimer = 90 + rng.nextInt(90);
            vx = rng.nextInt(3) - 1;
            vy = rng.nextInt(5) - 2;
        }

        x += vx;
        y += vy;
        keepInZone();
    }

    private void keepInZone() {
        int w = getImage().getWidth(null);
        int h = getImage().getHeight(null);

        int minX = BOARD_WIDTH / 2;
        int maxX = BOARD_WIDTH - w - 10;
        if (x < minX) {
            x = minX;
            vx = -vx;
        }
        if (x > maxX) {
            x = maxX;
            vx = -vx;
        }

        if (y < 20) {
            y = 20;
            vy = -vy;
        }
        if (y > BOARD_HEIGHT - h - 20) {
            y = BOARD_HEIGHT - h - 20;
            vy = -vy;
        }
    }

    // The cave is solid to her too: back out of it and turn around
    public void blocked(int ox, int oy) {
        x = ox;
        y = oy;
        vx = -vx;
        vy = -vy;
        driftTimer = 30 + rng.nextInt(30);
    }

    // She presses harder the more damage she has taken
    private int attackGap() {
        switch (getPhase()) {
            case 1:
                return 26;
            case 2:
                return 16;
            default:
                return 8;
        }
    }

    // Pick an attack row allowed by the current phase
    private int chooseAttack() {
        int phase = getPhase();
        List<Integer> options = new ArrayList<>();
        options.add(ROW_CRESCENT);          // all phases
        if (phase >= 2) {
            options.add(ROW_TRIPLE);
        }
        if (phase >= 3) {
            options.add(ROW_METEOR);
        }
        return options.get(rng.nextInt(options.size()));
    }

    private void fire(int attackRow) {
        int w = getImage().getWidth(null);
        int h = getImage().getHeight(null);
        int cx = x + w / 3;
        int cy = y + h / 2;

        switch (attackRow) {
            case ROW_CRESCENT: {
                AudioPlayer.playSound("src/audio/crescent.wav", 0f);
                double dx = lastPx - cx;
                double dy = lastPy - cy;
                double d = Math.max(1, Math.hypot(dx, dy));
                double sp = 3;
                pending.add(new BossProjectile(BossProjectile.Kind.CRESCENT,
                        cx, cy, dx / d * sp, dy / d * sp, true, 150));
                break;
            }
            case ROW_TRIPLE:
                AudioPlayer.playSound("src/audio/boss_3_shot.wav", 0f);
                // Fan out wide, hang in the air for a beat, then each one
                // turns toward wherever the ship is at that instant and holds
                // that line until it leaves the screen.
                double aim = Math.atan2(lastPy - cy, lastPx - cx);
                double bombSpeed = 6;
                for (double spread : new double[] {-0.5, 0, 0.5}) {
                    double ang = aim + spread;
                    BossProjectile bomb = new BossProjectile(BossProjectile.Kind.BOMB,
                            cx, cy, Math.cos(ang) * bombSpeed, Math.sin(ang) * bombSpeed,
                            false, -1);
                    bomb.turnAfter(60, 36); // 1s spreading, then 0.6s hanging
                    pending.add(bomb);
                }
                break;
            case ROW_METEOR:
                // One sound for the whole volley, not one per rock
                meteorSoundTimer = 45; // just after the warning clears
                for (int i = 0; i < 3; i++) {
                    int tx = lastPx + rng.nextInt(240) - 100;
                    tx = Math.max(0, Math.min(BOARD_WIDTH - 40, tx));
                    // vx is 0 so the meteors drop straight down
                    pending.add(new BossProjectile(BossProjectile.Kind.ROCK,
                            tx, -40, 0, 11.0, false, -1, 40));
                }
                break;
        }
    }

    // Scene pulls newly-fired projectiles into its own list
    public List<BossProjectile> takeProjectiles() {
        if (pending.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<BossProjectile> out = new ArrayList<>(pending);
        pending.clear();
        return out;
    }
}
