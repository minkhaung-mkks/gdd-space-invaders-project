package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class BigShot extends PowerUp {

    public BigShot(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_BIG);
        setImage(ii.getImage());
    }

    public void act() {
        this.x -= 2;
    }

    public void upgrade(Player player) {
        if (player.getBigShotCount() < 2) {
            player.incrementBigShotCount();
        }
        this.die();
    }

}
