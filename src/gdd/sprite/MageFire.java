package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class MageFire extends Sprite {

    private static final int SPEED = 5;

    public MageFire(int x, int y) {
        this.x = x;
        this.y = y;

        var ii = new ImageIcon(IMG_VFX_MAGE_FIREBALL);
        setImage(ii.getImage());
    }

    @Override
    public void act() {
        x -= SPEED;
        if (x < -getImage().getWidth(null)) {
            die();
        }
    }
}
