package edu.opa.ui;
	
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	@Override
	public void start(Stage primaryStage) {
	
		try {
			FXMLLoader loader = new FXMLLoader();
			InputStream is = this.getClass().getResourceAsStream("/fxml/Main.fxml");
			log.info("Is available: {}", is.available());
			//URL url = this.getClass().getResource("/fxml/Main.fxml");
			//log.info("URL: {}", url);
			//loader.setLocation(url);
			log.info("Main.fxml loaded");
			BorderPane borderPane = loader.load(is);
			log.info("Border pane loaded");
			Scene scene = new Scene(borderPane);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Awesome backupper");
			primaryStage.setMinHeight(300);
			primaryStage.setMinWidth(300);
			primaryStage.show();
		} catch(Exception e) {
			log.info("Exception in main class: {}", e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
