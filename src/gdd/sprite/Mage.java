package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class Mage extends Enemy {

    private enum Phase { ENTER, WANDER, SHOOT }

    private static final int ROWS = 4;
    private static final int COLS = 6;
    private static final int R_IDLE = 0;
    private static final int R_MOVE = 1;
    private static final int R_SHOOT = 2;
    private static final int R_DIE = 3;
    private static final int[] FRAME_COUNT = {4, 6, 5, 4};

    private static final int TARGET_H = 96;   // scaled frame height
    private static final int ANIM_PERIOD = 6; // game frames per animation frame
    private static final int ENTER_SPEED = 2;

    private Image[][] frames; 

    private Phase phase = Phase.ENTER;
    private int row = R_MOVE;
    private int animFrame = 0;
    private int animTick = 0;

    private final int holdX;      // roughly where the mage would be
    private int wanderTimer = 0;  // frames until the next shoot
    private int vx = 0;
    private int vy = 0;
    private boolean deathStarted = false;
    private boolean deathDone = false;

    private final Random rng = new Random();
    private final List<MageFire> pending = new ArrayList<>();

    public Mage(int x, int y) {
        super(x, y);
        health = 3;
        maxHealth = 3;
        holdX = BOARD_WIDTH - 280;
        loadFrames();
        this.x = x;
        this.y = y;
        if (frames != null) {
            setImage(frames[R_MOVE][0]);
        }
    }

    private void loadFrames() {
        try {
            BufferedImage sheet = ImageIO.read(new File(IMG_MAGE));
            double fw = (double) sheet.getWidth() / COLS;
            double fh = (double) sheet.getHeight() / ROWS;
            int th = TARGET_H;
            int tw = (int) Math.round(fw * th / fh);

            frames = new Image[ROWS][];
            for (int r = 0; r < ROWS; r++) {
                int n = FRAME_COUNT[r];
                frames[r] = new Image[n];
                for (int c = 0; c < n; c++) {
                    int sx = (int) Math.round(c * fw);
                    int sy = (int) Math.round(r * fh);
                    int w = Math.min((int) fw, sheet.getWidth() - sx);
                    int h = Math.min((int) fh, sheet.getHeight() - sy);
                    BufferedImage sub = sheet.getSubimage(sx, sy, w, h);
                    // Scale and mirror horizontally so the mage faces left
                    BufferedImage flipped = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = flipped.createGraphics();
                    g2.drawImage(sub, 0, 0, tw, th, w, 0, 0, h, null);
                    g2.dispose();
                    frames[r][c] = flipped;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading mage sheet: " + e.getMessage());
            frames = null;
        }
    }

    private int frameCount(int r) {
        return (frames == null) ? 1 : frames[r].length;
    }

    private Image currentFrame() {
        if (frames == null) {
            return getImage();
        }
        int f = Math.min(animFrame, frames[row].length - 1);
        return frames[row][f];
    }

    // Advance the animation clock; returns true when a non-looping run finishes
    private boolean advanceAnim(int count, boolean loop) {
        animTick++;
        if (animTick >= ANIM_PERIOD) {
            animTick = 0;
            animFrame++;
            if (animFrame >= count) {
                if (loop) {
                    animFrame = 0;
                } else {
                    animFrame = count - 1;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isDeathAnimating() {
        return isDying() && !deathDone;
    }

    @Override
    public void act(int direction) {
        if (isDying()) {
            if (!deathStarted) {
                deathStarted = true;
                row = R_DIE;
                animFrame = 0;
                animTick = 0;
            }
            if (advanceAnim(frameCount(R_DIE), false)) {
                deathDone = true;
                die();
            }
            setImage(currentFrame());
            return;
        }

        switch (phase) {
            case ENTER:
                row = R_MOVE;
                x -= ENTER_SPEED;
                if (x <= holdX) {
                    mageMove();
                }
                advanceAnim(frameCount(R_MOVE), true);
                break;

            case WANDER:
                row = R_MOVE;
                x += vx;
                y += vy;
                keepInZone();
                advanceAnim(frameCount(R_MOVE), true);
                if (--wanderTimer <= 0) {
                    phase = Phase.SHOOT;
                    animFrame = 0;
                    animTick = 0;
                }
                break;

            case SHOOT:
                row = R_SHOOT;
                // Fire only after the whole shoot animation has played
                if (advanceAnim(frameCount(R_SHOOT), false)) {
                    fire();
                    mageMove();
                }
                break;
        }
        setImage(currentFrame());
    }

    private void mageMove() {
        phase = Phase.WANDER;
        animFrame = 0;
        animTick = 0;
        wanderTimer = 60 + rng.nextInt(60);    // ~1–2s of drifting, then shoot
        vx = rng.nextInt(3) - 1;              
        vy = rng.nextInt(5) - 2;               
    }

    // Keep the mage roaming within the right portion of the screen
    private void keepInZone() {
        int minX = BOARD_WIDTH - 360;
        int maxX = BOARD_WIDTH - TARGET_H;
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
        if (y > BOARD_HEIGHT - TARGET_H - 20) {
            y = BOARD_HEIGHT - TARGET_H - 20;
            vy = -vy;
        }
    }

    private void fire() {
        int cx = x + 10;
        int cy = y + TARGET_H / 2;
        pending.add(new MageFire(cx, cy));
    }

    public List<MageFire> takeProjectiles() {
        if (pending.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<MageFire> out = new ArrayList<>(pending);
        pending.clear();
        return out;
    }
}
