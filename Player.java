package main;
/**
 * Created by Joachim on 02.04.2017.
 */
public class Player {
    public Player(String name,double cash, double fish, int count) {
        this.name = name;
        this.cash = cash;
        this.fish = fish;
        this.count = count;
    }

    String name;
    double cash;
    double fish;
    String pwd;
    int count;
    boolean gameover = false;
    double percentage = 0;
    
    public double getCash() {
      return cash;
    }
}
