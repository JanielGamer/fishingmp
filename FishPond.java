package main;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FishPond {
    double population;
    double targetPopulation;
    double growthFactor;

    public FishPond(double startPopulation, double targetPopulation) {
        this(startPopulation,targetPopulation, 0.0005);
    }

    public FishPond(double startPopulation, double targetPopulation, double growthFactor){
        population = startPopulation;
        this.targetPopulation = targetPopulation;
        this.growthFactor = growthFactor;
    }

    public void fish(Map<Player, Double> percentages){
        double amounts = 0;

        for (Map.Entry<Player, Double> playerPercentage:percentages.entrySet()             ) {
            double amount = population * playerPercentage.getValue();
            playerPercentage.getKey().fish += amount;
            amounts += amount;
        }

        population -= amounts;
        repopulate(1);
        population = Math.round(population);
    }

    public void repopulate(double time){
        double rand = Math.random()*2;

        double newPopulation  = targetPopulation / (1 + Math.exp(-growthFactor*rand * targetPopulation * time) * (targetPopulation/population-1));
        population = newPopulation;
    }
    public double getPopulation() {
      return population;
    }
}