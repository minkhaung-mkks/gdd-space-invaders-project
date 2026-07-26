package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Explosion extends Sprite {

    private static final int LIFE = 12; // frames on screen, 4 per picture

    public Explosion(int x, int y) {

        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;
        this.visibleFrames = LIFE;

        var ii = new ImageIcon(IMG_VFX_EXPLOSION[0]);
        setImage(ii.getImage());
    }

    @Override
    public void visibleCountDown() {
        super.visibleCountDown();

        int step = (LIFE - visibleFrames) / 4;
        if (step > IMG_VFX_EXPLOSION.length - 1) {
            step = IMG_VFX_EXPLOSION.length - 1;
        }
        setImage(new ImageIcon(IMG_VFX_EXPLOSION[step]).getImage());
    }

    public void act(int direction) {
        // this.x += direction;
    }

}
