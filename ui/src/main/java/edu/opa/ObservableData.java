package edu.opa;

import edu.opa.xml.XMLStructure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableData {

	private static ObservableData instance = new ObservableData();
	private ObservableList<FileDTO> observableList;
	
	private ObservableData()
	{
		observableList = FXCollections.observableArrayList();
		observableList.addAll(XMLStructure.getInstance().listArchivedFiles());
	}

	public static ObservableData getInstance()
	{
		return instance;
	}

	public ObservableList<FileDTO> getObservableList()
	{
		return observableList;
	}
}
