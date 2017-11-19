package edu.opa.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.opa.ObservableData;
import edu.opa.xml.XMLStructure;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainSceneController {

	@FXML 
	private AnchorPane filesToArchivePane;
	@FXML
	private AnchorPane archivedFilesPane;
	
	@FXML
	private SplitPane mainPane;
	
	//Buttons
	@FXML
	private Button archiveFilesButton;
	@FXML
	private Button addFilesToArchiveButton;
	@FXML
	private Button showFilesToArchiveButton;
	@FXML
	private Button showArchivedFilesButton;
	@FXML
	private Button scheduleArchivingButton;
	
	private XMLStructure xmlStructure;
	
	public MainSceneController()
	{
		xmlStructure = XMLStructure.getInstance();
	}
	
	@FXML
	public void initialize() {}

	@FXML
	public void archiveFilesOnAction()
	{
		//TODO
	}
	
	@FXML
	public void addFilesToArchiveOnAction() 
	{
		Stage stage = new Stage();
		stage.setTitle("Wybierz pliki do archiwizacji");
		
		FileChooser fileChooser = new FileChooser();
		List<File> files = fileChooser.showOpenMultipleDialog(stage);
		
		xmlStructure.addFilesToXmlStructure(files);
		xmlStructure.save();
		
		List<File> selectedFiles = xmlStructure.listFilesToArchive();
		ObservableData.getInstance().getObservableFilesToArchive().clear();
		ObservableData.getInstance().getObservableFilesToArchive().addAll(selectedFiles);
	}
	
	@FXML
	public void showFilesToArchiveOnAction()
	{
		try {
			filesToArchivePane = FXMLLoader.load(getClass().getResource("/fxml/FilesToArchivePane.fxml"));
			mainPane.getItems().set(1, filesToArchivePane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void showArchivedFilesOnAction()
	{
		try {
			archivedFilesPane = FXMLLoader.load(getClass().getResource("/fxml/ArchivedFilesPane.fxml"));
			mainPane.getItems().set(1, archivedFilesPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void scheduleArchivingOnAction() 
	{
		//TODO
	}
}
