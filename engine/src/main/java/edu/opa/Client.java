package edu.opa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

	protected static final Logger log = LoggerFactory.getLogger(Client.class);
	private static final String CONFIG_FILE_NAME = "config.properties";
	
	private static Client instance = new Client();
	
	private FTPClient ftpClient;
	private String server;
	private int port;
	private String user;
	private String password;
	
	public static Client getInstance()
	{
		return instance;
	}
	
	private Client()
	{
		log.debug("Constructor started");
		ftpClient = new FTPClient();
		loadConfig();
		log.debug("Client is up.");
	}
	
	private void loadConfig()
	{
		log.debug("Loading config");
		Properties prop = new Properties();

//		URL url = this.getClass().getResource("/" + CONFIG_FILE_NAME);
//		if(url == null) {
//			log.error("Configuration file not found: " + CONFIG_FILE_NAME);
//			throw new RuntimeException();
//		}
		try(InputStream input = this.getClass().getResourceAsStream("/" + CONFIG_FILE_NAME)) {

			log.debug("Got stream");
			prop.load(input);

			server = prop.getProperty("server.ip");
			port = Integer.parseInt(prop.getProperty("server.port"));
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			
		} catch (IOException e) {
			log.error("Exception while loading configuration from config.properties.\n"
					+ "Exception message: {}", e);
			throw new RuntimeException();
		} 
		log.info("Configuration loaded:\n"
				+ "server: {}\n"
				+ "port: {}\n"
				+ "user: {}\n"
				+ "password: {}",
				server, port, user, password);
	}
	
	public void connect() throws IOException
	{
		try {
			ftpClient.connect(server, port);
			ftpClient.login(user, password);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			log.info("Client connected.");
		} catch (Exception e) {
			throw new IOException("Couldn't establish connection.");
		}
	}
	
	public boolean isConnected()
	{
		return ftpClient.isConnected();
	}
	
	public void disconnect() throws IOException
	{
		ftpClient.disconnect();
		log.info("Client disconnected.");
	}
	
	public boolean sendFile(File file, String remoteFilename) 
			throws IllegalArgumentException, IOException
	{
		if(file.isDirectory()) {
			throw new IllegalArgumentException("Given \"File\" object is a directory, "
					+ "but must be a file.");
		}
		if(!file.exists()) {
			log.warn("File doesn't exist: " + file.getAbsolutePath());
			return false;
		}
		if(remoteFilename.isEmpty()) {
			remoteFilename = file.getName();
		}
		try(InputStream inputStream = new FileInputStream(file)) {
			boolean sent = ftpClient.storeFile(remoteFilename, inputStream);
			if(sent) {
				return true;
			}
			else {
				log.warn("Failed to send the file: " + file.getAbsolutePath());
				return false;
			}
		} catch (IOException e) {
			log.error("Exception while trying to send the file: " + file.getAbsolutePath() + ".\n"
					+ "Exception message: {}", e);
			throw new IOException("An error occured while archiving a file.");
		}
	}
	
	public boolean restoreFiles(String remotePath, String localPath) throws IOException
	{	
		File newLocalFile = new File(localPath);
		try(OutputStream outputStream = new FileOutputStream(newLocalFile)) {
			if(fileExists(remotePath)) {
				log.debug("File exists on a remote server");
				boolean retrieved = ftpClient.retrieveFile(remotePath, outputStream);
				if(retrieved) {
					log.debug("Retrieved file");
					return true;
				} else {
					return false;
				}
			}
			else {
				return false;
			}
		} catch (IOException e) {
			log.error("Exception thrown while retrieving file.\n"
					+ "Exception message: {}", e);
			throw new IOException("An error occured while restoring a file.");
		}
	}
	
	private boolean fileExists(String remotePath) throws IOException
	{
		FTPFile[] ftpFiles = ftpClient.listFiles();
		for(FTPFile file : ftpFiles) {
			if(file.getName().equals(remotePath)) {
				return true;
			}
		}
		return false;
	}
}
