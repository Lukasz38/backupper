package edu.opa;

import java.util.List;

import edu.opa.xml.XMLStructure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableData {

	private static ObservableData instance = new ObservableData();
	private ObservableList<FileDTO> observableList;
	
	private ObservableData()
	{
		observableList = FXCollections.observableArrayList();
		observableList.addAll(XMLStructure.getInstance().listFiles());
	}

	public static ObservableData getInstance()
	{
		return instance;
	}
	
	public synchronized void update()
	{
		List<FileDTO> fileDTOs = XMLStructure.getInstance().listFiles();
		ObservableData.getInstance().getObservableList().clear();
		ObservableData.getInstance().getObservableList().addAll(fileDTOs);
	}

	public synchronized ObservableList<FileDTO> getObservableList()
	{
		return observableList;
	}
}
