package FinalPKG;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

import Example5.PackageThread;

/**
 * Client method
 * 
 * @author Nate, Jose, Tyler
 *
 *         Source:
 *         https://github.com/Michaelcj10/Java-UDP-File-Transfer-Made-Reliable/tree/master/src
 */
public class Client {

	private static String fileName;
	private static int sendRate;
	private static String destFileName;
	private static String hostName;
	private static int port;
    private InetAddress ip;
    private DatagramPacket packet;
    private DatagramSocket socket;


	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {

		sendRate = 12;
		port = 8080;
		hostName = "localhost";
		destFileName = "outputFile.txt";

		setLossRate(sendRate);
		setHostname(hostName);
		setPort(port);
		fileName = args[0];
		setFileName(fileName);
		setDestFile(destFileName);
		setUp();
	}

	/**
	 * setUp method opens the socket and send a file
	 * 
	 * @throws IOException
	 */
	public static void setUp() throws IOException {

		System.out.println("Sending the file");
		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName(getHostname());

		String saveFileAs = getDestFileName();
		byte[] saveFileAsData = saveFileAs.getBytes();

		DatagramPacket fileStatPacket = new DatagramPacket(saveFileAsData, saveFileAsData.length, address, getPort());
		socket.send(fileStatPacket);

		File file = new File(getFileName());
		// Create a byte array to store file
		InputStream inFromFile = new FileInputStream(file);
		byte[] fileByteArray = new byte[(int) file.length()];

		closeSocket(socket);
	}

	/**
	 * Closing the socket that was used to send the file
	 * 
	 * @param socket
	 */
	private static void closeSocket(DatagramSocket socket) {
		socket.close();
	}

	private static void setLossRate(int passed_loss_rate) {
		sendRate = passed_loss_rate;
	}

	/**
	 * getPort method
	 * 
	 * @return
	 */
	private static int getPort() {
		return port;
	}

	/**
	 * setPort method
	 * 
	 * @param passed_port
	 */
	private static void setPort(int passed_port) {
		port = passed_port;
	}

	/**
	 * getFileName method
	 * 
	 * @return
	 */
	private static String getFileName() {
		return fileName;
	}

	/**
	 * setFileName method
	 * 
	 * @param passed_file_name
	 */
	private static void setFileName(String passed_file_name) {
		fileName = passed_file_name;
	}

	/**
	 * Setting destination file
	 * 
	 * @param passed_dest_file
	 */
	private static void setDestFile(String passed_dest_file) {
		destFileName = passed_dest_file;
	}

	/**
	 * Get method for the destination file
	 * 
	 * @return
	 */
	private static String getDestFileName() {
		return destFileName;
	}

	/**
	 * Getting host name
	 * 
	 * @return
	 */
	private static String getHostname() {
		return hostName;
	}

	/**
	 * Sets host name
	 * 
	 * @param passed_host_name
	 */
	private static void setHostname(String passed_host_name) {
		hostName = passed_host_name;
	}
	
    private void sendPackets(byte[] bytes, int i){

        if(bytes.length - ( i  * 1000 ) > 1000) {
            byte[] arr = Arrays.copyOfRange(bytes, i * 1000, (i + 1) * 1000);
            packet = new DatagramPacket(arr, 1000, ip, port);
            try {
                socket.send(packet);
            } catch (Exception e) {
                System.out.println("Something went wrong");
            }
            PackageThread pt = new PackageThread(socket, this) {
                @Override
                public void doWork() throws Exception {
                    if (new String(receivedBuffer).trim().equals("I received")) {
                        sendPackets(bytes, i + 1);
                    }else{
                        System.out.println("error occurred");
                    }
                }
            };
            pt.startListening();
        } else {

            byte[] arr = Arrays.copyOfRange(bytes, bytes.length - ( bytes.length % 1000), bytes.length);
            packet = new DatagramPacket(arr, bytes.length % 1000, ip, port);
            try {
                socket.send(packet);
            } catch (Exception e) {
                System.out.println("Something went wrong");
            }
            PackageThread pt = new PackageThread(socket, this) {
                @Override
                public void doWork() throws Exception {
                    if (new String(receivedBuffer).trim().equals("I received")) {
                        label.setText("File has been sent successfully");
                    }else{
                        System.out.println("error occurred");
                    }
                }
            };
            pt.startListening();
        }
    }
}