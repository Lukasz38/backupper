package edu.opa.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.xml.crypto.URIReferenceException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;

public class XmlStructure implements XmlStructureCreator {

	private static final String ROOT_NODE = "backup";
	private static final String FOLDERS_NODE = "folders";
	private static final String FOLDER_NODE = "folder";
	private static final String FILES_NODE = "files";
	private static final String FILE_NODE = "file";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String HASH_NODE = "hash";
	private static final String LOCAL_PATH_NODE = "localPath";
	private static final String REMOTE_PATH_NODE = "remotePath";
	private static final String DATE_TIME_NODE = "lastUpdate";
	
	private static Document document;
	
	public XmlStructure(URI filePath)
	{
		boolean loaded = loadDocument(filePath);
		if(!loaded) {
			throw new RuntimeException();
		}
	}
	
	private boolean loadDocument(URI filePath)
	{
		try {
			if(filePath != null) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				File xmlFile = new File(filePath);
				if(xmlFile.exists()) {
					document = builder.parse(xmlFile);
					return true;
				}
				else {
					//TODO create new xml file
					throw new FileNotFoundException();
				}
			} 
			else 
				throw new URIReferenceException("Invalid URI.");
		} catch (ParserConfigurationException | URIReferenceException | SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	@Override
	public void addFilesToXmlStructure(List<File> files)
	{
		for(File file : files) {
			if(file.isDirectory()) {
				Node node = addFolderToXmlStructure(file);
				addFilesToNode(Arrays.asList(file.listFiles()), node);
			}
			else {
				addFileToXmlStructure(file);
			}
		}
	}

	@Override
	public Node addFolderToXmlStructure(File folder) throws IllegalArgumentException
	{
		return addFileToNode(folder, document.getFirstChild());
	}

	@Override
	public void addLocalPathToNode(String path, Node node)
	{
		Element element = document.createElement(LOCAL_PATH_NODE);
		element.setTextContent(path);
		node.appendChild(element);
	}

	@Override
	public void addHashToNode(int hash, Node node)
	{
		Element element = document.createElement(LOCAL_PATH_NODE);
		element.setTextContent(Integer.toString(hash));
		node.appendChild(element);
	}

	@Override
	public void updateHash(int hash, Node node)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void addFileBackupDateToNode(LocalDateTime localDateTime, Node node)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFileBackupDate(LocalDateTime localDateTime, Node node)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteFileNode(Node node)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteFileNode(String localPath)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteFolderNode(Node node)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteFolderNode(String localPath)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createNewXmlFile()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Node addFileToXmlStructure(File file) throws IllegalArgumentException
	{
		return addFileToNode(file, document.getFirstChild());
	}

	@Override
	public Node addFileToNode(File file, Node node) throws IllegalArgumentException
	{
		Element element = document.createElement(FILE_NODE);
		element.setAttribute(NAME_ATTRIBUTE, file.getName());
		node.appendChild(element);
		return element;
	}

	@Override
	public Node addFolderToNode(File folder, Node node) throws IllegalArgumentException
	{
		Element element = document.createElement(FOLDER_NODE);
		element.setAttribute(NAME_ATTRIBUTE, folder.getName());
		node.appendChild(element);
		return element;
	}

	@Override
	public void addFilesToNode(List<File> files, Node node)
	{
		for(File file : files) {
			if(file.isDirectory()) {
				Node createdNode = addFolderToNode(file, node);
				addFilesToNode(Arrays.asList(file.listFiles()), createdNode);
			}
			else {
				addFileToNode(file, node);
			}
		}
	}

}
