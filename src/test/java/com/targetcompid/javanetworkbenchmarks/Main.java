package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static java.lang.System.out;


public class Main {
	
	final static int PORT = 1234;
	final static int bufferSize = 100;
	final static boolean TCP_NO_DELAY = true;

	@BeforeClass public static void setup(){
		Thread serverThread = new Thread(new Runnable(){

			@Override
			public void run() {
				out.println("Starting server");
				Server server = new Server();
				try {
					server.run(PORT);
				} catch (IOException e) {}
				
			}
			
		});
		serverThread.start();
		
	}
	
	@Test public void testClientInputStreamOIO() throws UnknownHostException, IOException{
		ClientInputStreamOIO inputStream = new ClientInputStreamOIO();
		inputStream.run(PORT, bufferSize, TCP_NO_DELAY);
		
		out.println("Printing results");
		Map<String,String> rslt = inputStream.getResults();
		out.println(rslt.toString());
	}
	
	public static void main(String[] args) {
	    Result result = JUnitCore.runClasses(Main.class);
	    for (Failure failure : result.getFailures()) {
	      out.println(failure.toString());
	    }
	}
}
