package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sun.misc.BASE64Encoder;
import window.*;

public class Main {
	
	public static String[] arguments;
	public static Socket socket;
	public static String password;
	public static String username;
	
	

	public static void main(String[] args) {
		
		ConnectWindow.launch(ConnectWindow.class, args);
		

	}
	

	
	public static void startMainWindow(){
		
		MainWindow window = new MainWindow();
		window.mainWindow();
		
	}
	
	public static void sendUserSettings() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			String pwdsend;
			pwdsend = verschlüsseln(password);
			
			out.write(username+"\n");
		    out.flush(); 
		    out.write(pwdsend+"\n"); 
		    out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	    
		
		
	}
	
	public static String verschlüsseln(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		 

	      String keyStr = "lqarfwhnfpoawkfgfe8352phf101234hf0ß1ßufh24e3f2jnwqd";
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
	
	
	

}
