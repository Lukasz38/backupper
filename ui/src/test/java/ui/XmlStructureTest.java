package ui;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.opa.xml.XmlStructure;

public class XmlStructureTest {
	
	private static URI filePath;
	
	@BeforeClass
	public static void setUpBeforeClass()
	{
		try
		{
			filePath = XmlStructureTest.class.getClassLoader().getResource("document.xml").toURI();
		} catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		filePath = ClassLoader.getSystemResource("data/document.xml").toString();
		//filePath = XmlStructureTest.class.getResource("/data/document.xml").toString();
	}
	
	@Test
	public void testXmlStructureConstructor()
	{
		XmlStructure xmlStructure = new XmlStructure(filePath);
	}

}
