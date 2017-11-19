package edu.opa.controllers;

import edu.opa.FileDTO;
import edu.opa.ObservableData;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ArchivedFilesPaneController {

	@FXML
	private TableView<FileDTO> archivedFilesTableView;
	
	public ArchivedFilesPaneController() {}
	
	@FXML
	public void initialize()
	{
		archivedFilesTableView.getColumns().get(0).setCellValueFactory(
				new PropertyValueFactory<>("remotePath"));
		archivedFilesTableView.getColumns().get(1).setCellValueFactory(
				new PropertyValueFactory<>("backupDate"));
		archivedFilesTableView.getColumns().get(0).setCellValueFactory(
				new PropertyValueFactory<>("localPath"));
		archivedFilesTableView.setItems(ObservableData.getInstance().getObservableArchivedFiles());
	}
}
