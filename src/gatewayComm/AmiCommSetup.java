package gatewayComm;

import java.io.*;
import java.net.*;

public class AmiCommSetup {

	static Socket mySocket = null;
	static PrintWriter out = null;
	static BufferedReader in = null;

	static String line;
	
	//setup constructor
	public AmiCommSetup(){
		System.out.println("AMI program running");
		
		GatewayConnect();
		setupIOStreams();
	}
		
	public static void GatewayConnect(){
		try{
			System.out.println("Attempting to connect to Gateway socket...");
			mySocket = new Socket("192.168.1.2", 1234);
		} catch(IOException e){
			System.err.println("Connection to port 1234 failed");
			System.exit(-1);
		}
	}
	
	public static void setupIOStreams(){
		try{
			System.out.println("Creating output stream");
			out = new PrintWriter(mySocket.getOutputStream(), true);
		} catch(IOException e){
			System.err.println("IO error in setup");
			System.exit(1);
		}
	}
	
	public static String convertToXML(String pow, String t){
		String temp;
		temp = "<time>" + t + "</time>" + " " 
		+ "<value>" + pow + "</value>";
		return temp;
	}
	
	public static void postToGateway(String l){
		out.println(l);
	}
	
	public static void closeAll(){
		try{
			in.close();
			out.close();
			mySocket.close();
		} catch(IOException e){
			System.err.println("Error during cleanup");
			System.exit(-1);
		}
	}

}
