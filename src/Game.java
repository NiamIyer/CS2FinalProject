
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;


public class Game implements ActionListener, MouseListener {
    private Person player;
    private Scanner input;
    private double betAmount;
    private GameView window;
    private Horse[] horses;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String choice;
    private Timer timer;
    private ArrayList<String> horseWinnerNames;
    private ArrayList<Integer> horseWinnerCount;
    private ArrayList<String> playerWinnerNames;
    private ArrayList<Double> playerWinnerBalances;
    File stats;
    File horseStats;
    File names;
    File namesCopy;
    Horse winner;
    public Game() {
        horseWinnerNames = new ArrayList<String>();
        horseWinnerCount = new ArrayList<Integer>();
        playerWinnerNames = new ArrayList<String>();
        playerWinnerBalances = new ArrayList<Double>();
        winner = null;
        player = new Person("Niam");
        window = new GameView(this);
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
            horses[i] = new Horse(window, 0,(i * 110) + 25,findHorseName(),i);
        }
        window.setState(GameView.CHOOSE_HORSE);
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
        window.addMouseListener(this);
        findTopFiveHorses();
        findTopFivePlayers();
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

    public Timer getTimer() {
        return timer;
    }

    public Person getPlayer() {
        return player;
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
                player = new Person(name, Double.valueOf(money));
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
        System.out.println(player.getName());
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
                winner = horse;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                window.setState(GameView.WINNER);
                return true;
            }
        }
        return false;
    }

    public Horse getWinner() {
        return winner;
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

    public void findTopFiveHorses() {
        ArrayList<Integer> winCounts = new ArrayList<Integer>();
        ArrayList<String> names = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader("horsestats.csv"))) {
            String line;
            line = reader.readLine();
            while (line != null) {
                String name = line.substring(0,line.indexOf(','));
                String wins = line.substring(line.indexOf(',') + 1);
                names.add(name);
                winCounts.add(Integer.valueOf(wins));
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < winCounts.size() - 1; i++) {
            for (int j = 0; j < winCounts.size() - i - 1; j++) {
                if (winCounts.get(j) < winCounts.get(j + 1)) {
                    int tempWin = winCounts.get(j);
                    winCounts.set(j, winCounts.get(j + 1));
                    winCounts.set(j + 1, tempWin);
                    String tempName = names.get(j);
                    names.set(j, names.get(j + 1));
                    names.set(j + 1, tempName);
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            System.out.println(names.get(i) + ": " + winCounts.get(i));
        }
        horseWinnerNames = names;
        horseWinnerCount = winCounts;
    }

    public void findTopFivePlayers() {
        ArrayList<Double> balances = new ArrayList<Double>();
        ArrayList<String> names = new ArrayList<String>();
        try {
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                String name = line.substring(0,line.indexOf(','));
                String wins = line.substring(line.indexOf(',') + 1);
                names.add(name);
                balances.add(Double.valueOf(wins));
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (names.size() < 5) {
        }
        for (int i = 0; i < balances.size() - 1; i++) {
            for (int j = 0; j < balances.size() - i - 1; j++) {
                if (balances.get(j) < balances.get(j + 1)) {
                    double tempWin = balances.get(j);
                    balances.set(j, balances.get(j + 1));
                    balances.set(j + 1, tempWin);
                    String tempName = names.get(j);
                    names.set(j, names.get(j + 1));
                    names.set(j + 1, tempName);
                }
            }
        }
        for (int i = 0; i < names.size(); i++) {
            System.out.println(names.get(i) + " " + balances.get(i));
        }
        playerWinnerBalances = balances;
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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (window.getState() == GameView.CHOOSE_HORSE) {
            Rectangle horse1 = new Rectangle(0,0,272,188);
            Rectangle horse2 = new Rectangle(728,0,272,188);
            Rectangle horse3 = new Rectangle(0,313,272,188);
            Rectangle horse4 = new Rectangle(728,312,272,188);
            if(horse1.contains(e.getX(),e.getY())) {
                player.setChosen(horses[0]);
                window.setState(GameView.ENTER_NAME);
            }
            else if (horse2.contains(e.getX(),e.getY())) {
                player.setChosen(horses[1]);
                window.setState(GameView.ENTER_NAME);
            }
            else if (horse3.contains(e.getX(),e.getY())) {
                player.setChosen(horses[2]);
                window.setState(GameView.ENTER_NAME);
            }
            else if (horse4.contains(e.getX(),e.getY())) {
                player.setChosen(horses[3]);
                window.setState(GameView.ENTER_NAME);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    public static void main(String[] args) {
        Game game = new Game();

    }
}
