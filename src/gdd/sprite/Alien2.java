package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {

    private static final int COLS = 2;
    private static final int ROWS = 2;
    private static final int R_IDLE = 0;
    private static final int R_ATTACK = 1;
    private static final int TARGET_H = 56;
    private static final int ANIM_PERIOD = 8;
    private static final int CHASE = 2; // how fast it follows the player up and down

    private Image[][] frames;
    private int animFrame = 0;
    private int animTick = 0;
    private int attackHold = 0;

    private int targetY;
    private Bomb bomb;

    public Alien2(int x, int y) {
        super(x, y);
        bomb = new Bomb(x, y);

        frames = Sheet.slice(IMG_ALIEN2_SHEET, COLS, ROWS, TARGET_H, true); // flip to face left
        if (frames != null) {
            setImage(frames[R_IDLE][0]);
        }
    }

    public void notifyShoot() {
        attackHold = 24;
    }

    // The scene tells it where the player is before every move
    public void track(int playerY) {
        targetY = playerY;
    }

    public void act(int direction) {

        // Hangs from the ceiling on the spot — it never moves sideways, it only
        // slides up and down to line itself up with the player
        if (y < targetY - CHASE) {
            y += CHASE;
        } else if (y > targetY + CHASE) {
            y -= CHASE;
        }
        if (y < 0) {
            y = 0;
        }

        if (frames != null) {
            int row = (attackHold > 0) ? R_ATTACK : R_IDLE;
            if (attackHold > 0) {
                attackHold--;
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

            var ii = new ImageIcon(IMG_VFX_ALIEN2_BOMB);
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
