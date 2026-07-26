package gdd.sprite;

import static gdd.Global.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class BossProjectile extends Sprite {

    public enum Kind { CRESCENT, BOMB, ROCK }

    private final Kind kind;
    private double vx;
    private double vy;
    private final boolean homing;
    private final double speed;
    private int life; // frames left; -1 = lives until off-screen
    private int warn; // frames of "incoming" warning before it activates

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
        int s = (kind == Kind.ROCK) ? 40 : (kind == Kind.CRESCENT ? 30 : 16);
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (kind) {
            case CRESCENT:
                // Crescent = big disc with a smaller disc punched out
                g.setColor(new Color(210, 100, 230));
                g.fillOval(0, 0, s, s);
                g.setComposite(java.awt.AlphaComposite.Clear);
                g.fillOval(s / 3, -s / 6, s, s);
                break;
            case BOMB:
                g.setColor(new Color(255, 130, 60));
                g.fillOval(0, 0, s, s);
                break;
            case ROCK:
                g.setColor(new Color(120, 85, 60));
                g.fillOval(0, 0, s, s);
                break;
        }
        g.dispose();
        return img;
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
