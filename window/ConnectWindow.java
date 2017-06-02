package window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.Main;

public class ConnectWindow extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			Pane root = (Pane)FXMLLoader.load(getClass().getResource("ConnectWindow.fxml"));
			Scene scene = new Scene(root,200,250);
			
			primaryStage.setTitle("Connect to Server");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	
}
