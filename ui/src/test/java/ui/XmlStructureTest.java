package ui;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.opa.xml.XMLStructure;

public class XMLStructureTest {

	private XMLStructure xmlStructure;
	
	private static List<File> files = new ArrayList<>();
	private static File file;
	private static File file3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws URISyntaxException
	{
		URI uri1 = XMLStructureTest.class.getResource("/testFolder/testFile.txt").toURI();
		URI uri2 = XMLStructureTest.class.getResource("/testFolder/secondTestFile.txt").toURI();
		URI uri3 = XMLStructureTest.class.getResource("/testFolder/thirdTestFile.txt").toURI();
		file = new File(uri1);
		file3 = new File(uri3);
		files.add(new File(uri1));
		files.add(new File(uri2));
	}
	
	@Before
	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException
	{
		Field instance = XMLStructure.class.getDeclaredField("instance");
		Constructor<XMLStructure> constructor = XMLStructure.class.getDeclaredConstructor();
		constructor.setAccessible(true);
        instance.setAccessible(true);
        instance.set(null, constructor.newInstance());
        xmlStructure = XMLStructure.getInstance();
	}
	
	@Test
	public void xmlStructure_isNotNull()
	{
		assertNotNull(xmlStructure);
	}
	
	@Test
	public void testAddFilesToXmlStructure()
	{
		xmlStructure.addFilesToXmlStructure(files);
		assertEquals(2, xmlStructure.getDocument().getElementsByTagName(XMLStructure.FILE_NODE).getLength());
	}
	
	@Test
	public void testAddFileToXmlStructure()
	{
		assertEquals(0, xmlStructure.getDocument().getElementsByTagName(XMLStructure.FILE_NODE).getLength());
	
		Node node = xmlStructure.addFileToXmlStructure(file);
		assertEquals(1,
				xmlStructure.getDocument().getElementsByTagName(XMLStructure.FILE_NODE).getLength());
		assertEquals(file.getName(), 
				node.getAttributes().getNamedItem(XMLStructure.NAME_ATTRIBUTE).getNodeValue());
		System.out.println(node.getFirstChild().getNodeName());
		System.out.println(node.getFirstChild().getNodeValue());
		assertEquals(file.getAbsolutePath(), 
				node.getFirstChild().getNodeValue().equals(file.getAbsolutePath()));
		
		xmlStructure.addFileToXmlStructure(file);
		assertEquals(1, xmlStructure.getDocument().getElementsByTagName(XMLStructure.FILE_NODE).getLength());
		
		xmlStructure.addFileToXmlStructure(file3);
		assertEquals(2, xmlStructure.getDocument().getElementsByTagName(XMLStructure.FILE_NODE).getLength());
		
		assertEquals(2, xmlStructure.getDocument().getElementsByTagName(XMLStructure.LOCAL_PATH_NODE).getLength());
	}
}
