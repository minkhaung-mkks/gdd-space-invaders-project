package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.LevelLoader;
import gdd.SpawnDetails;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Obstacle;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<Obstacle> obstacles;
    private Player player;
    // private Shot shot;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;
    private int score = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private int currentRow = -1;
    // TODO load this map from a file
    private int mapOffset = 0;
    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    // Terrain tile grid.
    private static final int TILE = 50;
    private static final int TERRAIN_SPEED = 2;
    private int[][] TERRAIN;

    private HashMap<Integer, SpawnDetails> spawnMap;
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
        loadTerrain();
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        spawnMap = LevelLoader.loadSpawns(LEVEL_SCENE1_SPAWNS);
    }

    private void loadTerrain() {
        TERRAIN = LevelLoader.loadGrid(LEVEL_SCENE1_TERRAIN);
    }

    private void initBoard() {

    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        obstacles = new ArrayList<>();

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player();
        // shot = new Shot();
    }

    private void drawMap(Graphics g) {
        // difference: far scrolls slowest, near fastest.
        drawParallaxLayer(g, frame / 2, 2);
        drawParallaxLayer(g, frame, 4);
        drawParallaxLayer(g, frame * 2, 6);
    }

    private void drawParallaxLayer(Graphics g, int scroll, int starSize) {

        int scrollOffset = scroll % BLOCKWIDTH;

        int baseCol = scroll / BLOCKWIDTH;
        int colsNeeded = (BOARD_WIDTH / BLOCKWIDTH) + 2; // +2 for smooth scrolling

        for (int screenCol = 0; screenCol < colsNeeded; screenCol++) {
            int mapRow = (baseCol + screenCol) % MAP.length;

            // Calculate X position for this row
            int x = BOARD_WIDTH - ( (screenCol * BLOCKWIDTH) - scrollOffset );

            // Skip if row is completely off-screen
            if (x > BOARD_WIDTH || x < -BLOCKWIDTH) {
                continue;
            }

            // Draw each block in this row
            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    // Calculate Y position
                    int y = col * BLOCKHEIGHT;

                    // Draw a cluster of stars
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT, starSize);
                }
            }
        }

    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height, int starSize) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - starSize / 2, centerY - starSize / 2, starSize, starSize);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    // Read one tile. Screen position is turned into a grid row and column.
    private int tileAt(int col, int row) {
        if (row < 0 || row >= TERRAIN.length) {
            return 0;
        }
        return TERRAIN[row][col % TERRAIN[row].length];
    }

    // True if the rectangle touches any solid tile
    private boolean hitsTerrain(int x, int y, int w, int h) {

        int scroll = frame * TERRAIN_SPEED;

        int firstCol = (scroll + x) / TILE;
        int lastCol = (scroll + x + w) / TILE;
        int firstRow = y / TILE;
        int lastRow = (y + h) / TILE;

        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = firstCol; col <= lastCol; col++) {
                if (tileAt(col, row) != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private void drawTerrain(Graphics g) {

        int scroll = frame * TERRAIN_SPEED;
        int offset = scroll % TILE;
        int firstCol = scroll / TILE;
        int cols = (BOARD_WIDTH / TILE) + 2;

        for (int row = 0; row < TERRAIN.length; row++) {
            for (int i = 0; i < cols; i++) {

                int tile = tileAt(firstCol + i, row);

                if (tile == 0) {
                    continue;
                }

                // Calculate screen position for this tile
                int x = i * TILE - offset;
                int y = row * TILE;

                drawTile(g, tile, x, y);
            }
        }
    }

    private void drawTile(Graphics g, int tile, int x, int y) {

        if (tile == 1) {
            g.setColor(new Color(90, 65, 45));
        } else {
            g.setColor(new Color(95, 100, 110));
        }

        g.fillRect(x, y, TILE, TILE);
    }

    private void drawObstacles(Graphics g) {

        for (Obstacle obstacle : obstacles) {

            if (obstacle.isVisible()) {

                g.drawImage(obstacle.getImage(), obstacle.getX(), obstacle.getY(), this);
            }
        }
    }

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {

                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {

        for (Enemy e : enemies) {
            if (e instanceof Alien1) {
                Alien1.Bomb bomb = ((Alien1) e).getBomb();
                if (!bomb.isDestroyed()) {
                    g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
                }
            }
        }
    }

    private void drawDashboard(Graphics g) {

        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.PLAIN, 12));
        g.drawString("Score: " + score, 10, 25);
        g.drawString("Speed: " + player.getSpeed(), 10, 40);
        g.drawString("Shots: " + player.getMaxShots(), 10, 55);

        drawSpeedUpTracker(g);
        drawMultiShotTracker(g);
    }

    private void drawSpeedUpTracker(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        var ii = new ImageIcon(IMG_POWERUP_SPEEDUP);
        Image icon = ii.getImage();

        int iconSize = 20;
        int startX = 10;
        int startY = 65;
        int gap = 25;

        for (int i = 0; i < 2; i++) {
            int x = startX + i * gap;
            g2d.drawImage(icon, x, startY, iconSize, iconSize, this);

            if (i >= player.getSpeedUpCount()) {
                Composite oldComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                g2d.setColor(Color.black);
                g2d.fillRect(x, startY, iconSize, iconSize);
                g2d.setComposite(oldComposite);
            }
        }
    }

    private void drawMultiShotTracker(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        var ii = new ImageIcon(IMG_SHOT);
        Image icon = ii.getImage();

        int iconSize = 20;
        int startX = 80;
        int startY = 65;
        int gap = 25;

        for (int i = 0; i < 2; i++) {
            int x = startX + i * gap;
            g2d.drawImage(icon, x, startY, iconSize, iconSize, this);

            if (i >= player.getMultiShotCount()) {
                Composite oldComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                g2d.setColor(Color.black);
                g2d.fillRect(x, startY, iconSize, iconSize);
                g2d.setComposite(oldComposite);
            }
        }
    }

    private void drawExplosions(Graphics g) {

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10);

        g.setColor(Color.green);

        if (inGame) {

            drawMap(g);  // Draw background stars first
            drawTerrain(g);
            drawObstacles(g);
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawBombing(g);
            drawPlayer(g);
            drawShot(g);
            drawDashboard(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {


        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    break;
                // Add more cases for different enemy types if needed
                case "Alien2":
                    // Enemy enemy2 = new Alien2(sd.x, sd.y);
                    // enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-MultiShot":
                    PowerUp multiShot = new MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
                case "Obstacle":
                    obstacles.add(new Obstacle(sd.x, sd.y));
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        player.act();

        // Terrain collision: player vs ground/ceiling
        if (player.isVisible() && !player.isDying()
                && hitsTerrain(player.getX(), player.getY(),
                        player.getImage().getWidth(null),
                        player.getImage().getHeight(null))) {
            var ii = new ImageIcon(IMG_EXPLOSION);
            player.setImage(ii.getImage());
            player.setDying(true);
        }

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
            }
        }

        // Obstacles
        List<Obstacle> obstaclesToRemove = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isVisible()) {
                obstacle.act();

                if (obstacle.collidesWith(player)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                }

                if (obstacle.getX() < -100) {
                    obstacle.die();
                    obstaclesToRemove.add(obstacle);
                }
            }
        }
        obstacles.removeAll(obstaclesToRemove);

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy, using the real
                    // (scaled) image sizes rather than the unscaled constants
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shot.collidesWith(enemy)) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                        score += 10;
                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                // Obstacles block shots
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.isVisible() && shot.isVisible()
                            && shot.collidesWith(obstacle)) {
                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                // Terrain blocks shots
                if (shot.isVisible() && hitsTerrain(shot.getX(), shot.getY(),
                        shot.getImage().getWidth(null),
                        shot.getImage().getHeight(null))) {
                    shot.die();
                    shotsToRemove.add(shot);
                }

                int x = shot.getX();
                x += 20;

                if (x > BOARD_WIDTH) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setX(x);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // enemies
        // for (Enemy enemy : enemies) {
        //     int x = enemy.getX();
        //     if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
        //         direction = -1;
        //         for (Enemy e2 : enemies) {
        //             e2.setY(e2.getY() + GO_DOWN);
        //         }
        //     }
        //     if (x <= BORDER_LEFT && direction != 1) {
        //         direction = 1;
        //         for (Enemy e : enemies) {
        //             e.setY(e.getY() + GO_DOWN);
        //         }
        //     }
        // }
        // for (Enemy enemy : enemies) {
        //     if (enemy.isVisible()) {
        //         int y = enemy.getY();
        //         if (y > GROUND - ALIEN_HEIGHT) {
        //             inGame = false;
        //             message = "Invasion!";
        //         }
        //         enemy.act(direction);
        //     }
        // }
        // bombs - collision detection
        // Bomb is with enemy, so it loops over enemies
        for (Enemy enemy : enemies) {

            if (!(enemy instanceof Alien1)) {
                continue;
            }

            Alien1.Bomb bomb = ((Alien1) enemy).getBomb();

            int chance = randomizer.nextInt(15);

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
            }

            if (!bomb.isDestroyed()) {
                bomb.setX(bomb.getX() - 4);
            }

            // Rect overlap with the real (scaled) image sizes, not the
            // unscaled PLAYER_WIDTH/HEIGHT constants
            if (player.isVisible() && !bomb.isDestroyed()
                    && bomb.collidesWith(player)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
            }

            if (!bomb.isDestroyed() && bomb.getX() < 0) {
                bomb.setDestroyed(true);
            }
        }
    }

    private void doGameCycle() {
        frame++;
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
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < player.getMaxShots()) {
                    // Create a new shot and add it to the list
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }

        }
    }
}
