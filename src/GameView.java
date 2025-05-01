import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.lang.Thread;


public class GameView extends JFrame {
    private final int WINDOW_HEIGHT = 500;
    private final int WINDOW_WIDTH = 1000;
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

    public GameView(Game game) {
        this.game = game;
        try {
            sansNiam = Font.createFont(Font.TRUETYPE_FONT, new File("Resources/PixelifySans.ttf"));
            sansNiam = sansNiam.deriveFont(Font.PLAIN, 24);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Horse Game");
        this.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        horses = new Horse[4];
        horse = new Image[4];
        for (int i = 0; i < 4; i++) {
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

    }

    public void setState (int state) {
        this.state = state;
        repaint();
    }

    public void initializeJTextField() {
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
        getName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = getName.getText().substring(getName.getText().indexOf(":") + 2);
                game.setPlayerName(name);
                getName.setVisible(false);
                getWager.setVisible(true);
            }
        });
        getWager.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String wager = getWager.getText().substring(getWager.getText().indexOf(":") + 2);
                double bet;
                try {
                    bet = Double.parseDouble(wager);
                    System.out.println(bet);

                } catch (NumberFormatException error) {
                    getWager.setText("Please enter a number: ");
                    return;
                }
                if (bet > game.getPlayer().getBalance()) {
                    getWager.setText("Please enter a wager less than $" + game.getPlayer().getBalance() + ": ");
                    return;
                }
                if (bet < 0) {
                    getWager.setText("Please enter a positive number: ");
                    return;
                }
                game.getPlayer().setWager(bet);
                System.out.println(game.getPlayer().getWager());
                getWager.setVisible(false);
                setState(RACE);
                game.getTimer().start();

            }

        });
    }

    public String getGetName() {
        return getName();
    }

    public void setHorses() {
        // Sets die because it is originally null
        for (int i = 0; i < 4; i++) {
            this.horses[i] = game.getHorses(i);
        }
        this.setVisible(true);
    }

    public Image getHorsePicture(int num) {
        return horse[num];
    }

    public void paint(Graphics g) {
        switch (state) {
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
                //drawScore(g);
        }
    }

    @Override
    public int getState() {
        return state;
    }

    public void drawStart(Graphics g) {
        g.drawImage(background[START],0,0,this);
        g.setFont(sansNiam);
        g.drawString("PLAY",250,350);
        g.drawString("HIGH SCORES",650,350);
    }

    public void drawChoose(Graphics g) {
        g.drawImage(background[CHOOSE_HORSE],0,0,this);
        g.setFont(sansNiam);
        g.drawImage(horse[0],20,20,220,120,0,0,500,250,this);
        g.drawString(horses[0].getName(),20,150);
        g.drawString(horses[0].getBettingOdds(),20,175);
        g.drawImage(horse[1],744,20,944,120,0,0,500,250,this);
        g.drawString(horses[1].getName(),744,150);
        g.drawString(horses[1].getBettingOdds(),744,175);
        g.drawImage(horse[2],20,315,220,415,0,0,500,250,this);
        g.drawString(horses[2].getName(),20,445);
        g.drawString(horses[2].getBettingOdds(),20,470);
        g.drawImage(horse[3],744,315,944,415,0,0,500,250,this);
        g.drawString(horses[3].getName(),744,445);
        g.drawString(horses[3].getBettingOdds(),744,470);
    }

    public void drawEnter(Graphics g) {
        g.drawImage(background[ENTER_NAME],0,0,this);
        getName.setVisible(true);
        getWager.setVisible(true);

    }

    public void drawRace(Graphics g) {
        g.drawImage(background[RACE],0,0,this);
        for (Horse horse : horses) {
            horse.draw(g);
        }
    }

    public void drawEnd(Graphics g) {
        g.setFont(new Font("serif",Font.ITALIC + Font.BOLD + Font.CENTER_BASELINE,80));
        g.drawImage(background[WINNER],0,0,this);
        g.drawString(game.getWinner().getName() + " Won!",200,200);
        g.drawImage(horse[game.getWinner().getHorseNumber()],300,250,740,450,0,0,500,250,this);
    }

    public void drawScore(Graphics g) {
        g.setFont(sansNiam);
        g.drawImage(background[HIGH_SCORE],0,0,this);
        g.drawString("",0,0);

    }
}


