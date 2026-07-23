package gdd.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Obstacle extends Sprite {

    private static final int SPEED = 2;
    private static final int SIZE = 60;

    public Obstacle(int x, int y) {
        initObstacle(x, y);
    }

    private void initObstacle(int x, int y) {

        this.x = x;
        this.y = y;

        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setColor(new Color(120, 85, 60));
        g2.fillOval(0, 0, SIZE, SIZE);

        g2.dispose();
        setImage(img);
    }

    @Override
    public void act() {
        this.x -= SPEED;
    }
}
