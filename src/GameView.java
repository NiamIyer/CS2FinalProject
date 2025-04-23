import java.awt.*;
import javax.swing.*;


public class GameView extends JFrame {
    private final int WINDOW_HEIGHT = 500;
    private final int WINDOW_WIDTH = 1000;
    public static final int START = 0;
    public static final int CHOOSE = 1;
    public static final int RACE = 2;
    public static final int WINNER = 3;
    public static final int HIGH_SCORE = 4;
    private Image[] horse;
    private Image[] background;
    private Game game;
    private Horse[] horses;
    private Font sansNiam;
    public GameView(Game game) {
        this.game = game;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Horse Game");
        this.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        horses = new Horse[4];
        horse = new Image[4];
        for (int i = 0; i < 4; i++) {
            horse[i] = new ImageIcon("Resources/Horse " + i + 1 + ".png").getImage();
        }
        background = new Image[5];
        this.setVisible(true);
        sansNiam = new Font("Sans Niam", Font.ITALIC + Font.BOLD,30);

    }

    public void setHorses() {
        // Sets die because it is originally null
        for (int i = 0; i < 4; i++) {
            horses[i] = game.getHorses(i);
        }
    }
}
