// Horse Racing by Niam
// Help from Google search AI, chatGPT (image creation) and cs50 style checker
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.*;
// Ability to write classes
public class Game implements ActionListener, MouseListener {
    private final int HORSE_COUNT = 4;
    private final int TRACK_LENGTH = 630;
    private Person player;
    private GameView window;
    // The horses that are racing
    private Horse[] horses;
    private Timer timer;
    // ArrayLists which contain info for the leaderboard
    private ArrayList<String> horseWinnerNames;
    private ArrayList<Integer> horseWinnerCount;
    private ArrayList<String> playerWinnerNames;
    private ArrayList<Double> playerWinnerBalances;
    // Chosen Horse;
    private Horse chosen;
    File stats;
    File horseStats;
    File names;
    // Copy of the names file which gets updated to ensure that no horse has the same name
    File namesCopy;
    Horse winner;

    public Game()
    {
        chosen = null;
        horseWinnerNames = new ArrayList<String>();
        horseWinnerCount = new ArrayList<Integer>();
        playerWinnerNames = new ArrayList<String>();
        playerWinnerBalances = new ArrayList<Double>();
        winner = null;
        player = null;
        window = new GameView(this);
        window.setState(GameView.START);
        stats = new File("stats.csv");
        horseStats = new File("horseStats.csv");
        names = new File("Resources/names.txt");
        namesCopy = new File("Resources/CopiedNames.txt");
        timer = new Timer(100, this);
        // Copies the Names file into the CopiedNames file
        try
        {
            Files.copy(names.toPath(), namesCopy.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        this.horses = new Horse[HORSE_COUNT];
        for (int i = 0; i < HORSE_COUNT; i++)
        {
            horses[i] = new Horse(window, 0, (i * 110) + 25, findHorseName(), i);
        }
        // Calculates the odds of each horse winning based off their rates
        calculateOdds();
        window.setHorses();
        window.addMouseListener(this);
        findTopFivePlayers();
        findTopFiveHorses();
    }

    public void calculateOdds()
    {
        double sum = 0;
        // For each loops
        for (Horse horse : horses)
        {
            sum += horse.getRate();
        }
        for (int i = 0; i < HORSE_COUNT; i++)
        {
            double odds = horses[i].getRate() / sum;
            horses[i].setOdds(odds);
            if (odds >= 0.5)
            {
                horses[i].setBettingOdds(-(int) (100 * odds / (1 - odds)));
            }
            else
            {
                horses[i].setBettingOdds((int) (100 * (1 - odds) / odds));
            }
        }
    }

    public Timer getTimer()
    {
        return timer;
    }

    public Person getPlayer()
    {
        return player;
    }

    public Horse getHorses(int num)
    {
        return horses[num];
    }

    // AI Assisted
    public void setPlayerName(String name)
    {
        // ArrayList usage
        ArrayList<String> otherPlayers = new ArrayList<>();
        player = null;
        // Creates a buffered reader which loops through the file and finds if the player already has played
        // If not, then it creates a new player with new data
        try {
            BufferedReader reader = new BufferedReader(new FileReader("stats.csv"));
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.isEmpty() || line.indexOf(",") == -1) {
                    continue;
                }
                // Gets the name and balance of the player
                String playerName = line.substring(0,line.indexOf(","));
                if (playerName.toLowerCase().equals((name.toLowerCase())))
                {
                    String money = line.substring(line.indexOf(",") + 1);
                    player = new Person(playerName, Double.parseDouble(money));
                }
                else
                {
                    // Keep other players
                    otherPlayers.add(line);
                }
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // Write back all other players
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("stats.csv")))
        {
            for (String playerData : otherPlayers)
            {
                writer.write(playerData);
                writer.newLine();
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // Creates new player
        if (player == null)
        {
            player = new Person(name);
        }
    }
    // AI assisted
    public String findHorseName()
    {
        // Finds a random name from the list and removes it from the list, then returns the name
        ArrayList<String> names = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Resources/CopiedNames.txt"));
            String line;
            line = reader.readLine();
            while (line != null)
            {
                names.add(line);
                line = reader.readLine();
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        int index = (int) (Math.random() * names.size());
        String name = names.get(index);
        names.remove(index);
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("Resources/CopiedNames.txt")))
        {
            for (String word : names)
            {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return name;
    }

    public void runRace()
    {
        // Changes the horse's rates and updates the positions
        for (int i = 0; i < HORSE_COUNT; i++)
        {
            horses[i].setX(horses[i].getX() + horses[i].getRate());
            horses[i].changeRate();
        }
        if (checkWinner())
        {
            timer.stop();
        }
        window.repaint();
    }

    public boolean checkWinner()
    {
        // Checks if each horse has won
        for (Horse horse : horses)
        {
            if (horse.getX() >= TRACK_LENGTH)
            {
                // Adds winning horse to csv file
                addWinningHorse(horse);
                winner = horse;
                // If the player won
                if (chosen.equals(winner))
                {
                    // Sets money based off of odds and bet
                    if (chosen.getBettingOdds().charAt(0) == '+')
                    {
                        player.addMoney(player.getWager() *
                                (Integer.parseInt(chosen.getBettingOdds().substring(1))) /
                                100.0);
                    }
                    else
                    {
                        player.addMoney(
                                player.getWager() *
                                        (100.0 / Integer.parseInt(chosen.getBettingOdds().substring(1))));
                    }
                }
                else
                {
                    // Removes money from player
                    player.addMoney(-1 * player.getWager());
                }
                // Updates player balance on csv file
                addWinner();
                window.setState(GameView.WINNER);
                // Sets a small delay
                Timer delayTimer = new Timer(1500, new ActionListener() {
                    public void actionPerformed(ActionEvent evt)
                    {
                        ((Timer) evt.getSource()).stop();
                        // Changes the screen to the leaderboard
                        window.setState(GameView.HIGH_SCORE);
                        window.repaint();
                    }
                });
                // Makes sure that afterward the timer doesn't repeat
                delayTimer.setRepeats(false);
                delayTimer.start();
                return true;
            }
        }
        return false;
    }

    public Horse getWinner()
    {
        return winner;
    }

    public void addWinningHorse(Horse horse)
    {
        // Updates the horse's win count on the csv file
        ArrayList<String> names = new ArrayList<String>();
        int wins = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("horsestats.csv")))
        {
            String line;
            line = reader.readLine();
            while (line != null)
            {
                if (line.substring(0, line.indexOf(',')).equals(horse.getName()))
                {
                    wins = Integer.valueOf(line.substring(line.indexOf(',') + 1)) + 1;
                }
                else
                {
                    names.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        BufferedWriter horseWriter;
        try
        {
            horseWriter = new BufferedWriter(new FileWriter("horsestats.csv"));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < names.size(); i++)
        {
            try
            {
                horseWriter.write(names.get(i));
                horseWriter.newLine();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        try
        {
            horseWriter.write(horse.getName() + "," + wins);
            horseWriter.flush();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void findTopFiveHorses()
    {
        // Finds the top five horses with the most wins
        ArrayList<Integer> winCounts = new ArrayList<Integer>();
        ArrayList<String> names = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader("horsestats.csv")))
        {
            String line;
            reader.readLine();
            line = reader.readLine();
            while (line != null)
            {
                String name = line.substring(0, line.indexOf(','));
                String wins = line.substring(line.indexOf(',') + 1);
                names.add(name);
                winCounts.add(Integer.valueOf(wins));
                line = reader.readLine();
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        // Uses bubble sort to sort through the horses in order to find the top five
        for (int i = 0; i < winCounts.size() - 1; i++)
        {
            for (int j = 0; j < winCounts.size() - i - 1; j++)
            {
                if (winCounts.get(j) < winCounts.get(j + 1))
                {
                    int tempWin = winCounts.get(j);
                    winCounts.set(j, winCounts.get(j + 1));
                    winCounts.set(j + 1, tempWin);
                    String tempName = names.get(j);
                    names.set(j, names.get(j + 1));
                    names.set(j + 1, tempName);
                }
            }
        }

        horseWinnerNames = names;
        horseWinnerCount = winCounts;
    }

    public ArrayList<String> getHorseWinnerNames()
    {
        return horseWinnerNames;
    }

    public ArrayList<Integer> getHorseWinnerCount()
    {
        return horseWinnerCount;
    }

    public ArrayList<String> getPlayerWinnerNames()
    {
        return playerWinnerNames;
    }

    public ArrayList<Double> getPlayerWinnerBalances()
    {
        return playerWinnerBalances;
    }

    public void findTopFivePlayers()
    {
        // Finds the top five players with the most money
        ArrayList<Double> balances = new ArrayList<Double>();
        ArrayList<String> names = new ArrayList<String>();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("stats.csv"));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null)
            {
                String name = line.substring(0, line.indexOf(','));
                String balanceStr = line.substring(line.indexOf(',') + 1);

                try
                {
                    double balance = Double.parseDouble(balanceStr);
                    names.add(name);
                    balances.add(balance);
                } catch (NumberFormatException e)
                {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        // Uses bubble sort for the players based off balance
        for (int i = 0; i < balances.size() - 1; i++)
        {
            for (int j = 0; j < balances.size() - i - 1; j++)
            {
                if (balances.get(j) < balances.get(j + 1))
                {
                    double tempWin = balances.get(j);
                    balances.set(j, balances.get(j + 1));
                    balances.set(j + 1, tempWin);
                    String tempName = names.get(j);
                    names.set(j, names.get(j + 1));
                    names.set(j + 1, tempName);
                }
            }
        }

        playerWinnerBalances = balances;
        playerWinnerNames = names;
    }

    public void addWinner()
    {
        // Adds the player to the csv file with new balance
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("stats.csv", true));
            writer.write(player.getName() + "," + player.getBalance());
            writer.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override public void actionPerformed(ActionEvent e)
    {
        // Every time the timer ticks, repeats runRace method
        runRace();
    }

    @Override public void mouseClicked(MouseEvent e) {}

    @Override public void mousePressed(MouseEvent e)
    {
        // If the screen is the choose horse screen
        if (window.getState() == GameView.CHOOSE_HORSE)
        {
            // Creates rectangles with different coordinates
            Rectangle horse1 = new Rectangle(0, 0, 272, 188);
            Rectangle horse2 = new Rectangle(728, 0, 272, 188);
            Rectangle horse3 = new Rectangle(0, 313, 272, 188);
            Rectangle horse4 = new Rectangle(728, 312, 272, 188);
            // If any of the horses contain the click, set the chosen horse
            if (horse1.contains(e.getX(), e.getY()))
            {
                chosen = horses[0];
                window.setState(GameView.ENTER_NAME);
            }
            else if (horse2.contains(e.getX(), e.getY()))
            {
                chosen = horses[1];
                window.setState(GameView.ENTER_NAME);
            }
            else if (horse3.contains(e.getX(), e.getY()))
            {
                chosen = horses[2];
                window.setState(GameView.ENTER_NAME);
            }
            else if (horse4.contains(e.getX(), e.getY()))
            {
                chosen = horses[3];
                window.setState(GameView.ENTER_NAME);
            }
        }
        // If the screen is the start screen
        if (window.getState() == GameView.START)
        {
            // Two rectangles to act as buttons
            Rectangle play = new Rectangle(120, 250, 324, 174);
            Rectangle highScore = new Rectangle(700, 350, 324, 174);
            // Sets the state based off of what is clicked
            if (play.contains(e.getX(), e.getY()))
            {
                window.setState(GameView.CHOOSE_HORSE);
            }
            else if (highScore.contains(e.getX(), e.getY()))
            {
                window.setState(GameView.HIGH_SCORE);
            }
        }
    }

    @Override public void mouseReleased(MouseEvent e) {}

    @Override public void mouseEntered(MouseEvent e) {}

    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args)
    {
        Game game = new Game();
    }
}
