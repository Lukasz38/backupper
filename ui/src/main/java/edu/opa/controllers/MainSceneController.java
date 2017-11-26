package edu.opa.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.opa.Client;
import edu.opa.Hasher;
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
	
	protected static final Logger log = LoggerFactory.getLogger(MainSceneController.class);

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
		Client client = Client.getInstance();
		try
		{
			client.connect();
		} catch (IOException e)
		{
			//log.error(arg0);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<File> files = xmlStructure.listFilesToArchive();
		for(File file : files) {
			System.out.println("File: " + file.getAbsolutePath());
			if(!file.exists()) {
				continue;
			}
			if(hashChanged(file)) {
				System.out.println("Hash changed for file: " + file.getAbsolutePath());
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
				System.out.println("File archived: " + file.getAbsolutePath());
			}
			else {
				System.out.println("Hash not changed for file: " + file.getAbsolutePath());
				continue;
			}
		}
		try
		{
			client.disconnect();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean hashChanged(File file) 
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
	
	private Thread getThreadByName(String threadName) {
		Set<Thread> threads = Thread.getAllStackTraces().keySet();
	    for (Thread t : threads) {
	        if (t.getName().equals(threadName)) {
	        	return t;
	        }
	    }
	    return null;
	}
	
	public void archiveInTheBackgroud()
	{
		Thread thread = getThreadByName("archive");
		if(thread == null) {
			System.out.println("Thread is null");
			thread = new Thread(() -> {
				//addFilesToArchiveOnAction();
			});
		}
		if(thread.isAlive()) {
			return;
		}
		else {
			thread.setName("archive");
			thread.start();
		}		
	}
}
