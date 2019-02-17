import com.sun.corba.se.pept.transport.Connection;

import javafx.scene.shape.Line;
import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments;

public class Instructions {
	
//		Yo dudes! This is Nathan. I got both these examples online here ready for 
//		the program in class. This *should* have all the functionality we need 
//		for the project. I think Example 1 is the closest, but example 2 is a little
//		more verbose. 
//		
//		So basically, we're gonna use Example 1 as the template, then find stuff in 
//		Example 2 and use it. Things that need to happen are...
//		
//		1.	Make sure you can fire these up via command line. That's how our 
//			instructor will test these things. 
//			
//		2.	Make sure all the program meets class requirements. I think there's 
//			juuuuuust one or two differences from what is accomplished in 
//			Example 1 and our programming assignment. Not only that, I believe 
//			those differences can be found in Example 2. 
//			
//		Please let me know if you guys need anything! I'll be out all night (Sat
//		Feb 16th), and will be back to work on this intermittently tomorrow.
//	
//		If you need to get ahold of me, you both have my number.
//		
//		Cheers!

	public void howToStartExample1() {
		
//		To start Example 1, you need to run the stopwaitreciever first. This 
//		sets up the sever for the stopwaitsender to send files to. I can only
//		run this successfully in Eclipse.
//
//		Once starting the stopwaitreciever, you can run the stopwaitsender and send
//		packets of info the the reciever. I'll ask you a number after it is started 
//		and will notify you when things are being sent/recieved.
//		
//		NOTES:
//		After starting a stopwaitreciever on a port (currently localhost:7777), when 
//		finished, you must either kill that port task or choose a new port next time
//		you run the reciever. If you choose a new port, be sure to update the sender
//		with that port as well.
		
	}
	
	public void howToStartExample2() {
		
//		Example 2 is more of what we're looking for in terms of functionality. 
//		Similarly to the stopwaitreceiver in Example 1, you need to start the 
//		Server first in Example 2. 
//		
//		Unlike Example 1's stopwaitreciever, you need to pass the port number 
//		to the program when starting it. This can be done by adding a port 
//		number under Eclipse>Run>Run Configurations>Arguments. Also, this
//		Example can be run from the command line. 
//		
//		After running the Server, then you can go to Client and send a file
//		over that connection. I included a few test files to do just that. 
//		The command I'm using to do so is the following...
//		
//		java Client <PACKET_RATE_HERE> localhost <PORT_NUM_HERE> test.bin serverText.bin
//		
//		NOTES:
//		From the cmd above, all these can be parameters given to the program in 
//		Eclipse>Run>Run Configurations>Arguments if needed to be ran from Eclipse
		
	}
}
