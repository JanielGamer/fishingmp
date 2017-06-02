package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import window.SampleController;

public class ClientThread extends Thread{
	
	public Socket socket;
	
	
	public void run(){
		
		SampleController sample = new SampleController();
		
		try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String output;
	        String [] clientInput;
	        
	        while(true){
	          output = in.readLine();
	          clientInput = output.split("~");
	          
	          switch (clientInput[0]) {
	          
	          	case "SAY":
	          		
	          		sample.chatOutput.setText(sample.chatOutput +clientInput[1]+"\n");
	          		break;
	        	  
	        	  
	          }
	          
	          
	          
	          
	        }
	        
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
		
		
	}

	

}
