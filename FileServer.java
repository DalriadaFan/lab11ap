
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class FileServer {

    private DatagramSocket socket = null;
    private FileHandler fileHandle = null;

    public FileServer() {

    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(45000);
            byte[] incomingData = new byte[1024 * 1000 * 50];
            while (true) {
                //receive file
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                byte[] data = incomingPacket.getData();
                
                //write to file
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                fileHandle = (FileHandler) is.readObject();
                if (fileHandle.getStatus().equalsIgnoreCase("Error")) {
                    System.out.println("Some issue happened while packing the data @ client side");
                    System.exit(0);
                }
                createAndWriteFile(); 
                
                //send confirmation
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                String reply = "Got the file";
                byte[] replyBytea = reply.getBytes();
                DatagramPacket replyPacket
                        = new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
                socket.send(replyPacket);
                

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createAndWriteFile() throws IOException {
        String outputFile = fileHandle.getDestinationDirectory() + fileHandle.getFilename();
        if (!new File(fileHandle.getDestinationDirectory()).exists()) {
            new File(fileHandle.getDestinationDirectory()).mkdirs();
            System.out.println("File created at the destination");
        }
        File dstFile = new File(outputFile);
        FileOutputStream fileOutputStream = new FileOutputStream(dstFile);

        	byte[] dataRead = fileHandle.getFileData();
            
            // write to file
            fileOutputStream.write(dataRead);
            fileOutputStream.flush();
            fileOutputStream.close();

            System.out.println("Done");

    }

    public static void main(String[] args) {
        FileServer server = new FileServer();
        server.createAndListenSocket();
    }
}
