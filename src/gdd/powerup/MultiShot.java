package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_SHOT);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        this.x -= 2;
    }

    public void upgrade(Player player) {
        if (player.getMultiShotCount() < 4) {
            player.setMaxShots(player.getMaxShots() + 1);
            player.incrementMultiShotCount();
        }
        this.die();
    }

}
