package edu.opa.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.xml.crypto.URIReferenceException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.opa.FileDTO;

public class XMLStructure implements XMLStructureCreator {

	private static final String XML_FOLDER_NAME = "data";
	private static final String XML_FILE_NAME = "document.xml";
	
	public static final String ROOT_NODE = "backup";
	public static final String FILE_NODE = "file";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String HASH_NODE = "hash";
	public static final String LOCAL_PATH_NODE = "localPath";
	public static final String REMOTE_PATH_NODE = "remotePath";
	public static final String DATE_TIME_NODE = "lastUpdate";
	
	private static XMLStructure instance = new XMLStructure();
	
	private File xmlFile;
	private Document document;
	
	private XMLStructure()
	{
		boolean fileExist = loadFile();
		if(!fileExist) {
			throw new RuntimeException();
		}
		boolean loaded = loadDocument();
		if(!loaded) {
			throw new RuntimeException();
		}
	}
	
	public static XMLStructure getInstance() 
	{
		return instance;
	}
	
	private boolean createFolder()
	{
		try {
			URL url = this.getClass().getResource("/");
			if(url != null) {
				URI uri = new URI(url.toString() + XML_FOLDER_NAME);
				File folder = new File(uri);
				if(!folder.exists()) {
					folder.mkdir();
				}
				return true;
			} 
			else 
				throw new URIReferenceException("Couldn't find resource.");
		} catch (URIReferenceException | URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean loadFile()
	{
		boolean folderCreated = createFolder();
		if(folderCreated) {
			try {
				URL url = this.getClass().getResource("/" + XML_FOLDER_NAME);
				if(url != null) {
					URI uri = new URI(url + "/" + XML_FILE_NAME);
					xmlFile = new File(uri);
					if(!xmlFile.exists()) {
						xmlFile.createNewFile();
						document = DocumentHelper.createDocument();
						document.addElement(ROOT_NODE);
						document.normalize();
						save();
					}
					return true;
				}
				else {
					throw new URIReferenceException("Invalid URI.");
				}
			}
			catch (URIReferenceException | URISyntaxException | IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		else {
			throw new RuntimeException("Couldn't create folder.");
		}
	}
	
	private boolean loadDocument()
	{
		try {
			if(document == null) {
				SAXReader reader = new SAXReader();
				if(xmlFile.exists()) {
						document = reader.read(xmlFile);
						document.normalize();
						return true;
				}
				else {
					return false;
				}
			}
			else {
				return true;
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean save()
	{
		OutputFormat format = OutputFormat.createPrettyPrint();
	    format.setEncoding("utf-8");
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile),format);
			writer.write(document);
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<File> listFilesToArchive()
	{
		List<File> files = new ArrayList<>();
		List<Node> nodes = document.getRootElement().selectNodes(FILE_NODE);
		for(Node node : nodes) {
			Element element = (Element) node;
			String path = element.element(LOCAL_PATH_NODE).getText();
			File file = new File(path);
			files.add(file);
		}
		return files;
	}
	
	public List<FileDTO> listArchivedFiles()
	{
		List<FileDTO> files = new ArrayList<>();
		List<Node> nodes = document.getRootElement().selectNodes(FILE_NODE + "//" + DATE_TIME_NODE);
		for(Node node : nodes) {
			Element element = (Element) node.getParent();
			String localPath = element.element(LOCAL_PATH_NODE).getText();
			LocalDateTime backupDate = LocalDateTime.parse(element.element(DATE_TIME_NODE).getText());
			String remotePath = element.element(REMOTE_PATH_NODE).getText();
			FileDTO fileDTO = new FileDTO(localPath, backupDate, remotePath);
			files.add(fileDTO);
		}
		return files;	
	}
	
	public List<FileDTO> listFiles()
	{
		List<FileDTO> files = new ArrayList<>();
		List<Node> nodes = document.getRootElement().selectNodes(FILE_NODE);
		for(Node node : nodes) {
			Element element = (Element) node;
			String localPath = element.element(LOCAL_PATH_NODE).getText();
			Element dateTimeElement = element.element(DATE_TIME_NODE);
			LocalDateTime backupDate = null;
			if(dateTimeElement != null) {
				backupDate = LocalDateTime.parse(dateTimeElement.getText());
			}
			Element remotePathElement = element.element(REMOTE_PATH_NODE);
		
			String remotePath = "";
			if(remotePathElement != null) {
				remotePath = remotePathElement.getText();
			}
			FileDTO fileDTO = new FileDTO(localPath, backupDate, remotePath);
			files.add(fileDTO);
		}
		return files;	
	}

	@Override
	public void addFilesToXmlStructure(List<File> files) throws IllegalArgumentException
	{
		for(File file : files) {
			if(file.isDirectory()) {
				throw new IllegalArgumentException("Given File object cannot be a directory.");
			}
			else {
				addFileToXmlStructure(file);
			}
		}
	}

	@Override
	public Element addFileToXmlStructure(File file) throws IllegalArgumentException
	{
		if(file.isDirectory()) {
			throw new IllegalArgumentException("Given File object cannot be a directory.");
		}
		Optional<Element> optional = fileElementExists(file);
		if(optional.isPresent()) {
			return optional.get();
		}
		Element element = document.getRootElement().addElement(FILE_NODE);
		element.addAttribute(NAME_ATTRIBUTE, file.getName());
		addLocalPathToElement(file.getAbsolutePath(), element);
		return element;
	}
	
	public Optional<Element> fileElementExists(File file) throws IllegalArgumentException
	{
		List<Node> nodes = document.getRootElement().selectNodes(FILE_NODE + "//" + LOCAL_PATH_NODE);
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			if(element.getText().equals(file.getAbsolutePath())) {
				return Optional.of(element.getParent());
			}
		}
		return Optional.empty();
	}

	@Override
	public void addLocalPathToElement(String path, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"" + FILE_NODE + "\" node.");
		}
		Element localPathElement = DocumentHelper.createElement(LOCAL_PATH_NODE);
		localPathElement.setText(path);
		element.add(localPathElement);
	}

	@Override
	public void addHashToElement(String hash, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"file\" node.");
		}
		Element hashElement = DocumentHelper.createElement(HASH_NODE);
		hashElement.setText(hash);
		element.add(hashElement);
	}

	@Override
	public void addBackupDateToElement(LocalDateTime localDateTime, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"file\" node.");
		}
		Element dateTimeElement = DocumentHelper.createElement(DATE_TIME_NODE);
		dateTimeElement.setText(localDateTime.toString());
		element.add(dateTimeElement);	
	}

	@Override
	public void addRemotePathToElement(String path, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"file\" node.");
		}
		Element remotePathElement = DocumentHelper.createElement(REMOTE_PATH_NODE);
		remotePathElement.setText(path);
		element.add(remotePathElement);
	}

	@Override
	public void updateHash(String hash, Element element) throws IllegalArgumentException
	{
		if(element.getName().equals(HASH_NODE)) {
			element.setText(hash);
		}
		else if(element.getName().equals(FILE_NODE)) {
			Element hashElement = element.element(HASH_NODE);
			if(hashElement == null) {
				addHashToElement(hash, element);
			}
			else {
				hashElement.setText(hash);
			}
			
		}
		else {
			throw new IllegalArgumentException("Illegal Element object. Must either \"" + FILE_NODE
					+ "\" or \"" + HASH_NODE + "\"");
		}
	}

	@Override
	public void updateBackupDate(LocalDateTime localDateTime, Element element) throws IllegalArgumentException
	{
		if(element.getName().equals(DATE_TIME_NODE)) {
			element.setText(localDateTime.toString());
		}
		else if(element.getName().equals(FILE_NODE)) {
			Element dateTimeElement = element.element(DATE_TIME_NODE);
			if(dateTimeElement == null) {
				addBackupDateToElement(localDateTime, element);
			}
			else {
				dateTimeElement.setText(localDateTime.toString());
			}
		}
		else {
			throw new IllegalArgumentException("Illegal Element object. Must either \"" + FILE_NODE
					+ "\" or \"" + HASH_NODE + "\"");
		}
	}

	@Override
	public boolean deleteFileElement(String localPath)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public Document getDocument()
	{
		return document;
	}
	
	public Optional<String> getHash(File file)
	{
		String xpath = FILE_NODE + "[localPath='" + file.getAbsolutePath() + "']";
		List<Node> nodes = document.getRootElement().selectNodes(xpath);
		Element fileElement = (Element) nodes.get(0);
		Element hashElement = fileElement.element(HASH_NODE);
		if(hashElement == null) {
			return Optional.empty();
		}
		if(hashElement.getText().isEmpty()) {
			return Optional.empty();
		}
		else {
			return Optional.of(hashElement.getText());
		}
	}
	
	public Optional<String> getRemotePath(File file) 
	{
		String xpath = FILE_NODE + "[localPath='" + file.getAbsolutePath() + "']";
		List<Node> nodes = document.getRootElement().selectNodes(xpath);
		Element fileElement = (Element) nodes.get(0);
		Element remotePathElement = fileElement.element(REMOTE_PATH_NODE);
		if(remotePathElement.getText().isEmpty() || remotePathElement == null) {
			return Optional.empty();
		}
		else {
			return Optional.of(remotePathElement.getText());
		}
	}
	
	public Optional<LocalDateTime> getLastUpdateTime(File file)
	{
		String xpath = FILE_NODE + "[localPath='" + file.getAbsolutePath() + "']";
		List<Node> nodes = document.getRootElement().selectNodes(xpath);
		Element fileElement = (Element) nodes.get(0);
		Element dateElement = fileElement.element(DATE_TIME_NODE);
		if(dateElement.getText().isEmpty() || dateElement == null) {
			return Optional.empty();
		}
		else {
			return Optional.of(LocalDateTime.parse(dateElement.getText()));
		}
	}
	
//	@Override
//	public boolean deleteFolderNode(Node node)
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean deleteFolderNode(String localPath)
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public Node addFolderToXmlStructure(File folder) throws IllegalArgumentException
//	{
//		return addFileToNode(folder, document.getFirstChild());
//	}
//	
//	@Override
//	public Node addFileToNode(File file, Node node) throws IllegalArgumentException
//	{
//		Element element = document.createElement(FILE_NODE);
//		element.setAttribute(NAME_ATTRIBUTE, file.getName());
//		node.appendChild(element);
//		return element;
//	}
//
//	@Override
//	public Node addFolderToNode(File folder, Node node) throws IllegalArgumentException
//	{
//		Element element = document.createElement(FILE_NODE);
//		element.setAttribute(NAME_ATTRIBUTE, folder.getName());
//		node.appendChild(element);
//		return element;
//	}
//
//	@Override
//	public void addFilesToNode(List<File> files, Node node)
//	{
//		for(File file : files) {
//			if(file.isDirectory()) {
//				Node createdNode = addFolderToNode(file, node);
//				addFilesToNode(Arrays.asList(file.listFiles()), createdNode);
//			}
//			else {
//				addFileToNode(file, node);
//			}
//		}
//	}
}
