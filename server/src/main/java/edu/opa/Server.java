package edu.opa;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class Server {

	public static void main(String[] args) {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		// set the port of the listener
		factory.setPort(2223);
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		
		
		
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
	
		UserManager um = userManagerFactory.createUserManager();
		BaseUser user = new BaseUser();
		user.setName("luk");
		user.setPassword("luk");
		user.setHomeDirectory("D:/FTP");
		
		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(new WritePermission());
		user.setAuthorities(authorities);
		try
		{
			um.save(user);
		} catch (FtpException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// start the server
		serverFactory.setUserManager(um);
		FtpServer server = serverFactory.createServer();   
		
		try
		{
			server.start();
		} catch (FtpException e)
		{
			// TODO Auto-generated catch block
			server.stop();
			e.printStackTrace();
		}
	}
}
