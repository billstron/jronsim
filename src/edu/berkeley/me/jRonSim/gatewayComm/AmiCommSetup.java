/*
 * Copyright (c) 2009, Regents of the University of California
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the University of California, Berkeley
 * nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.berkeley.me.jRonSim.gatewayComm;

import java.io.*;
import java.net.*;


/**
 * 
 * @author
 *
 */
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
