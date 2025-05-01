public class Person {
    private double balance;
    private String name;
    private Horse chosen;
    private double wager;
    public Person (String name) {
        this.balance = 100;
        this.name = name;
        chosen = null;
        wager = 0;
        System.out.println(balance);
    }

    public double getWager() {
        return wager;
    }

    public void setWager(double wager) {
        this.wager = wager;
    }

    public Horse getChosen() {
        return chosen;
    }

    public void setChosen(Horse chosen) {
        this.chosen = chosen;
    }

    public Person(String name, double startMoney)
    {
        this.balance = startMoney;
        this.name = name;
        System.out.println(balance);
    }

    public double getBalance()
    {
        return this.balance;
    }

    public void addMoney(double money)
    {
        // Adds money instead of setting it
        this.balance += money;
    }

    public String getName()
    {
        return this.name;
    }
}
