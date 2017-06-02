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
		return 50 / playerCount;
	}

	public void testAccount() {

	}

	public void saveAccount() {

	}

	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			player.name = in.readLine();
			this.setName(player.name);
			player.pwd = in.readLine();
			String text = "";

			System.out.println("[SERVER/THREAD/PLAYERS]: " + this.getName() + " has joined the game!");
			FishServer.playerSaveDates.put(this.player, false);
			FishServer.actionPerformed.put(player, false);
			FishServer.gui.addList(player.name);

			String inputLine = "";
			while ((inputLine = in.readLine()) != null) {

				PossibleActions action;

				// BUY 100 -> [BUY, 100]
				String[] arguments = inputLine.split(" ");

				try {
					action = PossibleActions.valueOf(arguments[0]);
				} catch (IllegalArgumentException e) {
					out.println("DIALOG~Didn't understand: " + arguments[0]);
					out.flush();
					continue;
				}

				double amount, costs, gains;

				if (arguments.length == 1 || arguments[0].equals("SAY") || proveString(arguments[1])) {
					switch (action) {
					case BUY:
						if (arguments.length == 2 && !FishServer.actionPerformed.get(player)) {
							amount = Double.parseDouble(arguments[1]);
							costs = server.market1.buy(player, amount);
							if (costs>0) {
								FishServer.actionPerformed.put(player, true);
								out.write("DIALOG~You bought " + amount + " fish for " + Double.toString(costs) + " $!\n");
							} else {
								out.write("DIALOG~You don't have enought money to buy fish!\n");
							}
						} else if (FishServer.actionPerformed.get(player)) {
							out.write("DIALOG~You already performed one action!\n");
						} else {
							out.write("DIALOG~Wrong syntax BUY [AMOUNT]\n");
						}
						out.flush();
						break;
					case SELL:
						if (arguments.length == 2 && !FishServer.actionPerformed.get(player)) {
							amount = Double.parseDouble(arguments[1]);
							gains = server.market1.sell(player, amount);
							if (gains>0) {
								FishServer.actionPerformed.put(player, true);
								out.write("DIALOG~You selled " + amount + " fish for " + Double.toString(gains) + " $!\n");
							} else {
								out.write("DIALOG~You don't have enought fish to sell!\n");
							}
						} else if (FishServer.actionPerformed.get(player)) {
							out.write("DIALOG~You already performed one action!\n");
						} else {
							out.println("DIALOG~Wrong syntax SELL [AMOUNT]\n");
						}
						out.flush();
						break;
					case FISH:
						if (arguments.length == 2) {

							amount = Double.parseDouble(arguments[1]);

							if (amount > getMaxFishPercentage() || amount < 0) {
								out.write("DIALOG~You are not allowed to fish more than " + getMaxFishPercentage()
										+ " % or less then 0%!\n");

							} else {
								out.write("DIALOG~Your percentace has been accepted!\n");
								server.addFishing(player, (amount / 100));
							}
						} else {
							out.println("DIALOG~Wrong syntax FISH [AMOUNT]\n");
						}
						out.flush();
						break;
					case STATS:
						out.write("NAME~" + player.name + ": \n\n");
						out.flush();
						double price = FishServer.market1.getPrice() * 100;
						price = Math.round(price);
						price /= 100;
						out.write("STATS~Price: " + price + "\t" + "Population: " + FishServer.pond.getPopulation() + "\t" + "Money: " + player.getCash() + "\t" + "Fish: " + player.fish + "\tPlayer: "
								+ playerCount + "\tMax. %: " + getMaxFishPercentage() + "\n");
						out.flush();
						break;
					case SAY:
						if (arguments.length > 1) {
							for (int i = 1; i < arguments.length; i++)
								text += arguments[i] + " ";
							FishServer.sayAll(text, this.player.name);
							text = "";
						} else {
							out.write("DIALOG~Wrong syntax SAY [TEXT]\n");
						}
						break;
					case PAUSE:
						FishServer.pause(player.name);
						break;
					case UNPAUSE:
						FishServer.playerContinue.put(player, true);
						break;
					case leave:
						playerCount--;
						server.playerCount--;
						FishServer.gui.removeList(player.name);
						System.out.println("[SERVER/THREAD/PLAYERS]: " + player.name + " has left the game!");
						out.write("stop\n");
						out.flush();
						interrupt();
					default:
						out.write("Action not yet implemented");
					}
				} else {
					out.write("DIALOG~Invaild parameter!\n");
					out.flush();
				}

			}
		} catch (SocketException e) {
			FishServer.playerCount--;
			playerCount--;
			FishServer.gui.removeList(player.name);
			System.out.println("[SERVER/THREAD/PLAYERS]: " + player.name + " disconnected!");
			interrupt();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean proveString(String a) {
		boolean ziffer;
		int b = 0;
		try {
			b = Integer.parseInt(a);
			ziffer = true;
		} catch (NumberFormatException e) {

			ziffer = false;
		}
		return ziffer;
	}
}