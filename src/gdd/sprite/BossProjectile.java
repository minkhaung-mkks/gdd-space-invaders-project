package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import javax.swing.ImageIcon;

public class BossProjectile extends Sprite {

    public enum Kind { CRESCENT, BOMB, ROCK }

    private final Kind kind;
    private double vx;
    private double vy;
    private final boolean homing;
    private final double speed;
    private int life; // frames left; -1 = lives until off-screen
    private int warn; // frames of "incoming" warning before it activates

    private static final int SPIN_PERIOD = 5; // frames per crescent turn
    private int spinFrame = 0;
    private int spinTick = 0;

    public BossProjectile(Kind kind, int x, int y, double vx, double vy, boolean homing, int life) {
        this(kind, x, y, vx, vy, homing, life, 0);
    }

    public BossProjectile(Kind kind, int x, int y, double vx, double vy,
            boolean homing, int life, int warn) {
        this.kind = kind;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.homing = homing;
        this.life = life;
        this.warn = warn;
        this.speed = Math.max(1, Math.hypot(vx, vy));
        setImage(makeImage());
    }

    // While warning, the projectile hasn't dropped yet — the scene draws a
    // "!" marker at its column instead of the sprite, and it can't hit anyone.
    public boolean isWarning() {
        return warn > 0;
    }

    private Image makeImage() {
        switch (kind) {
            case CRESCENT:
                return new ImageIcon(IMG_VFX_CRESCENT[0]).getImage();
            case ROCK:
                return new ImageIcon(IMG_VFX_METEOR).getImage();
            default:
                return new ImageIcon(IMG_VFX_BOSS_BOMB).getImage();
        }
    }

    // Re-aim toward the player each frame
    public void steer(int px, int py) {
        if (!homing) {
            return;
        }
        double dx = px - x;
        double dy = py - y;
        double d = Math.hypot(dx, dy);
        if (d > 1) {
            vx = dx / d * speed;
            vy = dy / d * speed;
        }
    }

    @Override
    public void act() {
        // Hold in place during the warning telegraph
        if (warn > 0) {
            warn--;
            return;
        }

        x += (int) Math.round(vx);
        y += (int) Math.round(vy);

        if (kind == Kind.CRESCENT) {
            spinTick++;
            if (spinTick >= SPIN_PERIOD) {
                spinTick = 0;
                spinFrame = (spinFrame + 1) % IMG_VFX_CRESCENT.length;
                setImage(new ImageIcon(IMG_VFX_CRESCENT[spinFrame]).getImage());
            }
        }

        if (life > 0) {
            life--;
            if (life == 0) {
                die();
            }
        }

        // Off-screen cleanup
        if (x < -60 || x > BOARD_WIDTH + 60 || y > BOARD_HEIGHT + 60 || y < -80) {
            die();
        }
    }
}
