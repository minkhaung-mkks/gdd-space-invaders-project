package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class MageFire extends Sprite {

    private static final int SPEED = 5;

    private final double vx;
    private final double vy;
    private double fx;
    private double fy;

    public MageFire(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.fx = x;
        this.fy = y;

        double dx = targetX - x;
        double dy = targetY - y;
        double d = Math.max(1, Math.hypot(dx, dy));
        this.vx = dx / d * SPEED;
        this.vy = dy / d * SPEED;

        var ii = new ImageIcon(IMG_VFX_MAGE_FIREBALL);
        setImage(ii.getImage());
    }

    @Override
    public void act() {
        fx += vx;
        fy += vy;
        x = (int) Math.round(fx);
        y = (int) Math.round(fy);

        int s = getImage().getWidth(null);
        if (x < -s || x > BOARD_WIDTH + s || y < -s || y > BOARD_HEIGHT + s) {
            die();
        }
    }
}
