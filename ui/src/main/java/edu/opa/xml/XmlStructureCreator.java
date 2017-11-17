package edu.opa.xml;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.w3c.dom.Node;

public interface XmlStructureCreator {

	public void createNewXmlFile();
	public void addFilesToXmlStructure(List<File> files);
	public void addFilesToNode(List<File> files, Node node);
	public Node addFileToXmlStructure(File file) throws IllegalArgumentException;
	public Node addFileToNode(File file, Node node) throws IllegalArgumentException;
	public Node addFolderToXmlStructure(File folder) throws IllegalArgumentException;
	public Node addFolderToNode(File folder, Node node) throws IllegalArgumentException;
	public void addLocalPathToNode(String path, Node node);
	public void addHashToNode(int hash, Node node);
	public void updateHash(int hash, Node node);
	public void addFileBackupDateToNode(LocalDateTime localDateTime, Node node );
	public void updateFileBackupDate(LocalDateTime localDateTime, Node node);
	boolean deleteFileNode(Node node);
	boolean deleteFileNode(String localPath);
	boolean deleteFolderNode(Node node);
	boolean deleteFolderNode(String localPath);
}
