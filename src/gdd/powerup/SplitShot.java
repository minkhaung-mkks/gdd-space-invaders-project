package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class SplitShot extends PowerUp {

    public SplitShot(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_SPLIT);
        setImage(ii.getImage());
    }

    public void act() {
        this.x -= 2;
    }

    public void upgrade(Player player) {
        if (player.getSplitShotCount() < 2) {
            player.incrementSplitShotCount();
        }
        this.die();
    }

}
