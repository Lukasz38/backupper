package edu.opa.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.opa.Client;
import edu.opa.FileDTO;
import edu.opa.Hasher;
import edu.opa.ObservableData;
import edu.opa.xml.XMLStructure;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
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
	public void archiveFilesOnAction()
	{
		archiveFiles();
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
		
		List<FileDTO> fileDTOs = xmlStructure.listFiles();
		ObservableData.getInstance().getObservableList().clear();
		ObservableData.getInstance().getObservableList().addAll(fileDTOs);
	}
	
	private void archiveFiles()
	{
		Client client = Client.getInstance();
		try {
			if(!client.isConnected()) {
				client.connect();
			}
			List<File> files = xmlStructure.listFilesToArchive();
			for(File file : files) {
				if(!file.exists()) {
					log.warn("File doesn't exist: " + file.getAbsolutePath());
					continue;
				}
				if(isHashChanged(file)) {
					client.sendFile(file, file.getName());
					Optional<Element> optElem = xmlStructure.fileElementExists(file);
					if(!optElem.isPresent()) {
						throw new UnknownError();
					}
					Element fileElement = optElem.get();
					xmlStructure.updateHash(Hasher.getHashForFile(file).get(), fileElement);	
					xmlStructure.updateBackupDate(LocalDateTime.now(), fileElement);
					xmlStructure.addRemotePathToElement(file.getName(), fileElement);
					xmlStructure.save();
				}
				else {
					continue;
				}
			}
			client.disconnect();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private boolean isHashChanged(File file) 
	{
		Optional<String> optCurrentHash = Hasher.getHashForFile(file);
		Optional<String> optPreviousHash = xmlStructure.getHash(file);
		if(!optCurrentHash.isPresent()) {
			throw new UnknownError("Couldn't generate hash for file.");
		}
		else if(!optPreviousHash.isPresent()) {
			return true;
		}
		else {
			String currentHash = optCurrentHash.get();
			System.out.println("Current hash: " + currentHash);
			String previousHash = optPreviousHash.get();
			System.out.println("Previous hash: " + previousHash);
			if(currentHash.equals(previousHash)) {
				return false;
			}
			else {
				return true;
			}
		}
	}
}
