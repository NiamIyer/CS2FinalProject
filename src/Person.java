public class Person {
    private double balance;
    private String name;
    private double wager;
    public Person(String name)
    {
        this.balance = 100;
        this.name = name;
        wager = 0;
    }

    public double getWager()
    {
        return wager;
    }

    public void setWager(double wager)
    {
        this.wager = wager;
    }

    public Person(String name, double startMoney)
    {
        this.balance = startMoney;
        this.name = name;
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
