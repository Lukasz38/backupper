package edu.opa.controllers;

import java.io.File;

import edu.opa.ObservableData;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class FilesToArchivePaneController {

	@FXML
	private ListView<File> filesToArchiveListView;
	
	public FilesToArchivePaneController() {}
	
	@FXML
	public void initialize()
	{
		filesToArchiveListView.setItems(ObservableData.getInstance().getObservableFilesToArchive());
	}
}
