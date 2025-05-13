import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
// Inheritance
public class GameView extends JFrame {
    private final int WINDOW_HEIGHT = 500;
    private final int WINDOW_WIDTH = 1000;
    private final int LEFT_HORSE_X = 20;
    private final int RIGHT_HORSE_X = 744;
    private final int TOP_HORSE_Y = 20;
    private final int LOW_HORSE_Y = 315;
    // Different states
    public static final int START = 0;
    public static final int CHOOSE_HORSE = 1;
    public static final int ENTER_NAME = 2;
    public static final int RACE = 3;
    public static final int WINNER = 4;
    public static final int HIGH_SCORE = 5;
    private int state;
    private Image[] horse;
    private Image[] background;
    private Game game;
    private Horse[] horses;
    private Font sansNiam;
    private JTextField getName;
    private JTextField getWager;

    public GameView(Game game)
    {
        this.game = game;
        // Creates new font from google fonts
        try
        {
            sansNiam = Font.createFont(Font.TRUETYPE_FONT, new File("Resources/PixelifySans.ttf"));
            sansNiam = sansNiam.deriveFont(Font.PLAIN, 24);
        } catch (FontFormatException | IOException e)
        {
            throw new RuntimeException(e);
        }
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Horse Game");
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        horses = new Horse[4];
        horse = new Image[4];
        for (int i = 0; i < 4; i++)
        {
            horse[i] = new ImageIcon("Resources/Horse " + (i + 1) + ".png").getImage();
        }
        background = new Image[6];
        background[0] = new ImageIcon("Resources/Start.png").getImage();
        background[1] = new ImageIcon("Resources/ChooseHorse.png").getImage();
        background[2] = new ImageIcon("Resources/EnterName.png").getImage();
        background[3] = new ImageIcon("Resources/Race.png").getImage();
        background[4] = new ImageIcon("Resources/Winner.png").getImage();
        background[5] = new ImageIcon("Resources/HighScore.png").getImage();
        initializeJTextField();
        setVisible(true);
        createBufferStrategy(2);
    }

