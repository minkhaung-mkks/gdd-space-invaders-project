package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Satellite extends Sprite {

    public static final int OFFSET = 30; // how far from the middle of the ship
    private static final int TURN = 6;   

    private final int side; // -1 above the ship, 1 below
    private Image base;
    private int level = -1;
    private int angle;

    public Satellite(int side) {
        this.side = side;
        setLevel(0);
    }

    public int getSide() {
        return side;
    }

    public void setLevel(int level) {
        if (level < 0) {
            level = 0;
        }
        if (level > IMG_VFX_SPLIT.length - 1) {
            level = IMG_VFX_SPLIT.length - 1;
        }
        if (level == this.level) {
            return;
        }
        this.level = level;

        var ii = new ImageIcon(IMG_VFX_SPLIT[level]);

        // Turning needs room in the corners, so sit the picture on a square big
        // enough to hold it at any angle
        int size = (int) Math.ceil(Math.hypot(ii.getIconWidth(), ii.getIconHeight()));
        BufferedImage padded = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = padded.createGraphics();
        g.drawImage(ii.getImage(), (size - ii.getIconWidth()) / 2,
                (size - ii.getIconHeight()) / 2, null);
        g.dispose();
        base = padded;
        setImage(base);
    }

    // Sit next to the ship and keep turning
    public void follow(Player player) {
        int size = base.getWidth(null);
        int playerH = player.getImage().getHeight(null);

        x = player.getX() + (player.getImage().getWidth(null) - size) / 2;
        y = player.getY() + (playerH - size) / 2 + side * OFFSET;

        angle = (angle + TURN) % 360;
        BufferedImage turned = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = turned.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.rotate(Math.toRadians(angle), size / 2.0, size / 2.0);
        g.drawImage(base, 0, 0, null);
        g.dispose();
        setImage(turned);
    }
}
