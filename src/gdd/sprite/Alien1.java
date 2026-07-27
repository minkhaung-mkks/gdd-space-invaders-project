package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Alien1 extends Enemy {

    private static final int COLS = 4;
    private static final int ROWS = 3;
    private static final int R_MOVE = 1;
    private static final int R_SHOOT = 2;
    private static final int TARGET_H = 44;
    private static final int ANIM_PERIOD = 6;

    private Image[][] frames; 
    private int animFrame = 0;
    private int animTick = 0;
    private int shootHold = 0;

    private Bomb bomb;

    public Alien1(int x, int y) {
        super(x, y);
        bomb = new Bomb(x, y);

        health = 2;
        maxHealth = 2;

        frames = Sheet.slice(IMG_ALIEN1_SHEET, COLS, ROWS, TARGET_H, true); // flip to face left
        if (frames != null) {
            setImage(frames[R_MOVE][0]);
        }
    }

    public void notifyShoot() {
        shootHold = 24;
    }

    public void act(int direction) {
        this.x--;

        if (frames != null) {
            int row = (shootHold > 0) ? R_SHOOT : R_MOVE;
            if (shootHold > 0) {
                shootHold--;
            }
            animTick++;
            if (animTick >= ANIM_PERIOD) {
                animTick = 0;
                animFrame = (animFrame + 1) % COLS;
            }
            setImage(frames[row][animFrame]);
        }
    }

    public Bomb getBomb() {

        return bomb;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var ii = new ImageIcon(IMG_VFX_ALIEN_BOMB);
            setImage(ii.getImage());
        }
        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
    }
}
