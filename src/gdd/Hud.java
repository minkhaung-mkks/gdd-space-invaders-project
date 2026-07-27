package gdd;

import static gdd.Global.*;
import gdd.sprite.Boss;
import gdd.sprite.Player;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Hud {

    private static final Color HEALTH_FULL = new Color(214, 48, 49);
    private static final Color HEALTH_FLASH = new Color(255, 168, 168);
    private static final Color HEALTH_EMPTY = new Color(38, 38, 46);
    private static final Color VALUE = new Color(246, 240, 220);
    private static final Color BOSS_NAME = new Color(255, 62, 62);
    private static final Color BOSS_FILL = new Color(198, 28, 38);
    private static final Color BOSS_TRACK = new Color(42, 20, 24);

    private static final int BOSS_X = 60;
    private static final int BOSS_Y = 26;
    private static final int BOSS_H = 12;

    private static final int HEALTH_X = 44;
    private static final int HEALTH_W = 100;
    private static final int ROW_MID = 670;

    private final Font stageFont = new Font("Monospaced", Font.BOLD, 10);
    private final Font scoreFont = new Font("Monospaced", Font.BOLD, 18);
    private final Font bossFont = new Font("Monospaced", Font.BOLD, 14);

    private final Image frame;
    private final Image speedIcon;
    private final Image multiIcon;
    private final Image splitIcon;
    private final Image bigIcon;

    private int shownScore = 0;
    private int tick = 0;

    public Hud() {
        frame = darken(IMG_GAME_UI, 0.3f);
        speedIcon = new ImageIcon(IMG_POWERUP_SPEEDUP).getImage();
        multiIcon = new ImageIcon(IMG_POWERUP_MULTI).getImage();
        splitIcon = new ImageIcon(IMG_POWERUP_SPLIT).getImage();
        bigIcon = new ImageIcon(IMG_POWERUP_BIG).getImage();
    }

    private Image darken(String path, float amount) {
        Image src = new ImageIcon(path).getImage();
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, amount));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return out;
    }

    public void draw(Graphics g, String stage, int score, Player player, Boss boss, JPanel panel) {

        tick++;
        if (shownScore > score) {
            shownScore = score;
        } else if (shownScore < score) {
            shownScore += Math.max(1, (score - shownScore) / 8);
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        g2.drawImage(frame, 0, 0, panel);

        g2.setColor(VALUE);
        g2.setFont(stageFont);
        g2.drawString(stage, 66, 627);
        g2.setFont(scoreFont);
        g2.drawString(String.format("%06d", shownScore), 624, 666);

        drawHealth(g2, player);

        drawSlots(g2, panel, speedIcon, player.getSpeedUpCount(), 2, 177, 13, 18);
        drawSlots(g2, panel, multiIcon, player.getMultiShotCount(), 4, 245, 26, 15);
        drawSlots(g2, panel, splitIcon, player.getSplitShotCount(), 2, 395, 16, 18);
        drawSlots(g2, panel, bigIcon, player.getBigShotCount(), 2, 481, 19, 18);

        if (boss != null && boss.isVisible()) {
            drawBossBar(g2, boss);
        }
    }

    private void drawBossBar(Graphics2D g2, Boss boss) {

        int w = BOARD_WIDTH - 2 * BOSS_X;

        g2.setFont(bossFont);
        int nameW = g2.getFontMetrics().stringWidth(Boss.NAME);
        g2.setColor(BOSS_NAME);
        g2.drawString(Boss.NAME, (BOARD_WIDTH - nameW) / 2, BOSS_Y - 6);

        g2.setColor(BOSS_TRACK);
        g2.fillRect(BOSS_X, BOSS_Y, w, BOSS_H);
        g2.setColor(BOSS_FILL);
        g2.fillRect(BOSS_X, BOSS_Y, (int) (w * boss.getHealthFraction()), BOSS_H);
        g2.setColor(Color.BLACK);
        g2.drawRect(BOSS_X, BOSS_Y, w, BOSS_H);
    }

    private void drawHealth(Graphics2D g2, Player player) {

        int blocks = player.getMaxHealth();
        int step = HEALTH_W / blocks;
        int y = ROW_MID - 8;
        boolean critical = player.getHealth() <= 1;

        for (int i = 0; i < blocks; i++) {
            if (i >= player.getHealth()) {
                g2.setColor(HEALTH_EMPTY);
            } else if (critical && (tick / 15) % 2 == 0) {
                g2.setColor(HEALTH_FLASH);
            } else {
                g2.setColor(HEALTH_FULL);
            }
            g2.fillRect(HEALTH_X + i * step, y, step - 2, 16);
        }
    }

    private void drawSlots(Graphics2D g2, JPanel panel, Image icon,
            int filled, int total, int x, int w, int h) {

        int y = ROW_MID - h / 2;

        for (int i = 0; i < total; i++) {
            Composite old = g2.getComposite();
            if (i >= filled) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
            }
            g2.drawImage(icon, x + i * (w + 2), y, w, h, panel);
            g2.setComposite(old);
        }
    }
}
