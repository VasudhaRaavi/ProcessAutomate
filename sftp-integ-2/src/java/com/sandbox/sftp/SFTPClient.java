package com.sandbox.sftp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.Hashtable;

import java.lang.String;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Test
	  public void testPutAndGetFile() throws JSchException, SftpException, IOException
	  {
	    JSch jsch = new JSch();

	    Hashtable<String, String> config = new Hashtable<String, String>();
	    config.put("StrictHostKeyChecking", "no");
	    JSch.setConfig(config);

	    Session session = jsch.getSession( "remote-username", "localhost", PORT);
	    session.setPassword("remote-password");

	    session.connect();

	    Channel channel = session.openChannel( "sftp" );
	    channel.connect();

	    ChannelSftp sftpChannel = (ChannelSftp) channel;

	    final String testFileContents = "some file contents";

	    String uploadedFileName = "uploadFile";
	    sftpChannel.put(new ByteArrayInputStream(testFileContents.getBytes()), uploadedFileName);

	    String downloadedFileName = "downLoadFile";
	    sftpChannel.get(uploadedFileName, downloadedFileName);

	    File downloadedFile = new File(downloadedFileName);
	    Assert.assertTrue(downloadedFile.exists());

	    String fileData = getFileContents(downloadedFile);

	    Assert.assertEquals(testFileContents, fileData);

	    if (sftpChannel.isConnected()) {
	      sftpChannel.exit();
	      System.out.println("Disconnected channel");
	    }

	    if (session.isConnected()) {
	      session.disconnect();  
	      System.out.println("Disconnected session");
	    }

	  }

	  private String getFileContents(File downloadedFile)
	    throws FileNotFoundException, IOException
	  {
	    StringBuffer fileData = new StringBuffer();
	    BufferedReader reader = new BufferedReader(new FileReader(downloadedFile));

	    try {
	      char[] buf = new char[1024];
	      for(int numRead = 0; (numRead = reader.read(buf)) != -1; buf = new char[1024]) {
	        fileData.append(String.valueOf(buf, 0, numRead));
	      }
	    } finally {    
	      reader.close();
	    }

	    return fileData.toString();
	  }

}
