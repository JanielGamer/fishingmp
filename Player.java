package main;
/**
 * Created by Joachim on 02.04.2017.
 */
public class Player {
    public Player(String name,double cash, double fish) {
        this.name = name;
        this.cash = cash;
        this.fish = fish;
    }

    String name;
    double cash;
    double fish;
    String pwd;
    
    public double getCash() {
      return cash;
    }
}
