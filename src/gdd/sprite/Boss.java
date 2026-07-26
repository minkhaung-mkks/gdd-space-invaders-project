package gdd.sprite;

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

    public static final String NAME = "MOTHER ADAM";
    private static final int MAX_HP = 50;
    private static final int SIZE = 240;

    private int clawTimer = 60;
    private int bombTimer = 90;
    private int rainTimer = 120;

    // Projectiles the boss has fired this frame, drained by the scene
    private final List<BossProjectile> pending = new ArrayList<>();
    private final Random rng = new Random();

    public Boss(int x, int y) {
        super(x, y);
        health = MAX_HP;
        maxHealth = MAX_HP;
        // Fixed giant pinned to the right, vertically centered
        this.x = BOARD_WIDTH - SIZE + 20;
        this.y = (BOARD_HEIGHT - SIZE) / 2;
        setImage(makeImage());
    }

    private Image makeImage() {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(150, 40, 90));
        g.fillOval(10, 20, SIZE - 20, SIZE - 40);
        g.setColor(new Color(90, 20, 60));
        g.fillOval(30, 45, 28, 28);
        g.fillOval(SIZE - 58, 45, 28, 28);
        g.dispose();
        return img;
    }

    public int getPhase() {
        double f = getHealthFraction();
        if (f > 0.66) {
            return 1;
        }
        if (f > 0.33) {
            return 2;
        }
        return 3;
    }

    // Driven by the scene each frame with the player's position.
    // The boss never moves — it is a fixed giant on the right; only attacks change.
    public void update(int px, int py) {

        int phase = getPhase();

        int cx = x + SIZE / 2;
        int cy = y + SIZE / 2;

        // Claw — homing crescents, all phases
        if (--clawTimer <= 0) {
            clawTimer = (phase == 3) ? 45 : (phase == 2 ? 65 : 85);
            double dx = px - cx;
            double dy = py - cy;
            double d = Math.max(1, Math.hypot(dx, dy));
            double sp = 3;
            pending.add(new BossProjectile(BossProjectile.Kind.CRESCENT,
                    cx, cy, dx / d * sp, dy / d * sp, true, 120));
        }

        // 3-way bomb spread — phase 2 and up
        if (phase >= 2 && --bombTimer <= 0) {
            bombTimer = (phase == 3) ? 75 : 105;
            pending.add(new BossProjectile(BossProjectile.Kind.BOMB, cx, cy, -5, -3, false, -1));
            pending.add(new BossProjectile(BossProjectile.Kind.BOMB, cx, cy, -5, 0, false, -1));
            pending.add(new BossProjectile(BossProjectile.Kind.BOMB, cx, cy, -5, 3, false, -1));
        }

        // Meteor rain — phase 3 only.
        if (phase == 3 && --rainTimer <= 0) {
            rainTimer = 140;
            for (int i = 0; i < 3; i++) {
                // Random column around the player
                int tx = px + rng.nextInt(240) - 100;
                tx = Math.max(0, Math.min(BOARD_WIDTH - 40, tx));
                pending.add(new BossProjectile(BossProjectile.Kind.ROCK,
                        tx, -40, -1.2, 2.0, false, -1, 70));
            }
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
