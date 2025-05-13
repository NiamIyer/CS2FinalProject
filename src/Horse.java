import java.awt.*;

public class Horse {
    public static final int HORSE_HEIGHT = 125;
    public static final int HORSE_WIDTH = 250;
    // Coordinates
    private int x;
    private int y;
    private String name;
    private GameView window;
    // Speed of the horse
    private int rate;
    // The chances that the horse is going to win based off of the initial rate
    private double odds;
    private int bettingOdds;
    // Marks the order that the horse was in when initialized (for graphics)
    private int horseNumber;

    public Horse(GameView window, int x, int y, String name, int number)
    {
        this.x = x;
        this.y = y;
        this.window = window;
        this.name = name;
        // Randomly assigns the rate of each horse to determine the odds
        rate = (int) (Math.random() * 15 + 5);
        odds = 0;
        bettingOdds = 0;
        horseNumber = number;
    }

    public String getName()
    {
        return name;
    }

    public void setOdds(double odds)
    {
        this.odds = odds;
    }
    public int getRate()
    {
        return rate;
    }

    public String getBettingOdds()
    {
        if (odds > 0.5)
        {
            return Integer.toString(bettingOdds);
        }
        else
        {
            return "+" + bettingOdds;
        }
    }

    public int getHorseNumber()
    {
        return horseNumber;
    }

    public void setBettingOdds(int bettingOdds)
    {
        this.bettingOdds = bettingOdds;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }
    // Changes the speed of the horse depending on the odds (which relates to the additional rate)

    public void changeRate()
    {
        if (odds < 0.2)
        {
            rate += (int) (Math.random() * 6) - 3;
        }
        else if (odds < 0.4)
        {
            rate += (int) (Math.random() * 8) - 4;
        }
        else
        {
            rate += (int) (Math.random() * 10) - 6;
        }
        if (rate < 1)
        {
            rate = 1;
        }
    }

    public void draw(Graphics g)
    {
        g.drawImage(window.getHorsePicture(horseNumber), x, y, x + HORSE_WIDTH, y + HORSE_HEIGHT, 0, 0, 500, 250,
                window);
    }
}
