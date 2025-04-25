import java.awt.*;

public class Horse {
    private int x;
    private int y;
    private String name;
    private GameView window;
    private int rate;
    private double odds;
    private int bettingOdds;
    private int state;
    private int horseNumber;
    public Horse(GameView window, int x, int y, String name, int number) {
        this.x = x;
        this.y = y;
        this.window = window;
        this.name = name;
        rate = (int) (Math.random() * 10 + 5);
        odds = 0;
        bettingOdds = 0;
        state = 0;
        horseNumber = number;
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
        System.out.println("+" + bettingOdds);
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
        else if (odds < 0.4) {
            rate += (int) (Math.random() * 8) - 4;
        }
        else {
            rate += (int) (Math.random() * 10) - 5;
        }
        if (rate < 1) {
            rate = 1;
        }
    }
    public void changeState() {
        if (state == 1) {
            state = 0;
        }
        else {
            state = 1;
        }
    }

    public void draw(Graphics g) {
        g.drawImage(window.getHorsePicture(horseNumber),x,y,x+250,y+125,0,0,500,250,window);
    }

    public void drawChoose(Graphics g,int x,int y) {
        g.drawImage(window.getHorsePicture(horseNumber),x,y,x+125,y+250,0,0,500,250,window);
    }
}
