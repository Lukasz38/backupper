package edu.opa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class Hasher {

	public static Optional<String> getHashForFile(File file) throws IllegalArgumentException
	{
		if(file.isDirectory()) {
			throw new IllegalArgumentException("Given File object cannot be a directory");
		}
		MessageDigest digest;
		try
		{
			digest = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
		     
		    byte[] byteArray = new byte[1024];
		    int bytesCount = 0;
		      
		    while ((bytesCount = fis.read(byteArray)) != -1) {
		        digest.update(byteArray, 0, bytesCount);
		    };
		     
		    fis.close();
		     
		    byte[] bytes = digest.digest();
		     
		    StringBuilder sb = new StringBuilder();
		    for(int i=0; i< bytes.length ;i++)
		    {
		        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		    return Optional.of(sb.toString());
		} catch (NoSuchAlgorithmException | IOException e)
		{
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
