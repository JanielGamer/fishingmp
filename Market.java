package main;


public class Market {
    double requestRate;
    double basePrice;

    double changePlayerInfluence = 1;
    double changeRandomInfluence = 1;

    public Market(double basePrice, double requestRate) {
        this.requestRate = requestRate;
        this.basePrice = basePrice;
    }

    double getPrice() {
        return basePrice * requestRate;
    }

    double buy(Player player, double amount) {
        double calculatedPrice = amount * getPrice();
        if (player.cash < calculatedPrice) {
            return 0;
        }

        requestRate += changePlayerInfluence * Math.sqrt(amount) / 100;

        calculatedPrice = Math.round(calculatedPrice*100)/100;
        player.cash -= calculatedPrice;
        player.fish += amount;
        return calculatedPrice;
    }

    double sell(Player player, double amount) {
        double calculatedGain = amount * getPrice();
        if (player.fish < amount) {
            return 0;
        }

        requestRate -= changePlayerInfluence * Math.sqrt(amount) / 100;
        
        calculatedGain = Math.round(calculatedGain*100)/100;
        player.cash += calculatedGain;
        player.fish -= amount;
        return calculatedGain;
    }

    void adjustMarket() {
        if (Math.random() < 0.5) {
            requestRate *= (Math.random() * 0.5 + 0.5) * changeRandomInfluence;
        } else {
            requestRate *= (Math.random() + 1) * changeRandomInfluence;
        }
    }
}