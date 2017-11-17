package edu.opa.view.objects;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class DirectoryTreeView extends TreeView<String>{

	private static final String rootDirectory = "backup";
	
	public DirectoryTreeView()
	{
		setRoot(new TreeItem<String>(rootDirectory));
	}
	
	public boolean addFileNode(File file) throws IllegalArgumentException
	{
		if(!file.isDirectory()) {
			return this.getRoot().getChildren().add(new TreeItem<String>(file.getName()));
		}
		else {
			throw new IllegalArgumentException("Given File object cannot be a directory.");
		}
	}
	
	public boolean addFileNode(File file, TreeItem<String> parentNode) throws IllegalArgumentException
	{
		if(!file.isDirectory()) {
			return parentNode.getChildren().add(new TreeItem<String>(file.getName()));
		}
		else {
			throw new IllegalArgumentException("Given File object cannot be a directory.");
		}
	}
	
	public Optional<TreeItem<String>> addFolderNode(File file) throws IllegalArgumentException
	{
		if(file.isDirectory()) {
			TreeItem<String> treeItem = new TreeItem<String>(file.getName());
			boolean result = this.getRoot().getChildren().add(treeItem);
			if(result)
				return Optional.of(treeItem);
			else
				return Optional.empty();
		}
		else {
			throw new IllegalArgumentException("Given File object must be a file, not directory.");
		}
	}
	
	public Optional<TreeItem<String>> addFolderNode(File file, TreeItem<String> parentNode) throws IllegalArgumentException
	{
		if(file.isDirectory()) {
			TreeItem<String> treeItem = new TreeItem<String>(file.getName());
			boolean result = parentNode.getChildren().add(treeItem);
			if(result)
				return Optional.of(treeItem);
			else
				return Optional.empty();
		}
		else {
			throw new IllegalArgumentException("Given File object must be a file, not directory.");
		}
	}
	
//	
//	public TreeItem<String> findTreeItemByName(String name) 
//	{
//		int index = this.
//	}
	
}
