package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_MULTI);
        setImage(ii.getImage());
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
