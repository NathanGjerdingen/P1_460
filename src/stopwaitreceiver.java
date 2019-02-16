//
//	CREDIT TO: https://gangof1994.wordpress.com/2015/08/11/stop-and-wait-protocol-using-socket-program/
//	
//	EDITED BY: Nathan Gjerdingen
//

//	RECIEVER

import java.io.*;
import java.net.*;

class stopwaitreceiver {

	public static void main(String args[])throws Exception {
		stopwaitreceiver swr = new stopwaitreceiver();
		swr.run();
	}
	
	public void run() throws Exception {
		
		String temp = "any message";
		String str="exit";
		
		ServerSocket myss=new ServerSocket(9999);
		Socket ss_accept=myss.accept();

		BufferedReader ss_bf=new BufferedReader(new InputStreamReader(ss_accept.getInputStream()));

		PrintStream myps=new PrintStream(ss_accept.getOutputStream());

		while(temp.compareTo(str) != 0) {
			Thread.sleep(1000);
			temp = ss_bf.readLine();

			if (temp.compareTo(str) == 0) { 
				break;
			}

			System.out.println("Frame " + temp + " was recieved");
			Thread.sleep(500);

			myps.println("Received");
		}

		System.out.println("ALL FRAMES  WERE RECEIVED SUCCESSFULLY");
		myss.close();
	}
}