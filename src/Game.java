import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class Game implements ActionListener {
    private Person player;
    private Scanner input;
    private double betAmount;
    private GameView window;
    private int state;
    private Horse[] horses;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String choice;
    private Timer timer;
    File stats;
    File horseStats;
    File names;
    File namesCopy;
    public Game() {
        this.input = new Scanner(System.in);
        stats = new File("stats.csv");
        horseStats = new File("horseStats.csv");
        names = new File("Resources/names.txt");
        namesCopy = new File("Resources/CopiedNames.txt");
        timer = new Timer(100,this);
        try {
            Files.copy(names.toPath(),namesCopy.toPath(),java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.horses = new Horse[4];
        for (int i = 0; i < 4; i++) {
            horses[i] = new Horse(window, 0,0,findName());
        }
        state = GameView.START;
        try {
            reader = new BufferedReader(new FileReader("stats.csv"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            writer = new BufferedWriter(new FileWriter("stats.csv",true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        calculateOdds();
        window = new GameView(this);
        window.setHorses();
    }

    public void calculateOdds() {
        double sum = 0;
        for (Horse horse : horses) {
            sum += horse.getRate();
        }
        for (int i = 0; i < 4; i++) {
            double odds =  horses[i].getRate() / sum;
            horses[i].setOdds(odds);
            if (odds >= 0.5) {
                 horses[i].setBettingOdds(-(int) (100 * odds / (1 - odds)));
            } else {
                horses[i].setBettingOdds((int) (100 * (1 - odds) / odds));
            }
        }

    }


    public Horse getHorses(int num) {
        return horses[num];
    }

    public BufferedWriter getWriter() {
        return writer;
    }


    public String findName() {
        ArrayList<String> names = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Resources/CopiedNames.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int index = (int)(Math.random() * names.size());
        String name = names.get(index);
        names.remove(index);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Resources/CopiedNames.txt"))) {
            for (String word : names) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return name;
    }


    public void runRace() {
        for (int i = 0; i < 4; i++) {
            horses[i].setX(horses[i].getX() + horses[i].getRate());
            horses[i].changeRate();
        }

    }

    public boolean checkWinner() {
        for (Horse horse : horses) {
            if (horse.getX() >= 400) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
    public static void main(String[] args) {
        Game game = new Game();
        for (int i = 0; i < 4; i ++) {
            System.out.println(game.getHorses(i).getName());

        }
        try {
            game.getWriter().write('c');
            game.getWriter().flush();
            game.getWriter().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
