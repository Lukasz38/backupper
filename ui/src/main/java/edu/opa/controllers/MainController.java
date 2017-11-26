package edu.opa.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.opa.FileDTO;
import edu.opa.ObservableData;
import edu.opa.xml.XMLStructure;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Button archiveButton;

    @FXML
    private Button selectButton;

    @FXML
    private Button restoreButton;

    @FXML
    private Button scheduleButton;
    
    @FXML
    private TableView<FileDTO> tableView;
    
    private XMLStructure xmlStructure;
    
    public MainController()
    {
    	xmlStructure = XMLStructure.getInstance();
    }
    
    @FXML
	public void initialize()
    {
    	tableView.getColumns().get(0).setCellValueFactory(
				new PropertyValueFactory<>("localPath"));
    	tableView.getColumns().get(1).setCellValueFactory(
				new PropertyValueFactory<>("remotePath"));
    	tableView.getColumns().get(2).setCellValueFactory(
				new PropertyValueFactory<>("backupDate"));
    	tableView.setItems(ObservableData.getInstance().getObservableList());
    }
    
    @FXML
    public void addFilesToArchiveOnAction()
    {
    	
    }

	@FXML
	public void archiveFilesOnAction()
	{
		//TODO
	}
	
	@FXML
	public void restoreFilesOnAction()
	{
		//TODO
	}
	
	@FXML
	public void scheduleBackupOnAction()
	{
		//TODO
	}
	
	@FXML
	public void selectFilesOnAction()
	{
		Stage stage = new Stage();
		stage.setTitle("Select files to archive");
		
		FileChooser fileChooser = new FileChooser();
		List<File> files = fileChooser.showOpenMultipleDialog(stage);
		if(files == null) {
			files = new ArrayList<>();
		}
		xmlStructure.addFilesToXmlStructure(files);
		xmlStructure.save();
		
		List<FileDTO> fileDTOs = xmlStructure.listArchivedFiles();
		ObservableData.getInstance().getObservableList().clear();
		ObservableData.getInstance().getObservableList().addAll(fileDTOs);
	}

}