    public void setState(int state)
    {
        this.state = state;
        repaint();
    }
    // Sets the text fields for input
    public void initializeJTextField()
    {
        setLayout(new FlowLayout());
        getName = new JTextField(50);
        getName.setText("Enter your name: ");
        getName.setBounds(350, 200, 300, 40);
        getName.setBackground(new Color(184, 230, 242));
        getName.setForeground(new Color(51, 102, 119));
        getName.setFont(sansNiam);
        getName.setVisible(false);
        add(getName);
        getWager = new JTextField(50);
        getWager.setText("Enter your wager: ");
        getWager.setBounds(350, 200, 300, 40);
        getWager.setPreferredSize(new Dimension(250, 60));
        getWager.setBackground(new Color(184, 240, 242));
        getWager.setForeground(new Color(51, 102, 119));
        getWager.setFont(sansNiam);
        getWager.setVisible(false);
        add(getWager);
        // Checks if the user hits enter, then sets the name and shows wager textfield
        getName.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e)
            {
                String name = getName.getText().substring(getName.getText().indexOf(":") + 2);
                game.setPlayerName(name);
                getName.setVisible(false);
                getWager.setVisible(true);
            }
        });
        // Checks if the user enters valid input and sets the player's wager
        getWager.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e)
            {
                String wager = getWager.getText().substring(getWager.getText().indexOf(":") + 2);
                double bet;
                try
                {
                    bet = Double.parseDouble(wager);
                    // If there is an error, makes sure it is a number
                } catch (NumberFormatException error)
                {
                    getWager.setText("Please enter a number: ");
                    return;
                }
                // Loops through and checks if there are any problems
                if (bet > game.getPlayer().getBalance())
                {
                    getWager.setText("Please enter a wager less than $" +
                            game.getPlayer().getBalance() + ": ");
                    return;
                }
                if (bet < 0)
                {
                    getWager.setText("Please enter a positive number: ");
                    return;
                }

                game.getPlayer().setWager(bet);
                getWager.setVisible(false);
                setState(RACE);
                game.getTimer().start();
            }
        });
    }

    public void setHorses()
    {
        // Sets horse because it is originally null
        for (int i = 0; i < 4; i++)
        {
            this.horses[i] = game.getHorses(i);
        }
        this.setVisible(true);
    }

    public Image getHorsePicture(int num)
    {
        return horse[num];
    }

    public void myPaint(Graphics g)
    {
        switch (state)
        {
            case START:
                drawStart(g);
                break;
            case CHOOSE_HORSE:
                drawChoose(g);
                break;
            case ENTER_NAME:
                drawEnter(g);
                break;
            case RACE:
                drawRace(g);
                break;
            case WINNER:
                drawEnd(g);
                break;
            case HIGH_SCORE:
                drawScore(g);
        }
    }

    @Override public int getState()
    {
        return state;
    }

    public void drawStart(Graphics g)
    {
        // Draws the background and sets the 'buttons'
        g.drawImage(background[START], 0, 0, this);
        g.setFont(sansNiam);
        g.drawString("PLAY", 250, 350);
        g.drawString("HIGH SCORES", 650, 350);
    }

    public void drawChoose(Graphics g)
    {
        // Draws the horses in corners of the choose screen
        g.drawImage(background[CHOOSE_HORSE], 0, 0, this);
        g.setFont(sansNiam);
        g.drawImage(horse[0], LEFT_HORSE_X, TOP_HORSE_Y, 220, 120, 0, 0, 2 * (Horse.HORSE_WIDTH), 2 * (Horse.HORSE_HEIGHT), this);
        g.drawString(horses[0].getName(), LEFT_HORSE_X, 150);
        g.drawString(horses[0].getBettingOdds(), LEFT_HORSE_X, 175);
        g.drawImage(horse[1], RIGHT_HORSE_X, TOP_HORSE_Y, 944, 120, 0, 0, 2 * (Horse.HORSE_WIDTH), 2 * (Horse.HORSE_HEIGHT), this);
        g.drawString(horses[1].getName(), RIGHT_HORSE_X, 150);
        g.drawString(horses[1].getBettingOdds(), RIGHT_HORSE_X, 175);
        g.drawImage(horse[2], LEFT_HORSE_X, LOW_HORSE_Y, 220, 415, 0, 0, 2 * (Horse.HORSE_WIDTH), 2 * (Horse.HORSE_HEIGHT), this);
        g.drawString(horses[2].getName(), LEFT_HORSE_X, 445);
        g.drawString(horses[2].getBettingOdds(), LEFT_HORSE_X, 470);
        g.drawImage(horse[3], RIGHT_HORSE_X, LOW_HORSE_Y, 944, 415, 0, 0, 2 * (Horse.HORSE_WIDTH), 2 * (Horse.HORSE_HEIGHT), this);
        g.drawString(horses[3].getName(), RIGHT_HORSE_X, 445);
        g.drawString(horses[3].getBettingOdds(), RIGHT_HORSE_X, 470);
    }

    public void drawEnter(Graphics g)
    {
        g.drawImage(background[ENTER_NAME], 0, 0, this);
        getName.setVisible(true);
        getWager.setVisible(false);
    }

    public void drawRace(Graphics g)
    {
        // Draws the horses positions in the race state
        g.drawImage(background[RACE], 0, 0, this);
        for (Horse horse : horses)
        {
            horse.draw(g);
        }
    }

    public void drawEnd(Graphics g)
    {
        // Draws who wins
        g.setFont(new Font("serif", Font.ITALIC + Font.BOLD + Font.CENTER_BASELINE, 80));
        g.drawImage(background[WINNER], 0, 0, this);
        g.drawString(game.getWinner().getName() + " Won!", 200, 200);
        g.drawImage(horse[game.getWinner().getHorseNumber()], 300, 250, 740, 450, 0, 0, 2 * (Horse.HORSE_WIDTH), 2 * (Horse.HORSE_HEIGHT),
                this);
    }

    public void drawScore(Graphics g)
    {
        // Draws the highscore of both the horses and players
        g.setFont(sansNiam);
        g.drawImage(background[HIGH_SCORE], 0, 0, this);
        g.drawString("", 0, 0);
        for (int i = 0; i < 5; i++)
        {
            g.drawString(game.getHorseWinnerNames().get(i) + ": " +
                            game.getHorseWinnerCount().get(i),
                    100, 200 + 50 * i);
        }

        for (int i = 0; i < game.getPlayerWinnerNames().size(); i++)
        {
            if (i > 4)
            {
                break;
            }
            g.drawString(game.getPlayerWinnerNames().get(i) + ": " +
                            game.getPlayerWinnerBalances().get(i),
                    760, 200 + 50 * i);
        }
    }

    public void paint(Graphics g)
    {
        // Uses double buffering from Front-end Back-end
        BufferStrategy bf = this.getBufferStrategy();
        if (bf == null)
            return;

        Graphics g2 = null;

        try
        {
            g2 = bf.getDrawGraphics();
            // myPaint does the actual drawing, as described in ManyBallsView
            myPaint(g2);
        } finally
        {
            // It is best to dispose() a Graphics object when done with it.
            g2.dispose();
        }

        // Shows the contents of the backbuffer on the screen.
        bf.show();

        // Tell the System to do the Drawing now, otherwise it can take a few extra ms until
        // Drawing is done which looks very jerky
        Toolkit.getDefaultToolkit().sync();
    }
}
