package gdd.sprite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

// Slices a sprite sheet into a [row][column] grid of scaled frames.
public class Sheet {

    private Sheet() {
        // Prevent instantiation
    }

    // Returns frames[rows][cols] scaled to targetH pixels tall (aspect kept),
    // optionally mirrored horizontally. Returns null if the sheet can't load.
    public static Image[][] slice(String path, int cols, int rows, int targetH, boolean flip) {
        try {
            BufferedImage sheet = ImageIO.read(new File(path));
            double fw = (double) sheet.getWidth() / cols;
            double fh = (double) sheet.getHeight() / rows;
            int th = targetH;
            int tw = (int) Math.round(fw * th / fh);

            Image[][] frames = new Image[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int sx = (int) Math.round(c * fw);
                    int sy = (int) Math.round(r * fh);
                    int w = Math.min((int) fw, sheet.getWidth() - sx);
                    int h = Math.min((int) fh, sheet.getHeight() - sy);
                    BufferedImage sub = sheet.getSubimage(sx, sy, w, h);

                    BufferedImage out = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = out.createGraphics();
                    if (flip) {
                        g.drawImage(sub, 0, 0, tw, th, w, 0, 0, h, null); // mirror
                    } else {
                        g.drawImage(sub, 0, 0, tw, th, null);
                    }
                    g.dispose();
                    frames[r][c] = out;
                }
            }
            return frames;
        } catch (Exception e) {
            System.err.println("Error slicing sheet " + path + ": " + e.getMessage());
            return null;
        }
    }
}
