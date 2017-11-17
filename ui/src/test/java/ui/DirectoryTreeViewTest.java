package ui;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.opa.view.objects.DirectoryTreeView;

@Ignore
public class DirectoryTreeViewTest {

	private DirectoryTreeView directoryTreeView;
	private File file;
	private File folder;
	
	public void setUpBeforeClass() throws URISyntaxException
	{
		file = new File(this.getClass().getResource("/testFile.txt").toURI());
		folder = new File(this.getClass().getResource("/testFolder").toURI()); 
		
		assertNotNull(file);
		assertNotNull(folder);
	}
	
	@Before
	public void setUp() 
	{
		directoryTreeView = new DirectoryTreeView();
		assertNotNull(directoryTreeView.getRoot());
	}
	
	@Test
	public void testAddFileNode()
	{
		directoryTreeView.addFileNode(file);
		
		int size = directoryTreeView.getRoot().getChildren().size();
		assertEquals(1, size);
		assertEquals(0, directoryTreeView.getRoot().getChildren().indexOf(file.getName()));
		assertNotNull(directoryTreeView.getRoot().getChildren().get(0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addFileNode_throwsIllegalArgumentException_ifFolderIsGivenAsArgument()
	{
		directoryTreeView.addFileNode(folder);
	}
	
	
	
	

}
