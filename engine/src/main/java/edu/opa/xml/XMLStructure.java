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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.opa.FileDTO;

public class XMLStructure implements XMLStructureCreator {

	private static final Logger log = LoggerFactory.getLogger(XMLStructure.class);
	
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
		boolean fileLoaded = loadFile();
		if(!fileLoaded) {
			log.error("Failed to load file: {}", XML_FILE_NAME);
			throw new RuntimeException();
		}
		boolean documentLoaded = loadDocument();
		if(!documentLoaded) {
			log.error("Failed to load document from file: {}", XML_FILE_NAME);
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
					log.debug("Folder created: {}", folder.getAbsolutePath());
				}
				return true;
			} 
			else {
				log.error("Couldn't find resource.");
				return false;
			}
		} catch (URISyntaxException e) {
			log.error("Exception while creating URI.\n"
					+ "Exception message: {}", e);
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
						log.debug("File created: {}", xmlFile.getAbsolutePath());
					}
					return true;
				}
				else {
					log.error("Couldn't find resource: /{}", XML_FOLDER_NAME);
					return false;
				}
			}
			catch (URISyntaxException | IOException e) {
				log.error("Exception while creating URI.\n"
						+ "Exception message: {}", e);
				return false;
			}
		}
		else {
			log.warn("Folder not found: {}", XML_FOLDER_NAME);
			throw new RuntimeException();
		}
	}
	
	private boolean loadDocument()
	{
		try {
			if(document == null) {
				SAXReader reader = new SAXReader();
				if(xmlFile.exists()) {
					document = reader.read(xmlFile);
					//document.normalize();
					log.debug("Document read.");
					return true;
				}
				else {
					log.warn("File doesn't exist: {}", xmlFile.getAbsolutePath());
					return false;
				}
			}
			else {
				log.debug("Document already loaded.");
				return true;
			}
		} catch (DocumentException e) {
			log.error("Exception while reading document.\n"
					+ "Exception message: {}", e);
			return false;
		}
	}
	
	public boolean save()
	{
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setIndent(false);
		format.setPadText(false);
		format.setTrimText(false);
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
	
	public synchronized List<File> listFilesToArchive()
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
	
	public synchronized List<FileDTO> listArchivedFiles()
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
	
	public synchronized List<FileDTO> listFiles()
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
	public synchronized void addFilesToXmlStructure(List<File> files) throws IllegalArgumentException
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
	public synchronized Element addFileToXmlStructure(File file) throws IllegalArgumentException
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
	
	public synchronized Optional<Element> fileElementExists(File file) throws IllegalArgumentException
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
	public synchronized void addLocalPathToElement(String path, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"" + FILE_NODE + "\" node.");
		}
		Element localPathElement = DocumentHelper.createElement(LOCAL_PATH_NODE);
		localPathElement.setText(path);
		element.add(localPathElement);
	}

	@Override
	public synchronized void addHashToElement(String hash, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"file\" node.");
		}
		Element hashElement = DocumentHelper.createElement(HASH_NODE);
		hashElement.setText(hash);
		element.add(hashElement);
	}

	@Override
	public synchronized void addBackupDateToElement(LocalDateTime localDateTime, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"file\" node.");
		}
		Element dateTimeElement = DocumentHelper.createElement(DATE_TIME_NODE);
		dateTimeElement.setText(localDateTime.toString());
		element.add(dateTimeElement);	
	}

	@Override
	public synchronized void addRemotePathToElement(String path, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"file\" node.");
		}
		Element remotePathElement = DocumentHelper.createElement(REMOTE_PATH_NODE);
		remotePathElement.setText(path);
		element.add(remotePathElement);
	}

	@Override
	public synchronized void updateHash(String hash, Element element) throws IllegalArgumentException
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
	public synchronized void updateBackupDate(LocalDateTime localDateTime, Element element) throws IllegalArgumentException
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
	
	public synchronized Optional<String> getHash(File file)
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
	
	public synchronized Optional<String> getRemotePath(File file) 
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
	
	public synchronized Optional<LocalDateTime> getLastUpdateTime(File file)
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
}
