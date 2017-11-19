package edu.opa.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

public class XMLStructure implements XMLStructureCreator {

	private static final String XML_FILE_RESOURCE_PATH = "/data/document.xml";
	
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
	
	private boolean loadFile()
	{
		try {
			URI uri = this.getClass().getResource(XML_FILE_RESOURCE_PATH).toURI();
			if(uri != null) {
				xmlFile = new File(uri);
				if(!xmlFile.exists()) {
					xmlFile.createNewFile();
					document = DocumentHelper.createDocument();
					document.addElement(ROOT_NODE);
					save();
				}
				return true;
			} 
			else 
				throw new URIReferenceException("Invalid URI.");
		} catch (URIReferenceException | URISyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean loadDocument()
	{
		try {
			if(document == null) {
				SAXReader reader = new SAXReader();
				if(xmlFile.exists()) {
						document = reader.read(xmlFile);
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
	
	public List<File> listFiles()
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
	
	private Optional<Element> fileElementExists(File file) throws IllegalArgumentException
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
	public void addHashToElement(int hash, Element element) throws IllegalArgumentException
	{
		if(!element.getName().equals(FILE_NODE)) {
			throw new IllegalArgumentException("Given Element object must be a \"file\" node.");
		}
		Element hashElement = DocumentHelper.createElement(HASH_NODE);
		hashElement.setText(Integer.toString(hash));
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
	public void updateHash(int hash, Element element) throws IllegalArgumentException
	{
		if(element.getName().equals(HASH_NODE)) {
			element.setText(Integer.toString(hash));
		}
		else if(element.getName().equals(FILE_NODE)) {
			Element hashElement = element.element(HASH_NODE);
			hashElement.setText(Integer.toString(hash));
		}
		else {
			throw new IllegalArgumentException("Illegal Element object. Must either \"" + FILE_NODE
					+ "\" or \"" + HASH_NODE + "\"");
		}
		
	}

	@Override
	public void updateBackupDate(LocalDateTime localDateTime, Element element) throws IllegalArgumentException
	{
		// TODO Auto-generated method stub
		
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
