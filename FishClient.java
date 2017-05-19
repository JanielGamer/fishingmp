package fishClientconsole;

import java.net.Socket;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;
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





public class FishClient {
  
  static boolean akzeptanz = false;
  static Socket socket;
  static Scanner inputsc;
  
  public static void main(String[] args) throws IOException {
    
    try {
      socket = new Socket("127.0.0.1",63564);
      
      BufferedReader inputsc = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter out = new PrintWriter(socket.getOutputStream()); 
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      
      String nickname;
      Console console=null;
      char[] pwd=null;
      
      System.out.print("Nickname: ");
      nickname = inputsc.readLine() + "\n";
      
      try{
        console=System.console();
        pwd = console.readPassword("Password: ");
        
      } catch(Exception ex) {
        
        // if any error occurs
        ex.printStackTrace();      
      }
      
      
      String pwdsend = new String(pwd);
      
      pwdsend = verschlüsseln(pwdsend);
      
      out.write(nickname);
      out.flush(); 
      out.write(pwdsend+"\n"); 
      out.flush();
      
      System.out.println("FISH, BUY, SELL, STATS, leave");
      System.out.println("----------------------------");
      
      
      new Thread(new serverInput()).start();
      new Thread(new serverOutput()).start();
      
      
      
      
      
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    
    
    
  }
    
  static class serverOutput implements Runnable{
    public void run() {
      String[] input;
      String toserver;
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream()); 
        while (true) {
          
          toserver = in.readLine();
          
          if (toserver=="leave"){
            out.close();
            in.close();
            socket.close();
            System.exit(0);
          } else{
            out.write(toserver+"\n");
            out.flush();
          }
          
          
        }
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
    
  static class serverInput implements Runnable{
    public void run() {
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream()); 
        String output;
        
        while(true){
          output = in.readLine();
          if (output.equals("stop")) {
            in.close();
            out.close();
            socket.close();
            System.exit(0);
          } else if (output.equals("reset")) {
            out.write("RESET\n");
            out.flush();
          } else {
            System.out.println(output);
          }
          
          
          
        }
        
      } catch (IOException e) {
        e.printStackTrace();
      }
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

/*
 * String keyStr = "lqarfwhnfpoawkfgfe8352phf101234hf0ß1ßufh24e3f2jnwqd";
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
      
      BASE64Decoder myDecoder2 = new BASE64Decoder();
      byte[] crypted2 = myDecoder2.decodeBuffer(geheim);
 
      Cipher cipher2 = Cipher.getInstance("AES");
      cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec);
      byte[] cipherData2 = cipher2.doFinal(crypted2);
      String erg = new String(cipherData2);
      */

