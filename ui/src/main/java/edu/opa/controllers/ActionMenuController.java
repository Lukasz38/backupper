package edu.opa.controllers;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;


import edu.opa.view.objects.DirectoryTreeView;
import edu.opa.xml.XMLStructure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ActionMenuController {

	@FXML
	private SplitPane splitPane;
	
	@FXML
	private ListView<File> filesChosenToArchiveView;
	
	@FXML
	private Button selectFilesToArchiveButton;
	
	@FXML
	private AnchorPane pane;
	
	private XMLStructure xmlStructure;
	
	public ActionMenuController()
	{
		xmlStructure = XMLStructure.getInstance();
	}
	
	@FXML
	public void initialize()
	{
		//pane.getChildren().add(new DirectoryTreeView());
		splitPane.getItems().add();
	}
	
	
	//private static ObservableList<String> 
	@FXML
	public void selectFilesToArchiveOnAction() 
	{
		Stage stage = new Stage();
		stage.setTitle("Wybierz pliki do archiwizacji");
		
		FileChooser fileChooser = new FileChooser();
		List<File> files = fileChooser.showOpenMultipleDialog(stage);
		
		xmlStructure.addFilesToXmlStructure(files);
		xmlStructure.save();
		
		List<File> selectedFiles = xmlStructure.listFiles();
		ObservableList<File> list = FXCollections.observableArrayList(selectedFiles);
		System.out.println(filesChosenToArchiveView);
		filesChosenToArchiveView.setItems(list);
	}
	
//	@FXML
//	public void selectDirectoryToArchiveOnAction()
//	{
//		Stage stage = new Stage();
//		stage.setTitle("Wybierz folder do archiwizacji");
//		
//		DirectoryChooser dirChooser = new DirectoryChooser();
//		File file = dirChooser.showDialog(stage);
//		
//		System.out.println(file.getName());	
//	}
}
