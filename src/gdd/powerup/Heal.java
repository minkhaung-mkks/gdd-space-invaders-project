package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class Heal extends PowerUp {

    private static final int AMOUNT = 2; // hearts given back

    public Heal(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_HEAL);
        setImage(ii.getImage());
    }

    public void act() {
        this.x -= 2;
    }

    public void upgrade(Player player) {
        player.heal(AMOUNT);
        this.die();
    }

}
