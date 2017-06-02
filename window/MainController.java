package window;

import main.ClientThread;
import main.Main;
import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController {

	public Socket socket;
	public MainController controller;
	public String s;

	@FXML
	public TextField chatInput;
	@FXML
	public TextField inputField;
	@FXML
	public TextArea chatOutput;
	@FXML
	public Label wrongSyntax;
	@FXML
	public Label statsLabel;

	static PrintWriter out;

	public MainController() {
		controller = this;
		new Thread(new InputHandler()).start();
		new Thread(() -> {
			try {
				PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
				out.write("STATS\n");
				out.flush();
				Thread.sleep(1000);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	public static void setPrintWriter() {
		try {
			PrintWriter out1 = new PrintWriter(Main.socket.getOutputStream());
			out = out1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// MenuBar mainframe

	// Buttons mainframe

	public void sendChat() {
		out.write("SAY " + chatInput.getText() + "\n");
		out.flush();
		chatInput.clear();

	}

	public void fishAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("FISH " + inputField.getText() + "\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buyAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("BUY " + inputField.getText() + "\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sellGAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("SELL " + inputField.getText() + "\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sellNAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("SELL " + inputField.getText() + "\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mineAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("MINE " + inputField.getText() + "\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("STATS\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pauseAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("PAUSE\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unpauseAction() {
		try {
			PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
			out.write("UNPAUSE\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStats() {
		statsLabel.setText(s);
	}
	
	public void setDialog() {
		wrongSyntax.setText(s);
	}

	class InputHandler implements Runnable {

		@Override
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(Main.socket.getInputStream()));
				PrintWriter out = new PrintWriter(Main.socket.getOutputStream());
				String output;
				String[] clientInput;

				while (true) {

					output = in.readLine();
					clientInput = output.split("~");

					switch (clientInput[0]) {

					case "SAY":
						chatOutput.setText(chatOutput.getText() + clientInput[1] + "\n");
						break;

					case "DIALOG":
						s = clientInput[1];
						Platform.runLater(() -> {
							setDialog();
						});
						break;

					case "STATS":
						s = clientInput[1];
						Platform.runLater(() -> {
							setStats();
						});
						break;
					case "KICK":
						Main.socket.close();
						Platform.exit();
						

					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
