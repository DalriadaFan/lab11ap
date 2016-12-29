/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.*;

/**
 *
 * @author deser_000
 */
public class FileClient {

    private DatagramSocket socket = null;
    private FileHandler event = null;
    private String sourceFilePath;
    private String destinationPath;
    private String hostName;

    byte[] dataRead = new byte[1024];
    public FileClient() {

        this.hostName = "localHost";
        this.sourceFilePath = "C:/Users/Ismail/Documents/workspace/lab11AP/block.zip";
        this.destinationPath = "C:/Users/Ismail/Documents/ApServer/Lab11Server/";
    }

    public void createConnection() {
        try {
            //prepare socket and file
            socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(hostName);
            byte[] incomingData = new byte[1024];

	            event = getFileHandler();
	            
	            //send file
	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            ObjectOutputStream os = new ObjectOutputStream(outputStream);
	            os.writeObject(event);
	            byte[] data = outputStream.toByteArray();
	            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 45000);
	            socket.send(sendPacket);
	            System.out.println("File sent from client");
      
	            //confirmation of reciept
	            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
	            socket.receive(incomingPacket);
	            String response = new String(incomingPacket.getData());
	            System.out.println("Response from server:" + response);
	            

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // changes made
    public FileHandler getFileHandler() {
        FileHandler  fileHandle = new FileHandler();
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
         fileHandle.setDestinationDirectory(destinationPath);
         fileHandle.setFilename(fileName);
         fileHandle.setSourceDirectory(sourceFilePath);
        File file = new File(sourceFilePath);
        FileInputStream fis = null;
        if (file.isFile()) {
            try {

            	fis = new FileInputStream(file);
            	System.out.println("Total file size to read (in bytes) : "
    					+ fis.available());

            	fis.read(dataRead, 0, fis.available());
            	

            	fileHandle.setFileData(dataRead);
            	fileHandle.setStatus("Okay");

            } catch (Exception e) {
                e.printStackTrace();
                 fileHandle.setStatus("Error");
            } finally {
            	try { 
            		if (fis != null)
            			fis.close();
            	} catch (IOException ex) {
            		ex.printStackTrace();
            	}
            }
        } else {
            System.out.println("path specified is not pointing to a file");
             fileHandle.setStatus("Error");
        }
        return  fileHandle;
    }

    public static void main(String[] args) {
        FileClient client = new FileClient();
        client.createConnection();
    }

}
