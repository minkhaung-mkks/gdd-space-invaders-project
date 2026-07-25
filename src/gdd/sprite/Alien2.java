package gdd.sprite;

public class Alien2 extends Enemy {

    private int counter;

    public Alien2(int x, int y) {
        super(x, y);
    }

    public void act(int direction) {
        counter++;
        this.x -= 2;
        this.y += (int) (2 * Math.sin(counter * 0.05));
    }
}
