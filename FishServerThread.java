package main;//-

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;


public class FishServerThread extends Thread {
    private final FishServer server;
    private final Player player;
    static Socket socket;

    static int playerCount = 0;

    public FishServerThread(Socket accept, FishServer server, Player player) {
        super("FishServerThread");
        this.socket = accept;
        this.server = server;
        this.player = player;
        server.playerCount++;
        FishServerThread.playerCount++;
    }
    public double getMaxFishPercentage() {
    	return 50/playerCount;
    }
    static void countdownFishing(int a){
    	try {
    		PrintWriter out = new PrintWriter(socket.getOutputStream());
    		if (a==0){
    			out.write("Jetzt wird gefischt!\n");
    		}
    		else { 
    			out.write("Es wird in " + a + " Sekunden gefischt!\n");
    		}
    		out.flush();
    		
		} catch (IOException e) {
			e.printStackTrace();
		}     	
    }

    @SuppressWarnings("deprecation")
	@Override
    public void run() {
        try {
        	PrintWriter out = new PrintWriter(socket.getOutputStream()); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	
        	player.name = in.readLine();
        	this.setName(player.name);
        	System.out.println("[SERVER/THREAD/PLAYERS]: " + this.getName() + " has joined the game!");
        	
            String inputLine ="";
            while ((inputLine=in.readLine())!= null) {
            	
                PossibleActions action;

                // BUY 100 -> [BUY, 100]
                String[] arguments = inputLine.split(" ");

                try {
                    action = PossibleActions.valueOf(arguments[0]);
                } catch (IllegalArgumentException e) {
                    out.println("Didn't understand: " + arguments[0]);
                    out.flush();
                    continue;
                }

                double amount, costs, gains;

                switch(action){
                    case BUY:
                        amount = Double.parseDouble(arguments[1]);
                        costs = server.market1.buy(player, amount);
                        out.write(Double.toString(costs)+" bought!\n");
                        out.flush();
                        break;
                    case SELL:
                    	if(arguments.length==2){
                    	
                        amount = Double.parseDouble(arguments[1]);
                        gains = server.market1.sell(player, amount);
                        out.write(Double.toString(gains)+" selled!\n");
                        out.flush();
                    	} else{
                    		out.println("Wrong syntax SELL [AMOUNT]");
                            out.flush();
                    	}
                        break;
                    case FISH:
                    	if (arguments.length==2){
                    	
                        amount = Double.parseDouble(arguments[1]);
                        
                        if (amount > getMaxFishPercentage() || amount < 0){
                        	out.write("You are not allowed to fish more than " + getMaxFishPercentage() +" % or less then 0%!\n");
                        
                		}else{
                			out.write("Your percentace has been accepted!\n");
                			server.addFishing(player, (amount/100));
                        }
                        out.flush();
                    	} else{
                    		out.println("Wrong syntax FISH [AMOUNT]");
                            out.flush();
                    	}
                        break;
                    case STATS:
                    	out.write(player.name +": \n\n");
                    	out.flush();
                    	double price = FishServer.market1.getPrice()*100;
                    	price = Math.round(price);
                    	price /=100;
                    	out.write("Price: " + price + "\t" + "Population: " + FishServer.pond.getPopulation() + "\n" + "Money: " + player.getCash() + "\t" + "Fish: "+ player.fish + "\tPlayer: " + playerCount + "\tMax. %: " + getMaxFishPercentage() + "\n");
                    	out.flush();
                    	break;
                    case leave:
                    	playerCount--;
                    	server.playerCount--;
                    	System.out.println("[SERVER/THREAD/PLAYERS]: " + player.name + " has left the game!");
                    	interrupt();
                    default:
                        out.write("Action not yet implemented");
                }

                
            }
        } catch (SocketException e) {
        	FishServer.playerCount--;
        	playerCount--;
        	interrupt();
        	System.out.println("[SERVER/THREAD/PLAYERS]: " + player.name + " disconnected!");
           
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
    }
}
