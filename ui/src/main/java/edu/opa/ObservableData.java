package edu.opa;

import java.io.File;

import edu.opa.xml.XMLStructure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableData {

	private static ObservableData instance = new ObservableData();
	private ObservableList<File> observableFilesToArchive;
	private ObservableList<FileDTO> observableArchivedFiles;
	
	private ObservableData()
	{
		observableFilesToArchive = FXCollections.observableArrayList();
		observableFilesToArchive.addAll(XMLStructure.getInstance().listFilesToArchive());
		observableArchivedFiles = FXCollections.observableArrayList();
		observableArchivedFiles.addAll(XMLStructure.getInstance().listArchivedFiles());
	}

	public static ObservableData getInstance()
	{
		return instance;
	}

	public ObservableList<File> getObservableFilesToArchive()
	{
		return observableFilesToArchive;
	}

	public ObservableList<FileDTO> getObservableArchivedFiles()
	{
		return observableArchivedFiles;
	}
}
