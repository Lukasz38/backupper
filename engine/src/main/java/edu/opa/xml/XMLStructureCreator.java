package edu.opa.xml;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.dom4j.Element;

public interface XMLStructureCreator {

	public void addFilesToXmlStructure(List<File> files) throws IllegalArgumentException;
	public Element addFileToXmlStructure(File file) throws IllegalArgumentException;
	public void addLocalPathToElement(String path, Element element) throws IllegalArgumentException;
	public void addHashToElement(String hash, Element element) throws IllegalArgumentException;
	public void addBackupDateToElement(LocalDateTime localDateTime, Element element) throws IllegalArgumentException;
	public void addRemotePathToElement(String path, Element element) throws IllegalArgumentException;
	
	public void updateHash(String hash, Element element) throws IllegalArgumentException;
	public void updateBackupDate(LocalDateTime localDateTime, Element element) throws IllegalArgumentException;

	public boolean deleteFileElement(String localPath);
}
