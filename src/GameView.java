import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;


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
        getName = new JTextField(50);
        getName.setText("Enter your name: ");
        getName.setPreferredSize(new Dimension(250, 60));
        getName.setBackground(new Color(184,230,242));
        getName.setForeground(new Color(51,102,119));
        getName.setFont(sansNiam);
        setLayout(new FlowLayout());
        getName.setVisible(false);
        add(getName);
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
        g.drawImage(background[RACE],0,0,this);
        for (Horse horse : horses) {
            horse.draw(g);
        }
//        switch (state) {
//            case START:
//                drawStart(g);
//                break;
//            case CHOOSE_HORSE:
//                //drawChoose(g);
//                break;
//            case ENTER_NAME:
//                //drawEnter(g);
//                break;
//            case RACE:
//                //drawRace(g);
//                break;
//            case WINNER:
//                //drawEnd(g);
//                break;
//            case HIGH_SCORE:
//                //drawScore(g);
//        }
    }

//    public void drawStart(Graphics g) {
//        g.drawImage(background[START],0,0,this);
//        g.setFont(sansNiam);
//        g.drawString("PLAY",250,350);
//        g.drawString("HIGH SCORES",650,350);
//    }

//    public void drawChoose(Graphics g) {
//        g.drawImage(background[CHOOSE_HORSE],0,0,this);
//        g.setFont(sansNiam);
//        g.drawImage(horse[1],0,0,this);
//        g.drawImage(horse[2],714,0,this);
//        g.drawImage(horse[3],0,315,0,this);
//        g.drawImage(horse[4[,714,0,this);
}


