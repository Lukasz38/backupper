package edu.opa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class Sender {

	private FTPClient ftpClient;
	private String server;
	private int port;
	private String user;
	private String password;
	
	public Sender(String server, int port, String user, String password)
	{
		ftpClient = new FTPClient();
//		String server = "localhost";
//		int port = 2223;
//		String user = "luk";
//		String pass = "luk";
	}
	
	public boolean connect()
	{
		try	{
			ftpClient.connect(server, port);
			ftpClient.login(user, password);
		    ftpClient.enterLocalPassiveMode();
		    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean sendFile(File file, String remoteFilename) throws IllegalArgumentException
	{
		if(file.isDirectory()) {
			throw new IllegalArgumentException("Given File object must be a file, not directory.");
		}
		if(!file.exists()) {
			//TODO add log
			return false;
		}
		String remoteFile;
		if(remoteFilename.isEmpty()) {
			remoteFile = file.getName();
		}
		else {
			remoteFile = remoteFilename;
		}
		try(InputStream inputStream = new FileInputStream(file)) {
			boolean sent = ftpClient.storeFile(remoteFile, inputStream);
			if(sent) {
				return true;
			}
			else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
