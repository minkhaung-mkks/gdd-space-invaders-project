package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Obstacle extends Sprite {

    private static final int SPEED = 2;
    private static final int MAX_HP = 3; // shots needed to break it

    public Obstacle(int x, int y) {
        initObstacle(x, y);
    }

    private void initObstacle(int x, int y) {

        this.x = x;
        this.y = y;

        health = MAX_HP;
        maxHealth = MAX_HP;

        var ii = new ImageIcon(IMG_OBSTACLE);
        setImage(ii.getImage());
    }

    @Override
    public void act() {
        this.x -= SPEED;
    }
}
