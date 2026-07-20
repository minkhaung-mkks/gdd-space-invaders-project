package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 40;
    private static final int START_Y = BOARD_HEIGHT / 2;
    private int width;
    private int dy;
    private int currentSpeed = 2;
    private int speedUpCount = 0;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        Image rotatedImage = rotateImage90(ii.getImage(), ii.getIconWidth(), ii.getIconHeight());

        // Scale the image to use the global scaling factor
        var scaledImage = rotatedImage.getScaledInstance(ii.getIconHeight() * SCALE_FACTOR,
                ii.getIconWidth() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);
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

    public void act() {
        y += dy;

        if (y <= 2) {
            y = 2;
        }

        if (y >= BOARD_HEIGHT - 3 * PLAYER_HEIGHT) {
            y = BOARD_HEIGHT - 3 * PLAYER_HEIGHT;
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
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            dy = 0;
        }
    }
}
