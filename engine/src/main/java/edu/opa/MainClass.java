package edu.opa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import edu.opa.xml.XMLStructure;

public class MainClass {

	public static void main(String[] args)
	{
		XMLStructure.getInstance().getHash(new File("D://Goals.jpg"));
	
//		String server = "localhost";
//		int port = 2223;
//		String user = "luk";
//		String pass = "luk";
//		
//		FTPClient ftpClient = new FTPClient();
//		try {
//			 
//            ftpClient.connect(server, port);
//            ftpClient.login(user, pass);
//            ftpClient.enterLocalPassiveMode();
// 
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
// 
//            // APPROACH #1: uploads first file using an InputStream
//            File firstLocalFile = new File("D:/Goals.jpg");
// 
//            String firstRemoteFile = "Goals_copy.jpg";
//            InputStream inputStream = new FileInputStream(firstLocalFile);
// 
//            System.out.println("Start uploading first file");
//            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
//            inputStream.close();
//            if (done) {
//                System.out.println("The first file is uploaded successfully.");
//            }
//            else 
//            	System.out.println("Fail");
// 
          //  ftpClient.list
//            // APPROACH #2: uploads second file using an OutputStreamp
//            inputStream = new FileInputStream(secondLocalFile);
// 
//            System.out.println("Start uploading second file");
//            OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
//            byte[] bytesIn = new byte[4096];
//            int read = 0;
// 
//            while ((read = inputStream.read(bytesIn)) != -1) {
//                outputStream.write(bytesIn, 0, read);
//            }
//            inputStream.close();
//            outputStream.close();
// 
//            boolean completed = ftpClient.completePendingCommand();
//            if (completed) {
//                System.out.println("The second file is uploaded successfully.");
//            }
// 
//        } catch (IOException ex) {
//            System.out.println("Error: " + ex.getMessage());
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (ftpClient.isConnected()) {
//                    ftpClient.logout();
//                    ftpClient.disconnect();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//	}
	}
}
		
