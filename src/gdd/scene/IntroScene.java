package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class IntroScene extends JPanel {

    private static final int WIPE_FRAMES = 72; // about 1.2 seconds
    private static final int WIPE_PAGE = 30;   // 0.5s, between pages

    private static final Font PROMPT_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final String PROMPT_TEXT = "Press Space to continue";
    private static final int PROMPT_BLINK = 30;

    private int frame = 0;

    private int wipeIn = WIPE_FRAMES;
    private int wipeInSpan = WIPE_FRAMES;
    private int wipeOut = 0;
    private boolean starting = false; // load the level once the screen is black

    private int page = 0;
    private AudioPlayer audioPlayer;

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private final Game game;

    public IntroScene(Game game) {
        this.game = game;
    }

    public void start() {
        if (getKeyListeners().length == 0) {
            addKeyListener(new TAdapter());
        }
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        page = 0;
        frame = 0;
        wipeIn = WIPE_FRAMES;
        wipeInSpan = WIPE_FRAMES;
        wipeOut = 0;
        starting = false;

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        initAudio();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initAudio() {
        try {
            audioPlayer = new AudioPlayer("src/audio/Venus.wav");
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

        var ii = new ImageIcon(IMG_GAME_START[page]);
        g.drawImage(ii.getImage(), 0, 0, BOARD_WIDTH, BOARD_HEIGHT, this);

        drawContinuePrompt(g);
        drawWipe(g);

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawContinuePrompt(Graphics g) {

        if (starting || (frame / PROMPT_BLINK) % 2 == 1) {
            return;
        }

        g.setFont(PROMPT_FONT);
        int w = g.getFontMetrics().stringWidth(PROMPT_TEXT);
        int x = (BOARD_WIDTH - w) / 2;
        int y = BOARD_HEIGHT - 230;

        g.setColor(Color.black);
        g.drawString(PROMPT_TEXT, x + 2, y + 2);
        g.setColor(Color.yellow);
        g.drawString(PROMPT_TEXT, x, y);
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
                game.loadScene1();
            }
        }
    }

    private void drawWipe(Graphics g) {

        int cover = 0;
        if (wipeIn > 0) {
            cover = 255 * wipeIn / wipeInSpan;
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
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() != KeyEvent.VK_SPACE || starting || wipeOut > 0) {
                return;
            }

            if (page < IMG_GAME_START.length - 1) {
                page++;
                wipeIn = WIPE_PAGE; // fade between pages
                wipeInSpan = WIPE_PAGE;
            } else {
                starting = true;
                wipeIn = 0;
                wipeOut = WIPE_FRAMES;
            }
        }
    }
}
