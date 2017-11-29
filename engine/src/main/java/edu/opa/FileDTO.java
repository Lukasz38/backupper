package edu.opa;

public class FileDTO {

	private String localPath;
	private String backupDate;
	private String remotePath;
	
	public FileDTO(String localPath, String backupDate, 
			String remotePath) {
		this.localPath = localPath;
		this.backupDate = backupDate;
		this.remotePath = remotePath;
	}

	public String getLocalPath()
	{
		return localPath;
	}
	public String getBackupDate()
	{
		return backupDate;
	}
	public String getRemotePath()
	{
		return remotePath;
	}
}
