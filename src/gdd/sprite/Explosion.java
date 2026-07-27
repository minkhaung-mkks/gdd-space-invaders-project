package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Explosion extends Sprite {

    private static final int HOLD = 4; // game frames per picture

    private final String[] frames;

    // The normal alien explosion
    public Explosion(int x, int y) {

        this(x, y, IMG_VFX_EXPLOSION);
    }

    // A different set of pictures, for the player and for obstacles
    public Explosion(int x, int y, String[] frames) {

        this.frames = frames;
        this.x = x;
        this.y = y;
        this.visibleFrames = frames.length * HOLD;

        setImage(new ImageIcon(frames[0]).getImage());
    }

    // Step through the pictures as the explosion runs out of time
    @Override
    public void visibleCountDown() {
        super.visibleCountDown();

        int step = (frames.length * HOLD - visibleFrames) / HOLD;
        if (step > frames.length - 1) {
            step = frames.length - 1;
        }
        setImage(new ImageIcon(frames[step]).getImage());
    }

    public void act(int direction) {
        // this.x += direction;
    }

}
