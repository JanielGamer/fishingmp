package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class FishServer {
	static int port = 63564;
	static boolean listening = true;

	static boolean question = false;
	static Market market1;
	static Market market2;
	static FishPond pond;
	static List<FishServerThread> playerThread = new ArrayList();
	static PrintWriter[] writer = new PrintWriter[100];
	static Player[] players = new Player[100];
	static Countdown c1;
	static boolean serverContinue = false;
	static boolean wait = true;
	static ServerFrame gui;
	static int count = 0;
	static double endCash = 10000;
	static Player winner;
	static int miningLevel = 0;
	static int pondSize = 1000;
	static double startMoney;
	static int gameover = 0;

	static ConcurrentMap<Player, Double> fishingRates = new ConcurrentHashMap<>();
	static ConcurrentMap<Player, Boolean> playerSaveDates = new ConcurrentHashMap<>();
	static ConcurrentMap<Player, Boolean> playerContinue = new ConcurrentHashMap<>();
	static ConcurrentMap<Player, Boolean> actionPerformed = new ConcurrentHashMap<>();
	
	static int playerCount = 0;

	public FishServer(Market market, FishPond pond, Market market2) {
		FishServer.market1 = market;
		FishServer.pond = pond;
		FishServer.market2 = market2;
	}

	void addFishing(Player player, double amount) {
		fishingRates.put(player, amount);
	}

	static void fish() {
		pond.fish(fishingRates);
		fishingRates.clear();
	}

	public static void main(String[] args) {

		System.out.println("[SERVER]: Server has been started!");
		
		c1 = new Countdown(60);
		c1.start();
		c1.setPause(true);

		FishPond pond = new FishPond(100, 2000);
		Market market1 = new Market(10, 1);
		Market market2 = new Market(6, 1);

		FishServer server = new FishServer(market1, pond, market2);

		gui = new ServerFrame();
		
		while (wait) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		
		startMoney = market1.basePrice;
		market2.basePrice = market1.basePrice*0.8;
		
		System.out.println("[SERVER]: Port accepted!");
		
		if (args.length > 1) {
			System.err.println("Usage: java FishServer [<port number>]");
			System.exit(1);
		}
		if (args.length == 1) {
			FishServer.port = Integer.parseInt(args[0]);
		}

		new Thread(new ConsoleController()).start();
		new Thread(new fishFlusher()).start();

		try (ServerSocket serverSocket = new ServerSocket(FishServer.port)) {
			int threadCount = 0;
			c1.setPause(true);
			while (FishServer.listening) {
				Socket socket = serverSocket.accept();
				players[count] = new Player("Player " + threadCount, (startMoney*10), 0, count);
				playerThread.add(new FishServerThread(socket, server, players[count]));
				playerThread.get(threadCount).start();
				writer[threadCount] = new PrintWriter(socket.getOutputStream());
				threadCount++;
				count++;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save(String name) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		File dir = new File(name);
		if (dir.mkdir()) {
			System.out.println("[SERVER/THREAD]: Save could be created!");
			saveDats(name);
		} else {
			System.out.println("[SERVER/THREAD]: Save already exists");
			System.out.println("[SERVER/THREAD]: Do you want to overide it? (y/n): ");
			question = true;
		}
	}

	public static void yes(String name) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		File dir = new File(name);
		delete(dir);
		dir.mkdir();
		saveDats(name);
	}

	public static void saveDats(String name) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		File writer[] = new File[100];
		BufferedWriter[] saver = new BufferedWriter[100];
		String files;
		int count = 0;
		File dir = new File(name + "/players");
		dir.mkdir();
		for (int a = 0; a < playerCount; a++) {
			files = (name + "/" + "players/Player" + count + ".txt");
			writer[count] = new File(files);
			writer[count].createNewFile();
			saver[count] = new BufferedWriter(new FileWriter(files));
			saver[count].write(players[a].name + "\r\n");
			saver[count].write(crypting(players[a].pwd) + "\r\n");
			saver[count].write(Double.toString(players[a].cash) + "\r\n");
			saver[count].write(Double.toString(players[a].fish) + "\r\n");
			saver[count].write(players[a].gameover + "\r\n");
			saver[count].flush();
			saver[count].close();
			count++;
		}
		File marketS = new File(name + "/Market.txt");
		File marketgS = new File(name + "/MarketG.txt");
		File pondS = new File(name + "/Pond.txt");
		File save = new File(name + "/Main.txt");

		marketS.createNewFile();
		marketgS.createNewFile();
		pondS.createNewFile();
		save.createNewFile();
		BufferedWriter bwM = new BufferedWriter(new FileWriter(name + "/Market.txt"));
		BufferedWriter bwMg = new BufferedWriter(new FileWriter(name + "/MarketG.txt"));
		BufferedWriter bwP = new BufferedWriter(new FileWriter(name + "/Pond.txt"));
		BufferedWriter bwS = new BufferedWriter(new FileWriter(name + "/Main.txt"));

		bwM.write(market1.requestRate + "\r\n");
		bwM.write(market1.basePrice + "\r\n");
		bwMg.write(market2.requestRate + "\r\n");
		bwMg.write(market2.basePrice + "\r\n");
		bwP.write(pond.population + "\r\n");
		bwP.write((pond.growthFactor * 10000) + "\r\n");
		bwP.write(pond.targetPopulation + "\r\n");
		bwS.write(playerCount + "\r\n");
		// bwS.write("\r\n");
		bwM.flush();
		bwP.flush();
		bwP.flush();

		bwM.close();
		bwMg.close();
		bwP.close();
		bwS.close();
	}

	public static void load(String name) throws IOException, InvalidKeyException, NumberFormatException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		BufferedReader brS = new BufferedReader(new FileReader(name + "/Main.txt"));
		String input;
		int playerSaved = Integer.parseInt(brS.readLine());
		// brS.readLine();
		BufferedReader[] reader = new BufferedReader[playerSaved];
		int i = 0;
		File f = new File(name + "/players");
		for (File c : f.listFiles()) {
			reader[i] = new BufferedReader(new FileReader(c));
			input = reader[i].readLine();
			for (int j = 0; j < playerCount; j++) {
				if (players[j].name.equals(input) && players[j].pwd.equals(encrypting(reader[i].readLine()))) {
					playerSaveDates.put(players[j], true);
					input = reader[i].readLine();
					players[j].cash = Double.parseDouble(input);
					input = reader[i].readLine();
					players[j].fish = Double.parseDouble(input);
					input = reader[i].readLine();
					players[j].gameover = Boolean.parseBoolean(input);
				} /*
					 * else{ System.out.println("Player "+players[j].
					 * name+" not in savegame!");
					 * writer[j].println("You are not in the savegame!");
					 * writer[j].flush(); }
					 */
			} // end of for
			BufferedReader brP = new BufferedReader(new FileReader(name + "/Pond.txt"));
			BufferedReader brM = new BufferedReader(new FileReader(name + "/Market.txt"));
			BufferedReader brMg = new BufferedReader(new FileReader(name + "/MarketG.txt"));
			market1.requestRate = Double.parseDouble(brM.readLine());
			market1.basePrice = Double.parseDouble(brM.readLine());
			market2.requestRate = Double.parseDouble(brMg.readLine());
			market2.basePrice = Double.parseDouble(brMg.readLine());
			pond.population = Double.parseDouble(brP.readLine());
			pond.growthFactor = Double.parseDouble(brP.readLine()) / 10000;
			pond.targetPopulation = Double.parseDouble(brP.readLine());

			brP.close();
			brM.close();
			brMg.close();
			brS.close();

			for (int j = 0; j < playerCount; j++) {
				for (Map.Entry<Player, Boolean> playersM : playerSaveDates.entrySet()) {
					if (!playersM.getValue()) {
						if (playersM.getKey().name.toString().equals(players[j].name.toString())) {
							writer[j].write("DIALOG~User doesn't exists in this savegame!\n");
							writer[j].flush();
						}
					}
				}
			}
			i++;
		}
		System.out.println("[SERVER/THREAD]: Loading Successfull");
	}

	public static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}

	}

	public static void sayAll(String text, String name) {
		for (int b = 0; b < playerThread.size(); b++) {
			if (name.equals("d1al0g~@1")) {
				writer[b].write("SAY~-- " + text + " --\n");
			} else if (name.equals("d1al0g~@2")) {
				writer[b].write("DIALOG~" + text + "\n");
			} else if (name.equals("d1al0g~@3")) {
				writer[b].write("DIALOG1~" + text + "\n");
			} else if (name.equals("d1al0g~@4")) {
				writer[b].write("DIALOG2~" + text + "\n");
			} else {
				writer[b].write("SAY~<" + name + "> " + text + "\n");
			}
			writer[b].flush();
		}
		if (name.equals("d1al0g~@1")) {
			System.out.println("[SERVER/THREAD]: " + text);
		} else if (name.equals("d1al0g~@2")) {
			System.out.println("[SERVER/THREAD]: " + text);
		} else if (name.equals("d1al0g~@3")) {
			System.out.println("[SERVER/THREAD]: " + text);
		} else if (name.equals("d1al0g~@4")) {
			System.out.println("[SERVER/THREAD]: " + text);
		} else {
			System.out.println("<" + name + "> " + text);
		}
	}
	
	public static void pause(String player) {
		c1.setPause(true);
		sayAll(player + " has paused the game", "d1al0g~@1");
	}
	
	public static void stopServer() throws InterruptedException {
		FishServer.listening = false;
		System.out.println("[SERVER/THREAD]: Server stopped!");
		c1.interrupt();
		for (int b = 0; b < playerThread.size(); b++) {
			writer[b].write("stop\n");
			writer[b].flush();
		}
		Thread.sleep(5000);
		System.out.println("[SERVER/THREAD]: Server closed!");
		System.exit(0);
	}
	
	public static void kick(String s) {
		for (int i=0;i<playerCount;i++) {
			if (players[i].name.equals(s)) {
				writer[i].write("KICK~\n");
				writer[i].flush();
				playerThread.get(i).interrupt();
				break;
			}
		}
		gui.removeList(s);
	}
	
	public static void end() {
		gameover = 0;
		for (int x=0;x<count;x++) {
			players[x].cash -= (startMoney + (Math.round(((players[x].percentage/100)*pond.getPopulation())/4)));
			if (players[x].cash>=endCash) {
				winner = players[x];
				sayAll(winner.name + " has won the game!", "d1al0g~@1");
				winner.gameover = true;
			}
			
			if (players[x].cash<0) {
				sayAll(players[x].name + " is game over!", "d1al0g~@1");
				players[x].gameover = true;
				c1.setPause(true);
			}
			
			if (players[x].gameover) {
				gameover++;
			} else {
				winner = players[x];
			}
		}
		
		if (gameover==(playerCount-1)) {
			sayAll(winner.name + " has won the game!", "d1al0g~@1");
			winner.gameover = true;
			c1.setPause(true);
		}
	}
	
	public static void setCash(String name, int amount) {
		for (int x=0;x<count;x++) {
			if (players[x].name.equals(name)) {
				players[x].cash = amount;
			}
		}
	}

	static class ConsoleController implements Runnable {
		@Override
		public void run() {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
				String input;
				String text = "";
				String name = "";
				String [] ins;
				
				while (true) {
					input = bufferedReader.readLine();
					ins = input.split(" ");
					if (Objects.equals(ins[0], "stop")) {
						FishServer.listening = false;
						System.out.println("[SERVER/THREAD]: Server stopped!");
						c1.interrupt();
						for (int b = 0; b < playerThread.size(); b++) {
							writer[b].write("stop\n");
							writer[b].flush();
						}
						Thread.sleep(5000);
						System.out.println("[SERVER/THREAD]: Server closed!");
						break;
					} else if (Objects.equals(ins[0], "fish")) {
						fish();
					} else if (Objects.equals(ins[0], "save")) {
						if (ins.length > 1) {
							name = ins[1];
							save(name);
						} else {
							System.out.println("[SERVER/THREAD]: Wrong syntax. Usage: save [SAVENAME]");
						}
					} else if (Objects.equals(ins[0], "load")) {
						if (ins.length > 1) {
							load(ins[1]);
						} else {
							System.out.println("[SERVER/THREAD]: Wrong syntax. Usage: load [LOADNAME]");
						}
					} else if (Objects.equals(ins[0], "y")) {
						if (question) {
							yes(name);
							question = false;
							System.out.println("[SERVER/THREAD]: Save has been overwritten!");
						} 
					} else if (Objects.equals(ins[0], "say")) {
						if (ins.length > 1) {
							for (int i=1;i<ins.length;i++) text += ins[i] + " ";
							FishServer.sayAll(text, "Server");
							text = "";
						} else {
							System.out.println("[SERVER/THREAD]: Wrong syntax. Usage: say [TEXT]");
						}
					} else if (Objects.equals(ins[0], "unpause")) {
						serverContinue = true;
					} else if (Objects.equals(ins[0], "pause")) {
						pause("Server");
					} else if (Objects.equals(ins[0], "setcash")) {
						if (ins.length>2) {
							setCash(ins[1], Integer.parseInt(ins[2]));
						}
					}

				}
				System.exit(0);
			} catch (IOException e) {
				listening = false;
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static class fishFlusher implements Runnable {
		public void run() {
			while (true) {
				if ((playerContinue.size()==playerCount && playerCount>0) || serverContinue) {
					c1.setPause(false);
					playerContinue.clear();
					sayAll("The game has been continued by all players or the server!", "d1al0g~@1");
					System.out.println("[SERVER/COUNTDOWN]: The game has benn continued by all players or the server!");
					serverContinue = false;
				}
				
				if ((!c1.isRunning() || fishingRates.size() == playerCount) && playerCount > 0) {
					
					c1.setPause(true);

					for (int a = 5; a >= 0; a--) {

						

							if (a == 0) {
								sayAll("Fishing starts now!", "d1al0g~@4");
								c1.restart();
								if (FishServer.miningLevel==2) {
									for (int x=0;x<FishServer.count;x++) {
										if (Math.random()<0.5 && players[x].percentage!=0) {
											sayAll(players[x].name + " has benn caught when he has fished illegal!", "d1al0g~@1");
											players[x].cash -= startMoney*10;
										}
									}
									FishServer.pond.targetPopulation += (pondSize*0.1);
									sayAll("The new max. population is " + FishServer.pond.targetPopulation, "d1al0g~@1");
									FishServer.miningLevel = 0;
								} else if (FishServer.miningLevel==1) {
									FishServer.miningLevel++;
									sayAll("In this round the fishing is forbidden!", "d1al0g~@2");
								}
							} else {
								sayAll("Fishing starts in " + a + " seconds!", "d1al0g~@3");
							}

							
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
					for (int x=0;x<count;x++) {
						actionPerformed.put(players[x], false);
					}
					end();
					fish();
					for (int x=0;x<FishServer.count;x++) {
						players[x].percentage = 0;
					}
					for (int b = 0; b < playerThread.size(); b++){
						writer[b].write("POP~"+pond.population+"°"+market1.getPrice()+"°"+market2.getPrice()+"\n");
						writer[b].flush();
					}
				}
			}
		}
	}

	static String crypting(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, IOException {

		String keyStr = "lq24rfwhfesapoa24wkfgegfcva8352hrphf101234hfwf0ß1ßufwwegfh24e3f2jnwfsvwqd";
		byte[] key = (keyStr).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] encrypted = cipher.doFinal(text.getBytes());

		BASE64Encoder myEncoder = new BASE64Encoder();
		String geheim = myEncoder.encode(encrypted);

		return geheim;
	}

	static String encrypting(String geheim) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {

		String keyStr = "lq24rfwhfesapoa24wkfgegfcva8352hrphf101234hfwf0ß1ßufwwegfh24e3f2jnwfsvwqd";
		byte[] key = (keyStr).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

		BASE64Decoder myDecoder2 = new BASE64Decoder();
		byte[] crypted2 = myDecoder2.decodeBuffer(geheim);

		Cipher cipher2 = Cipher.getInstance("AES");
		cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec);
		byte[] cipherData2 = cipher2.doFinal(crypted2);
		String erg = new String(cipherData2);

		return erg;
	}

}