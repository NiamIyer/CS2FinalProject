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
        window = new GameView(this);
        this.input = new Scanner(System.in);
        stats = new File("stats.csv");
        horseStats = new File("horseStats.csv");
        names = new File("Resources/names.txt");
        namesCopy = new File("Resources/CopiedNames.txt");
        timer = new Timer(100,this);
        player = new Person("Niam");
        try {
            Files.copy(names.toPath(),namesCopy.toPath(),java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.horses = new Horse[4];
        for (int i = 0; i < 4; i++) {
            horses[i] = new Horse(window, 0,(i * 110) + 25,findHorseName(),i);
        }
        window.setState(GameView.START);
        // AI
        try {
            reader = new BufferedReader(new FileReader("stats.csv"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // AI
        try {
            writer = new BufferedWriter(new FileWriter("stats.csv",true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        calculateOdds();
        window.setHorses();
        timer.start();
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
            System.out.println(odds);

        }

    }


    public Horse getHorses(int num) {
        return horses[num];
    }

    public BufferedWriter getWriter() {
        return writer;
    }
    // AI
    public void setPlayerName(String name) {
        String line;
        String money;
        ArrayList<String> names = new ArrayList<>();

        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (line != null) {
            String playerName = line.substring(0,line.indexOf(','));
            names.add(line);
            if (name.toLowerCase().equals(playerName.toLowerCase())) {
                money = line.substring(line.indexOf(',') + 1);
                player = new Person(name, Integer.valueOf(money));
                names.remove(line);
                break;
            }
            else {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (names.isEmpty()) {
            return;
        }
        for (int i = 0; i < names.size(); i++) {
            try {
                writer.write(names.get(i));
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        player = new Person(name);
    }
    // AI
    public String findHorseName() {
        ArrayList<String> names = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Resources/CopiedNames.txt"))) {
            String line;
            line = reader.readLine();
            while (line  != null) {
                names.add(line);
                line = reader.readLine();
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
        if (checkWinner()) {
            timer.stop();
        }
        window.repaint();

    }

    public boolean checkWinner() {
        for (Horse horse : horses) {
            if (horse.getX() >= 630) {
                addWinningHorse(horse);
                return true;
            }
        }
        return false;
    }

    public void addWinningHorse(Horse horse) {
        ArrayList<String> names = new ArrayList<String>();
        int wins = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("horsestats.csv"))) {
            String line;
            line = reader.readLine();
            while (line  != null) {
                if (line.substring(0,line.indexOf(',')).equals(horse.getName())) {
                    wins = Integer.valueOf(line.substring(line.indexOf(',') + 1)) + 1;
                }
                else {
                    names.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter horseWriter;
        try {
             horseWriter = new BufferedWriter(new FileWriter("horsestats.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < names.size(); i++) {
            try {
                horseWriter.write(names.get(i));
                horseWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            horseWriter.write(horse.getName() + "," + wins);
            horseWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addWinner() {
        try {
            writer.write("\n" + player.getName() + "," + player.getBalance());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        runRace();





    }
    public static void main(String[] args) {
        Game game = new Game();


    }
}
