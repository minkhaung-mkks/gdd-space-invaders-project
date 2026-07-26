package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 40;
    private static final int V_SPACE = 20;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y, 0);
    }

    public Shot(int x, int y, int level) {

        initShot(x, y, level);
    }

    private void initShot(int x, int y, int level) {

        if (level < 0) {
            level = 0;
        }
        if (level > IMG_VFX_SHOT.length - 1) {
            level = IMG_VFX_SHOT.length - 1;
        }

        var ii = new ImageIcon(IMG_VFX_SHOT[level]);
        setImage(ii.getImage());

        setX(x + H_SPACE);
        setY(y + V_SPACE);
    }
}
