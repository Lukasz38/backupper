package edu.opa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
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
		ftpClient = new FTPClient();
		loadConfig();
	}
	
	private void loadConfig()
	{
		Properties prop = new Properties();

		URL url = this.getClass().getResource("/" + CONFIG_FILE_NAME);
		if(url == null) {
			log.error("Configuration file not found: " + CONFIG_FILE_NAME);
			throw new RuntimeException();
		}
		try(InputStream input = new FileInputStream(
				new File(url.toURI()))) {

			prop.load(input);

			server = prop.getProperty("server.ip");
			port = Integer.parseInt(prop.getProperty("server.port"));
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			
		} catch (IOException | URISyntaxException e) {
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
		ftpClient.connect(server, port);
		ftpClient.login(user, password);
	    ftpClient.enterLocalPassiveMode();
	    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	    log.info("Client connected.");
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
	
	public boolean sendFile(File file, String remoteFilename) throws IllegalArgumentException
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
			return false;
		}
	}
}
