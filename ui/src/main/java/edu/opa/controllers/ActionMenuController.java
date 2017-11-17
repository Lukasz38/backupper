package edu.opa.controllers;

import java.io.File;
import java.util.List;

import edu.opa.view.objects.DirectoryTreeView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ActionMenuController {

	@FXML
	private Button selectFilesToArchiveButton;
	
	@FXML
	private AnchorPane pane;
	
	public ActionMenuController()
	{
		
	}
	
	@FXML
	public void initialize()
	{
		
		pane.getChildren().add(new DirectoryTreeView());
	}
	
	
	//private static ObservableList<String> 
	@FXML
	public void selectFilesToArchiveOnAction() 
	{
		Stage stage = new Stage();
		stage.setTitle("Wybierz pliki do archiwizacji");
		
		FileChooser fileChooser = new FileChooser();
		List<File> files = fileChooser.showOpenMultipleDialog(stage);
		
		for(File f : files) {
			System.out.println(f.getName());
		}
	}
	
	@FXML
	public void selectDirectoryToArchiveOnAction()
	{
		Stage stage = new Stage();
		stage.setTitle("Wybierz folder do archiwizacji");
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		File file = dirChooser.showDialog(stage);
		
		System.out.println(file.getName());	
	}
}
