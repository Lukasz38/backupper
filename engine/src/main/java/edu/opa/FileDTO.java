package edu.opa;

import java.time.LocalDateTime;

public class FileDTO {

	private String localPath;
	private LocalDateTime backupDate;
	private String remotePath;
	
	public FileDTO(String localPath, LocalDateTime backupDate, 
			String remotePath) {
		this.localPath = localPath;
		this.backupDate = backupDate;
		this.remotePath = remotePath;
	}

	public String getLocalPath()
	{
		return localPath;
	}
	public LocalDateTime getBackupDate()
	{
		return backupDate;
	}
	public String getRemotePath()
	{
		return remotePath;
	}
}
