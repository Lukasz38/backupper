package edu.opa.ui;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
	
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("/fxml/Main.fxml"));
			BorderPane borderPane = loader.load();
			Scene scene = new Scene(borderPane);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Awesome backupper");
			primaryStage.setMinHeight(300);
			primaryStage.setMinWidth(300);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
