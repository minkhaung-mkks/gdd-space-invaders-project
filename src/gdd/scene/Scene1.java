package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.LevelLoader;
import gdd.SpawnDetails;
import gdd.powerup.BigShot;
import gdd.powerup.Heal;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.powerup.SplitShot;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Boss;
import gdd.sprite.BossProjectile;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Mage;
import gdd.sprite.MageFire;
import gdd.sprite.Obstacle;
import gdd.sprite.Player;
import gdd.sprite.Satellite;
import gdd.sprite.Shot;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Satellite> satellites;
    private int deathTimer = 0;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<Obstacle> obstacles;
    private List<BossProjectile> bossShots;
    private List<MageFire> mageShots;
    private Boss boss;
    private Player player;
    // private Shot shot;
    private Image bgBack;
    private Image bgFar;
    private Image bgMidFloor;   // middle_bottom.png — mountain on the floor
    private Image bgMidCeiling; // middle_top.png — rocks on the ceiling

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
    private Image[] tiles; // tile image per grid id (index 1..7)

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
            String filePath = "src/audio/scene1_final.wav";
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
        loadTerrainTiles();
    }

    // Load each terrain tile image, scaled to TILE size, indexed by grid id
    private void loadTerrainTiles() {
        tiles = new Image[IMG_TERRAIN_TILES.length];
        for (int id = 1; id < IMG_TERRAIN_TILES.length; id++) {
            try {
                BufferedImage img = ImageIO.read(new File(IMG_TERRAIN_TILES[id]));
                tiles[id] = img.getScaledInstance(TILE, TILE, Image.SCALE_SMOOTH);
            } catch (Exception e) {
                System.err.println("Error loading tile " + id + ": " + e.getMessage());
            }
        }
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
        satellites = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        obstacles = new ArrayList<>();
        bossShots = new ArrayList<>();
        mageShots = new ArrayList<>();

        // Darken each layer 
        bgBack = darken(IMG_BG_BACK, 0.45f);
        bgFar = darken(IMG_BG_FAR, 0.55f);

        bgMidFloor = darken(IMG_BG_MID_BOTTOM, 0.85f);
        bgMidCeiling = darken(IMG_BG_MID_TOP, 0.85f);

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

    private BufferedImage darken(String path, float amount) {
        Image src = new ImageIcon(path).getImage();
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, amount));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return bi;
    }

    private void drawMap(Graphics g) {
        // Three parallax layers, far to near. Farther layers scroll slower.
        drawParallaxLayer(g, bgBack, frame, 1.0, false);          // full height
        drawParallaxLayer(g, bgFar, frame * 2, 1.0, false);
        drawParallaxLayer(g, bgMidCeiling, frame * 3, 0.45, true);  // ceiling rocks
        drawParallaxLayer(g, bgMidFloor, frame * 3, 0.45, false);   // floor mountain
    }

    private void drawParallaxLayer(Graphics g, Image img, int scroll,
            double heightFrac, boolean anchorTop) {
        if (img == null) {
            return;
        }
        int ih = img.getHeight(null);
        int iw = img.getWidth(null);
        if (ih <= 0 || iw <= 0) {
            return;
        }

        int drawH = (int) (BOARD_HEIGHT * heightFrac);
        int tileW = iw * drawH / ih;              // keep aspect ratio
        int offset = scroll % tileW;
        int y = anchorTop ? 0 : (BOARD_HEIGHT - drawH);

        for (int x = -offset; x < BOARD_WIDTH; x += tileW) {
            g.drawImage(img, x, y, tileW, drawH, this);
        }
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

        Image t = (tiles != null && tile >= 1 && tile < tiles.length) ? tiles[tile] : null;
        if (t != null) {
            g.drawImage(t, x, y, TILE, TILE, this);
            return;
        }

        // Fallback flat color if a tile image is missing
        g.setColor(new Color(90, 65, 45));
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

                // Alien2 hangs from the ceiling on a thin thread
                if (enemy instanceof Alien2) {
                    int cx = enemy.getX() + enemy.getImage().getWidth(null) / 2;
                    g.setColor(Color.white);
                    g.drawLine(cx, 0, cx, enemy.getY());
                }

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);

                // Floating health bar, only once the enemy has taken damage
                if (!enemy.isFullHealth() && !enemy.isDying()) {
                    int w = enemy.getImage().getWidth(null);
                    drawHealthBar(g, enemy.getX(), enemy.getY() - 8, w, 4,
                            enemy.getHealthFraction());
                }
            }

            if (enemy.isDying() && !enemy.isDeathAnimating()) {

                enemy.die();
            }
        }
    }

    private void drawMageShots(Graphics g) {
        for (MageFire mf : mageShots) {
            if (mf.isVisible()) {
                g.drawImage(mf.getImage(), mf.getX(), mf.getY(), this);
            }
        }
    }

    private void drawBoss(Graphics g) {
        if (boss != null && boss.isVisible()) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);
        }
    }

    private void drawBossShots(Graphics g) {
        for (BossProjectile bp : bossShots) {
            if (!bp.isVisible()) {
                continue;
            }
            if (bp.isWarning()) {
                // "!" telegraph + guide line down the column the meteor drops from
                int mx = bp.getX() + 20;
                g.setColor(new Color(255, 40, 40, 90));
                g.fillRect(mx - 2, 60, 4, BOARD_HEIGHT - 60);
                g.setColor(Color.RED);
                g.setFont(new Font("Helvetica", Font.BOLD, 40));
                g.drawString("!", mx - 8, 100);
            } else {
                g.drawImage(bp.getImage(), bp.getX(), bp.getY(), this);
            }
        }
    }

    // Boss name + full-width health bar across the top
    private void drawBossBar(Graphics g) {
        if (boss == null || !boss.isVisible()) {
            return;
        }
        int bw = BOARD_WIDTH - 120;
        int bx = 60;
        int by = 25;

        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.BOLD, 14));
        int nameW = g.getFontMetrics().stringWidth(Boss.NAME);
        g.drawString(Boss.NAME, (BOARD_WIDTH - nameW) / 2, by - 5);

        drawHealthBar(g, bx, by, bw, 14, boss.getHealthFraction());
    }

    // Generic bar: dark background, green-to-red fill by fraction
    private void drawHealthBar(Graphics g, int x, int y, int w, int h, double frac) {
        if (frac < 0) {
            frac = 0;
        }
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, w, h);
        g.setColor(frac > 0.5 ? Color.GREEN : (frac > 0.25 ? Color.ORANGE : Color.RED));
        g.fillRect(x, y, (int) (w * frac), h);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
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

    }

    private void drawSatellites(Graphics g) {

        for (Satellite s : satellites) {
            g.drawImage(s.getImage(), s.getX(), s.getY(), this);
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

            if (e instanceof Alien2) {
                Alien2.Bomb bomb = ((Alien2) e).getBomb();
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

        drawPlayerHealth(g);
        drawSpeedUpTracker(g);
        drawMultiShotTracker(g);
        drawSplitShotTracker(g);
        drawBigShotTracker(g);
    }

    // Player HP as red diagonal slashes: / / / / /
    private void drawPlayerHealth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(Color.white);
        g.drawString("HP", 10, 102);

        int startX = 35;
        int top = 90;
        int height = 14;
        int slotGap = 12;

        Composite old = g2.getComposite();
        g2.setStroke(new BasicStroke(4));
        for (int i = 0; i < player.getMaxHealth(); i++) {
            int x = startX + i * slotGap;
            if (i < player.getHealth()) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(new Color(70, 70, 70));
            }
            g2.drawLine(x, top + height, x + 8, top); // diagonal "/"
        }
        g2.setStroke(new BasicStroke(1));
        g2.setComposite(old);
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
        var ii = new ImageIcon(IMG_POWERUP_MULTI);
        Image icon = ii.getImage();

        int iconSize = 20;
        int startX = 80;
        int startY = 65;
        int gap = 25;

        for (int i = 0; i < 4; i++) {
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

    private void drawSplitShotTracker(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        Image icon = new ImageIcon(IMG_POWERUP_SPLIT).getImage();

        int iconSize = 20;
        int startX = 190;
        int startY = 65;
        int gap = 25;

        for (int i = 0; i < 2; i++) {
            int x = startX + i * gap;
            g2d.drawImage(icon, x, startY, iconSize, iconSize, this);

            if (i >= player.getSplitShotCount()) {
                Composite oldComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                g2d.setColor(Color.black);
                g2d.fillRect(x, startY, iconSize, iconSize);
                g2d.setComposite(oldComposite);
            }
        }
    }

    private void drawBigShotTracker(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        Image icon = new ImageIcon(IMG_POWERUP_BIG).getImage();

        int iconSize = 20;
        int startX = 260;
        int startY = 65;
        int gap = 25;

        for (int i = 0; i < 2; i++) {
            int x = startX + i * gap;
            g2d.drawImage(icon, x, startY, iconSize, iconSize, this);

            if (i >= player.getBigShotCount()) {
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
            drawMageShots(g);
            drawBoss(g);
            drawBossShots(g);
            drawBombing(g);
            drawPlayer(g);
            drawSatellites(g);
            drawShot(g);
            drawDashboard(g);
            drawBossBar(g);

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
                    Enemy enemy2 = new Alien2(sd.x, sd.y);
                    enemies.add(enemy2);
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
                case "PowerUp-SplitShot":
                    PowerUp splitShot = new SplitShot(sd.x, sd.y);
                    powerups.add(splitShot);
                    break;
                case "PowerUp-BigShot":
                    PowerUp bigShot = new BigShot(sd.x, sd.y);
                    powerups.add(bigShot);
                    break;
                case "PowerUp-Heal":
                    PowerUp heal = new Heal(sd.x, sd.y);
                    powerups.add(heal);
                    break;
                case "Obstacle":
                    obstacles.add(new Obstacle(sd.x, sd.y));
                    break;
                case "Boss":
                    boss = new Boss(sd.x, sd.y);
                    break;
                case "Mage":
                    enemies.add(new Mage(sd.x, sd.y));
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
            player.setDying(true);
        }

       
        // wait for the explosion to finish before showing the game over screen
        if (player.isDying() && player.isVisible()) {
            explosions.add(new Explosion(player.getX(), player.getY(),
                    IMG_VFX_PLAYER_EXPLOSION));
            player.die();
            deathTimer = IMG_VFX_PLAYER_EXPLOSION.length * 4;
        }
        if (deathTimer > 0) {
            deathTimer--;
            if (deathTimer == 0) {
                inGame = false;
            }
        }

        while (satellites.size() < player.getSplitShotCount()) {
            satellites.add(new Satellite(satellites.isEmpty() ? -1 : 1));
        }
        for (Satellite s : satellites) {
            s.setLevel(player.getBigShotCount());
            s.follow(player);
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

        // Enemies — stopped by terrain, despawned once off screen.
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                int ox = enemy.getX();
                int oy = enemy.getY();

                // Alien2 follows the player up and down
                if (enemy instanceof Alien2) {
                    ((Alien2) enemy).track(player.getY());
                }

                enemy.act(direction);

                // Collect any projectiles a Mage fired this frame
                if (enemy instanceof Mage) {
                    mageShots.addAll(((Mage) enemy).takeProjectiles());
                }

                int w = enemy.getImage().getWidth(null);
                int h = enemy.getImage().getHeight(null);

                // Alien2 is pinned to its thread, so terrain must not shove it
                if (!(enemy instanceof Alien2)
                        && hitsTerrain(enemy.getX(), enemy.getY(), w, h)) {
                    enemy.setX(ox - TERRAIN_SPEED);
                    enemy.setY(oy);
                }

                // Despawn once fully off the left of the screen
                if (enemy.getX() + w < 0) {
                    enemy.die();
                }
            }

            if (!enemy.isVisible()) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);

        // Mage projectiles: move and hit the player
        List<MageFire> mageShotsToRemove = new ArrayList<>();
        for (MageFire mf : mageShots) {
            if (!mf.isVisible()) {
                mageShotsToRemove.add(mf);
                continue;
            }
            mf.act();
            if (player.isVisible() && !player.isDying() && mf.collidesWith(player)) {
                player.damage(1);
                mf.die();
                mageShotsToRemove.add(mf);

            }
        }
        mageShots.removeAll(mageShotsToRemove);

        // Boss
        if (boss != null && boss.isVisible()) {
            boss.update(player.getX(), player.getY());
            bossShots.addAll(boss.takeProjectiles());

            if (boss.isDying()) {
                boss.die();
                explosions.add(new Explosion(boss.getX() + 40, boss.getY() + 40));
                explosions.add(new Explosion(boss.getX() + 80, boss.getY() + 60));
                score += 500;
                inGame = false;
                timer.stop();
                message = boss.NAME + " defeated!";
            }
        }

        // Boss projectiles: steer (homing), move, and hit the player
        List<BossProjectile> bossShotsToRemove = new ArrayList<>();
        for (BossProjectile bp : bossShots) {
            if (!bp.isVisible()) {
                bossShotsToRemove.add(bp);
                continue;
            }
            bp.steer(player.getX(), player.getY());
            bp.act();

            if (!bp.isWarning() && player.isVisible() && !player.isDying()
                    && bp.collidesWith(player)) {
                player.damage(1);
                bp.die();
                bossShotsToRemove.add(bp);

            }
        }
        bossShots.removeAll(bossShotsToRemove);

        // Obstacles
        List<Obstacle> obstaclesToRemove = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isVisible()) {
                obstacle.act();

                // Flying into it sets it off and hurts the ship
                if (player.isVisible() && !player.isDying()
                        && obstacle.collidesWith(player)) {
                    explosions.add(new Explosion(obstacle.getX(), obstacle.getY(),
                            IMG_VFX_OBSTACLE_EXPLOSION));
                    obstacle.die();
                    player.damage(1);
                }

                if (obstacle.getX() < -100) {
                    obstacle.die();
                }
            }

            if (!obstacle.isVisible()) {
                obstaclesToRemove.add(obstacle);
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

                        enemy.damage(1);
                        shot.die();
                        shotsToRemove.add(shot);

                        // Only explode and score when the hit was fatal
                        if (enemy.isDying()) {
                            // Enemies that animate their own death keep their
                            // sprite; the rest swap to the explosion image.
                            if (!enemy.isDeathAnimating()) {
                                var ii = new ImageIcon(IMG_VFX_EXPLOSION[0]);
                                enemy.setImage(ii.getImage());
                                explosions.add(new Explosion(enemyX, enemyY));
                            }
                            AudioPlayer.playSound("src/audio/alien_dead.wav", 0f);
                            deaths++;
                            score += 10;
                        }
                    }
                }

                // Shot vs boss
                if (boss != null && boss.isVisible() && shot.isVisible()
                        && shot.collidesWith(boss)) {
                    boss.damage(1);
                    shot.die();
                    shotsToRemove.add(shot);
                }

                // Obstacles block shots, and break after enough hits
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.isVisible() && shot.isVisible()
                            && shot.collidesWith(obstacle)) {
                        shot.die();
                        shotsToRemove.add(shot);

                        obstacle.damage(1);
                        if (obstacle.isDying()) {
                            explosions.add(new Explosion(obstacle.getX(), obstacle.getY(),
                                    IMG_VFX_OBSTACLE_EXPLOSION));
                            obstacle.die();
                        }
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
                ((Alien1) enemy).notifyShoot(); // play the SHOOT animation
                // fired often, so keep it quieter than the music
                AudioPlayer.playSound("src/audio/alien_shoot.wav", -8f);
            }

            if (!bomb.isDestroyed()) {
                bomb.setX(bomb.getX() - 4);
            }

            // Rect overlap with the real (scaled) image sizes, not the
            // unscaled PLAYER_WIDTH/HEIGHT constants
            if (player.isVisible() && !bomb.isDestroyed()
                    && bomb.collidesWith(player)) {

                player.damage(1);
                bomb.setDestroyed(true);

                // Explosion sprite only on the fatal hit

            }

            if (!bomb.isDestroyed() && bomb.getX() < 0) {
                bomb.setDestroyed(true);
            }
        }

        // Same again for Alien2, which has its own bomb
        for (Enemy enemy : enemies) {

            if (!(enemy instanceof Alien2)) {
                continue;
            }

            Alien2.Bomb bomb = ((Alien2) enemy).getBomb();

            int chance = randomizer.nextInt(15);

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
                ((Alien2) enemy).notifyShoot(); // play the ATTACK animation
                AudioPlayer.playSound("src/audio/alien_shoot.wav", -8f);
            }

            // Faster than the Alien1 bomb
            if (!bomb.isDestroyed()) {
                bomb.setX(bomb.getX() - 7);
            }

            if (player.isVisible() && !bomb.isDestroyed()
                    && bomb.collidesWith(player)) {

                player.damage(1);
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

                int bullets = player.getSplitShotCount() + 1;

                if (shots.size() < player.getMaxShots() * bullets) {
                    int level = player.getBigShotCount();

                    shots.add(new Shot(x, y, level));
                    for (Satellite s : satellites) {
                        shots.add(new Shot(x, y + s.getSide() * Satellite.OFFSET, level));
                    }
                    AudioPlayer.playSound("src/audio/player_shoot.wav", -6f);
                }
            }

        }
    }
}
