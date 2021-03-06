package edu.opa.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.opa.Client;
import edu.opa.FileDTO;
import edu.opa.Hasher;
import edu.opa.ObservableData;
import edu.opa.xml.XMLStructure;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    private Client client;
    
    public MainController()
    {
    	xmlStructure = XMLStructure.getInstance();
    	client = Client.getInstance();
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
		archiveInTheBackgroud();
	}
	
	@FXML
	public void restoreFilesOnAction()
	{
		//TODO
	}
	
	@FXML
	public void scheduleBackupOnAction()
	{
		Runnable runnable = () -> {
			archiveFiles();
		};
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); 
        scheduler.scheduleAtFixedRate(runnable, 10, 60, TimeUnit.SECONDS);
        log.info("Archived at fixed rate");
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
		
		ObservableData.getInstance().update();
	}
	
	private void archiveFiles()
	{
		boolean sthArchived = false;
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
					boolean sent = client.sendFile(file, file.getName());
					if(sent) {
						sthArchived = true;
						Optional<Element> optElem = xmlStructure.fileElementExists(file);
						if(!optElem.isPresent()) {
							throw new UnknownError();
						}
						Element fileElement = optElem.get();
						xmlStructure.updateHash(Hasher.getHashForFile(file).get(), fileElement);	
						xmlStructure.updateBackupDate(LocalDateTime.now(), fileElement);
						xmlStructure.addRemotePathToElement(file.getName(), fileElement);
						xmlStructure.save();
						ObservableData.getInstance().update();
					}
				}
				else {
					continue;
				}
			}
			Runnable runnable;
			if(sthArchived) {
				runnable = () -> {
					String message = "Successful archiving.";
					showAlert(message, AlertType.INFORMATION);
				};
			}
			else {
				runnable = () -> {
					String message = "Nothing to archive. Everything is up to date.";
					showAlert(message, AlertType.INFORMATION);
				};
			};
			Platform.runLater(runnable);
		} catch (IOException e) {
			Platform.runLater(() -> {
				String message = e.getMessage() + " Archiving aborted. "
						+ "Please, check your network connection.";
				showAlert(message, AlertType.WARNING);
			});
		} finally {
			try {
				ObservableData.getInstance().update();
				client.disconnect();
			} catch (IOException e) {
				log.error("Exception while disconnecting client.\n"
						+ "Exception message: {}", e);
			}
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
			String previousHash = optPreviousHash.get();
			if(currentHash.equals(previousHash)) {
				return false;
			}
			else {
				return true;
			}
		}
	}
	
	private void showAlert(String message, AlertType alertType)
	{
		Alert alert = new Alert(alertType);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private Thread getThreadByName(String threadName) {
		Set<Thread> threads = Thread.getAllStackTraces().keySet();
	    for (Thread t : threads) {
	        if (t.getName().equals(threadName)) {
	        	return t;
	        }
	    }
	    return null;
	}
	
	private void archiveInTheBackgroud()
	{
		Thread thread = getThreadByName("archive");
		if(thread == null) {
			log.debug("\"archive\" thread is null.");
			thread = new Thread(() -> {
				archiveFiles();
			});
		}
		if(thread.isAlive()) {
			log.debug("\"archive\" thread is alive.");
			showAlert("Archiving already started.", AlertType.INFORMATION);
			return;
		}
		else {
			thread.setName("archive");
			thread.start();
		}		
	}
}
