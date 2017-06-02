package window;

import main.ClientThread;
import main.Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SampleController {
	
	public Socket socket;
	
	public TextField ipField;
	public TextField portField;
	public TextField domainField;
	public Label canNotConnect;
	public TextField userName;
	public PasswordField password;
	
	
	
	//Buttons connectFrame
	
	public void ipConnect(){
		
		
		try {
			String ip = ipField.getText();
			int port = Integer.parseInt(portField.getText());
			
			if (password==null || userName==null){
				canNotConnect.setText("Need Pw and Username!");
			} else {
				Main.password=password.getText();
				Main.username=userName.getText();
			}
			
			socket = new Socket(ip,port);
			Main.socket = socket;
			Main.sendUserSettings();
			Main.startMainWindow();
			MainController.setPrintWriter();
			
			
		} catch (UnknownHostException e) {
			canNotConnect.setText("Can not connect!");
		} catch (IOException e) {
			canNotConnect.setText("Can not connect!");
		} catch (Exception e){
			e.printStackTrace();
			//canNotConnect.setText("Wrong input!");
		}
		
	}
	
	public void domainConnect(){
		
		try {
			String ip = InetAddress.getByName(domainField.getText()).toString();
			int port = 7070;

			
			if (password==null || userName==null){
				canNotConnect.setText("Need Pw and Username!");
			} else {
				Main.password=password.getText();
				Main.username=userName.getText();
			}
			
			socket = new Socket(ip,port);
			Main.socket = socket;
			Main.sendUserSettings();
			Main.startMainWindow();
			MainController.setPrintWriter();
			
			
			
		} catch (UnknownHostException e) {
			canNotConnect.setText("Can not connect!");
		} catch (IOException e) {
			canNotConnect.setText("Can not connect!");
		} catch (Exception e){
			canNotConnect.setText("Wrong input!");
		}
		
	}

}
