package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {

    private int frame = 0;
    private Image image;
    private int imageWidth;
    private int imageHeight;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private Game game;

    // Screens fade through black instead of snapping over
    private static final int WIPE_FRAMES = 72; // about 1.2 seconds
    private int wipeIn = WIPE_FRAMES;          // black clearing away
    private int wipeOut = 0;                   // screen going black
    private boolean starting = false;          // load the level once black

    public TitleScene(Game game) {
        this.game = game;
        // initBoard();
        // initTitle();
    }

    private void initBoard() {

    }

    public void start() {
        // Only once, in case the title screen is shown again after the ending
        if (getKeyListeners().length == 0) {
            addKeyListener(new TAdapter());
        }
        setFocusable(true);
        requestFocusInWindow(); // needed when coming back from the ending
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        wipeIn = WIPE_FRAMES;
        wipeOut = 0;
        starting = false;

        initTitle();
        initAudio();
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }

            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initTitle() {
        var ii = new ImageIcon(IMG_TITLE);

        // The title art is a wide image, so shrink it to the board width and
        // keep the aspect ratio, so the whole picture stays visible
        imageWidth = BOARD_WIDTH;
        imageHeight = ii.getIconHeight() * BOARD_WIDTH / ii.getIconWidth();
        image = ii.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/title_final.wav";
            audioPlayer = new AudioPlayer(filePath);

            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error with playing sound.");
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        // Whole title art, centered on the black background.
        // The game title and the "press space" text are part of the image.
        g.drawImage(image, (d.width - imageWidth) / 2, (d.height - imageHeight) / 2, this);

        g.setColor(Color.gray);
        g.setFont(g.getFont().deriveFont(10f));

        String name1 = "6712164 - Min Khaung Kyaw Swar";
        String name2 = "6726129 - Lwin Pyae Aung";
        g.drawString(name1, 240, 640);
        g.drawString(name2, 240, 652);

        drawWipe(g);

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        frame++;

        if (wipeIn > 0) {
            wipeIn--;
        }
        if (wipeOut > 0) {
            wipeOut--;
            if (wipeOut == 0 && starting) {
                starting = false;
                game.loadScene2();
            }
        }
    }

    // Black sheet over everything, thickest at the start of a fade
    private void drawWipe(Graphics g) {

        int cover = 0;
        if (wipeIn > 0) {
            cover = 255 * wipeIn / WIPE_FRAMES;
        }
        if (wipeOut > 0) {
            cover = 255 * (WIPE_FRAMES - wipeOut) / WIPE_FRAMES;
        }
        if (cover <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cover / 255f));
        g2.setColor(Color.black);
        g2.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g2.setComposite(old);
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Title.keyPressed: " + e.getKeyCode());
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE && !starting) {
                // Fade out first, the level loads once the screen is black.
                // Works even while the title is still fading in, so the very
                // first press always counts.
                starting = true;
                wipeIn = 0;
                wipeOut = WIPE_FRAMES;
                AudioPlayer.playSound("src/audio/game_start.wav", 0f);
            }

        }
    }
}
