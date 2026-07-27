package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 24; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 24;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 30; // Doubled from 15
    public static final int PLAYER_HEIGHT = 20; // Doubled from 10

    // Images
    public static final String IMG_PLAYER = "src/images/player.png";
    public static final String IMG_PLAYER_SHEET = "src/images/player_sheet.png";
    public static final String IMG_TITLE = "src/images/title.jpg";
    public static final String IMG_GAME_OVER = "src/images/game_over.jpg";

    public static final String[] IMG_GAME_WIN = {
        "src/images/game_win_1.jpg",
        "src/images/game_win_2.jpg",
        "src/images/game_win_3.jpg",
        "src/images/game_win_4.jpg"
    };
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup_speed.png";
    public static final String IMG_POWERUP_SPLIT = "src/images/powerup_split.png";
    public static final String IMG_POWERUP_BIG = "src/images/powerup_big.png";
    public static final String IMG_POWERUP_MULTI = "src/images/powerup_multi.png";
    public static final String IMG_POWERUP_HEAL = "src/images/powerup_heal.png";
    public static final String IMG_MAGE = "src/images/mage.png";
    public static final String IMG_ALIEN1_SHEET = "src/images/alien1_packed.png";

    public static final String IMG_BOSS = "src/images/boss_packed.png";

    public static final String IMG_VFX_BOSS_BOMB = "src/images/boss_bomb.png";
    public static final String IMG_VFX_METEOR = "src/images/meteor.png";
    public static final String IMG_VFX_MAGE_FIREBALL = "src/images/mage_fireball.png";
    public static final String IMG_VFX_ALIEN_BOMB = "src/images/alien_bomb.png";

    public static final String[] IMG_VFX_CRESCENT = {
        "src/images/crescent_1.png",
        "src/images/crescent_2.png",
        "src/images/crescent_3.png"
    };

    public static final String IMG_OBSTACLE = "src/images/obstacle.png";
    public static final String IMG_ALIEN2_SHEET = "src/images/alien_2.png";
    public static final String IMG_VFX_ALIEN2_BOMB = "src/images/alien2_bomb.png";

    public static final String[] IMG_VFX_PLAYER_EXPLOSION = {
        "src/images/player_explosion_1.png",
        "src/images/player_explosion_2.png",
        "src/images/player_explosion_3.png"
    };

    public static final String[] IMG_VFX_OBSTACLE_EXPLOSION = {
        "src/images/obstacle_explosion_1.png",
        "src/images/obstacle_explosion_2.png",
        "src/images/obstacle_explosion_3.png",
        "src/images/obstacle_explosion_4.png"
    };

    public static final String[] IMG_VFX_EXPLOSION = {
        "src/images/explosion_1.png",
        "src/images/explosion_2.png",
        "src/images/explosion_3.png"
    };

    public static final String[] IMG_VFX_SHOT = {
        "src/images/shot_1.png",
        "src/images/shot_2.png",
        "src/images/shot_3.png"
    };

    public static final String[] IMG_VFX_SPLIT = {
        "src/images/split_1.png",
        "src/images/split_2.png",
        "src/images/split_3.png"
    };

    // Parallax background layers, far to near
    public static final String IMG_BG_BACK = "src/images/back.png";
    public static final String IMG_BG_FAR = "src/images/far.png";
    public static final String IMG_BG_MID_TOP = "src/images/middle_top.png";      
    public static final String IMG_BG_MID_BOTTOM = "src/images/middle_bottom.png";

    // Terrain tiles, indexed by grid id 1..7 (0 = empty)
    public static final String[] IMG_TERRAIN_TILES = {
        null,                                 // 0 = empty
        "src/images/terrain_cave_left.png",   // 1 cave block, left edge
        "src/images/terrain_stone.png",       // 2 stone
        "src/images/terrain_cave_upper.png",  // 3 cave upper
        "src/images/terrain_cave_lower.png",  // 4 cave lower
        "src/images/terrain_cave_middle.png", // 5 cave middle
        "src/images/terrain_cave_right.png",  // 6 cave block, right edge
        "src/images/terrain_cave_mid.png"     // 7 cave block, middle
    };

    // Levels
    public static final String LEVEL_SCENE1_TERRAIN = "src/levels/scene1-terrain.csv";
    public static final String LEVEL_SCENE1_SPAWNS = "src/levels/scene1-spawns.csv";
}
