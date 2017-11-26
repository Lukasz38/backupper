package edu.opa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

	protected static final Logger log = LoggerFactory.getLogger(Server.class);
	
	private static final String CONFIG_FILE_NAME = "config.properties";
	
	private FtpServer server;
	private int port;
	private String username;
	private String password;
	private String homeDir;
	
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	
	public void start()
	{
		loadConfig();
		init();
		try {
			server.start();
			log.info("Server is up.");
		} catch (FtpException e) {
			log.error("FTP server exception.\n"
					+ "Exception message: {}", e);
		}	
	}
	
	private void init()
	{
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		factory.setPort(port);
		serverFactory.addListener("default", factory.createListener());

		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		UserManager userManager = userManagerFactory.createUserManager();
		BaseUser user = new BaseUser();
		user.setName(username);
		user.setPassword(password);
		user.setHomeDirectory(homeDir);
		
		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(new WritePermission());
		user.setAuthorities(authorities);
		try	{
			userManager.save(user);
		} catch (FtpException e) {
			log.error("Exception while adding user.\n"
					+ "Exception message: {}", e);
		}
		serverFactory.setUserManager(userManager);
		server = serverFactory.createServer();   
		
		log.info("Server initialized.");
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

			port = Integer.parseInt(prop.getProperty("port"));
			username = prop.getProperty("user");
			password = prop.getProperty("password");
			homeDir = prop.getProperty("home.dir");
			
		} catch (IOException | URISyntaxException e) {
			log.error("Exception while loading configuration from config.properties.\n"
					+ "Exception message: {}", e);
			throw new RuntimeException();
		} 
		log.info("Configuration loaded:\n"
				+ "port: {}\n"
				+ "user: {}\n"
				+ "password: {}\n"
				+ "homeDir: {}",
				port, username, password, homeDir);
	}
}
