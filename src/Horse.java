import java.awt.*;

public class Horse {
    private int x;
    private int y;
    private String name;
    private GameView window;
    private int rate;
    private double odds;
    private int bettingOdds;
    public Horse(GameView window, int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.window = window;
        this.name = name;
        rate = (int) (Math.random() * 10 + 5);
        odds = 0;
        bettingOdds = 0;
    }
    public String getName() {
        return name;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }
    public int getRate() {
        return rate;
    }

    public int getBettingOdds() {
        return bettingOdds;
    }

    public void setBettingOdds(int bettingOdds) {
        this.bettingOdds = bettingOdds;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void changeRate() {
        if (odds < 0.2) {
            rate += (int) (Math.random() * 6) - 3;
        }
        if (odds < 0.4) {
            rate += (int) (Math.random() * 8) - 4;
        }
        else {
            rate += (int) (Math.random() * 10) - 5;
        }
        if (rate < 1) {
            rate = 1;
        }
    }

    public void draw(Graphics g) {

    }


}
