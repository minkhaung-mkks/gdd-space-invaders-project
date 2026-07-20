package gdd.sprite;

import javax.swing.*;

import static gdd.Global.IMG_SHOT;
import static gdd.Global.SCALE_FACTOR;

public class Shot extends Sprite {

    private static final int H_SPACE = 40;
    private static final int V_SPACE = 20;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {

        var ii = new ImageIcon(IMG_SHOT);

        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR, 
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(x + H_SPACE);
        setY(y + V_SPACE);
    }
}
