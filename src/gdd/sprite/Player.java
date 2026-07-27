package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 40;
    private static final int START_Y = BOARD_HEIGHT / 2;

    private static final int COLS = 6;
    private static final int ROWS = 4;
    private static final int CROP_X = 20;
    private static final int CROP_Y = 27;
    private static final int CROP_W = 65;
    private static final int CROP_H = 49;
    private static final int TARGET_H = 36;  
    private static final int ANIM_PERIOD = 6;

    // Sheet rows
    private static final int R_LEVEL = 0;
    private static final int R_UP = 1;
    private static final int R_DOWN = 2;
    private static final int R_DIAGONAL = 3;

    private Image[][] frames;
    private int animFrame = 0;
    private int animTick = 0;

    private int width;
    private int dy;
    private int currentSpeed = 2;
    private int speedUpCount = 0;
    private int maxShots = 1;
    private int multiShotCount = 0;
    private int splitShotCount = 0;
    private int bigShotCount = 0;

    // Time after being hit where nothing can hurt the ship again
    private static final int INVINCIBLE_FRAMES = 90; // 1.5 seconds at 60 fps
    private static final int BLINK_PERIOD = 6;       // frames per flash
    private int invincible = 0;

    private static final int KNOCK_FRAMES = 21; // 0.35s with no steering
    private int knockFrames = 0;
    private double kvx = 0;
    private double kvy = 0;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        health = 5;
        maxHealth = 5;
        initPlayer();
    }

    // Take the upgrades earned in the previous stage. Health starts fresh.
    public void copyUpgradesFrom(Player other) {
        if (other == null) {
            return;
        }
        this.currentSpeed = other.currentSpeed;
        this.speedUpCount = other.speedUpCount;
        this.maxShots = other.maxShots;
        this.multiShotCount = other.multiShotCount;
        this.splitShotCount = other.splitShotCount;
        this.bigShotCount = other.bigShotCount;
    }

    private void initPlayer() {
        loadFrames();

        if (frames != null) {
            setImage(frames[R_LEVEL][0]);
        } else {
            var ii = new ImageIcon(IMG_PLAYER);
            Image rotatedImage = rotateImage90(ii.getImage(), ii.getIconWidth(), ii.getIconHeight());
            var scaledImage = rotatedImage.getScaledInstance(ii.getIconHeight() * SCALE_FACTOR,
                    ii.getIconWidth() * SCALE_FACTOR,
                    java.awt.Image.SCALE_SMOOTH);
            setImage(scaledImage);
        }

        setX(START_X);
        setY(START_Y);
    }

    private void loadFrames() {
        try {
            BufferedImage sheet = ImageIO.read(new File(IMG_PLAYER_SHEET));
            double fw = (double) sheet.getWidth() / COLS;
            double fh = (double) sheet.getHeight() / ROWS;
            int tw = CROP_W * TARGET_H / CROP_H;

            frames = new Image[ROWS][COLS];
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    int sx = (int) Math.round(c * fw) + CROP_X;
                    int sy = (int) Math.round(r * fh) + CROP_Y;
                    if (sx + CROP_W > sheet.getWidth() || sy + CROP_H > sheet.getHeight()) {
                        continue;
                    }
                    BufferedImage sub = sheet.getSubimage(sx, sy, CROP_W, CROP_H);

                    BufferedImage out = new BufferedImage(tw, TARGET_H, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = out.createGraphics();
                    g2.drawImage(sub, 0, 0, tw, TARGET_H, null);
                    g2.dispose();
                    frames[r][c] = out;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading player sheet: " + e.getMessage());
            frames = null;
        }
    }

    private void updateFrame() {
        if (frames == null) {
            return;
        }

        int row = R_LEVEL;
        int firstCol = 0;

        if (dy < 0 && dx > 0) {
            row = R_DIAGONAL;          // up and forward
        } else if (dy > 0 && dx > 0) {
            row = R_DIAGONAL;
            firstCol = 2;              // down and forward
        } else if (dy < 0) {
            row = R_UP;
        } else if (dy > 0) {
            row = R_DOWN;
        }

        animTick++;
        if (animTick >= ANIM_PERIOD) {
            animTick = 0;
            animFrame = (animFrame + 1) % 2;
        }

        Image img = frames[row][firstCol + animFrame];
        if (img != null) {
            setImage(img);
        }
    }

    private Image rotateImage90(Image source, int width, int height) {
        var rotated = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.translate((height - width) / 2.0, (width - height) / 2.0);
        g2d.rotate(Math.PI / 2, width / 2.0, height / 2.0);
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return rotated;
    }

    // Ignore hits while the ship is still flashing from the last one
    @Override
    public void damage(int amount) {
        if (invincible > 0) {
            return;
        }
        super.damage(amount);
        invincible = INVINCIBLE_FRAMES;
    }

    public boolean isInvincible() {
        return invincible > 0;
    }

    // The scene skips drawing the ship on the off part of each flash
    public boolean isFlashedOff() {
        return invincible > 0 && (invincible / BLINK_PERIOD) % 2 == 1;
    }

    // Bounced off the cave: downward is 1 to be shoved down, -1 to be shoved up
    public void knockBack(int downward) {
        knockFrames = KNOCK_FRAMES;
        kvx = -10;
        kvy = downward * 12;
    }

    public boolean isStunned() {
        return knockFrames > 0;
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public int getSpeedUpCount() {
        return speedUpCount;
    }

    public void incrementSpeedUpCount() {
        speedUpCount++;
    }

    public int getMaxShots() {
        return maxShots;
    }

    public void setMaxShots(int maxShots) {
        this.maxShots = maxShots;
    }

    public int getMultiShotCount() {
        return multiShotCount;
    }

    public void incrementMultiShotCount() {
        multiShotCount++;
    }

    public int getSplitShotCount() {
        return splitShotCount;
    }

    public void incrementSplitShotCount() {
        splitShotCount++;
    }

    public int getBigShotCount() {
        return bigShotCount;
    }

    public void incrementBigShotCount() {
        bigShotCount++;
    }

    public void act() {
        if (knockFrames > 0) {
            // Thrown clear of the wall — steering does nothing until it settles
            x += (int) Math.round(kvx);
            y += (int) Math.round(kvy);
            kvx *= 0.82;
            kvy *= 0.82;
            knockFrames--;
        } else {
            y += dy;
            x += dx;
        }

        if (invincible > 0) {
            invincible--;
        }

        updateFrame();

        if (y <= 2) {
            y = 2;
        }

        if (y >= BOARD_HEIGHT - 3 * PLAYER_HEIGHT) {
            y = BOARD_HEIGHT - 3 * PLAYER_HEIGHT;
        }

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 4 * PLAYER_WIDTH) {
            x = BOARD_WIDTH - 4 * PLAYER_WIDTH;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            dy = -currentSpeed;
        }

        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            dy = currentSpeed;
        }

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            dy = 0;
        }

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            dx = 0;
        }
    }
}
