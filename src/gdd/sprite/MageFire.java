package gdd.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class MageFire extends Sprite {

    private static final int SIZE = 44;
    private static final int SPEED = 5;

    public MageFire(int x, int y) {
        this.x = x;
        this.y = y;
        setImage(makeImage());
    }

    // Placeholder: a bigger fireball. Swap for a real sprite later.
    private Image makeImage() {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 170, 40));
        g.fillOval(0, 0, SIZE, SIZE);
        g.setColor(new Color(255, 90, 20));
        g.fillOval(7, 7, SIZE - 14, SIZE - 14);
        g.setColor(new Color(255, 240, 130));
        g.fillOval(15, 15, SIZE - 30, SIZE - 30);
        g.dispose();
        return img;
    }

    @Override
    public void act() {
        x -= SPEED;
        if (x < -SIZE) {
            die();
        }
    }
}
