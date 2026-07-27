package gdd;

import gdd.scene.IntroScene;
import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Game extends JFrame  {

    TitleScene titleScene;
    IntroScene introScene;
    Scene1 scene1;
    Scene2 scene2;

    private gdd.sprite.Player carriedPlayer;

    public void setCarriedPlayer(gdd.sprite.Player p) {
        carriedPlayer = p;
    }

    public gdd.sprite.Player getCarriedPlayer() {
        return carriedPlayer;
    }

    public Game() {
        titleScene = new TitleScene(this);
        introScene = new IntroScene(this);
        scene1 = new Scene1(this);
        scene2 = new Scene2(this);
        initUI();
        loadTitle();
    }

    private void initUI() {

        setTitle("Space Invaders");
        getContentPane().setPreferredSize(new Dimension(Global.BOARD_WIDTH,
                Global.BOARD_HEIGHT));
        pack();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

    }

    public void loadTitle() {
        getContentPane().removeAll();
        add(titleScene);
        introScene.stop();
        scene1.stop();
        scene2.stop();
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadIntro() {
        getContentPane().removeAll();
        add(introScene);
        titleScene.stop();
        introScene.start();
        revalidate();
        repaint();
    }

    // Stage 1
    public void loadScene1() {
        getContentPane().removeAll();
        add(scene1);
        titleScene.stop();
        introScene.stop();
        scene1.start();
        revalidate();
        repaint();
    }

    // Stage 2 — the boss stage
    public void loadScene2() {
        getContentPane().removeAll();
        add(scene2);
        titleScene.stop();
        introScene.stop();
        scene1.stop();
        scene2.start();
        revalidate();
        repaint();
    }
}
