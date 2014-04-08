package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
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

	@Before
	public void setup(){
		Thread serverThread = new Thread(new Runnable(){

			@Override
			public void run() {
				Server server = new Server();
				try {
					server.run(PORT);
				} catch (IOException e) {}
				
			}
			
		});
		serverThread.start();
	}
	
	
	@Test 
	public void testClientInputStreamOIO() throws UnknownHostException, IOException{
		ClientInputStreamOIO client = new ClientInputStreamOIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientBufferedInputStreamOIO() throws UnknownHostException, IOException{
		ClientBufferedInputStreamOIO client = new ClientBufferedInputStreamOIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientDataBufferedInputStreamOIO() throws UnknownHostException, IOException{
		ClientDataBufferedInputStreamOIO client = new ClientDataBufferedInputStreamOIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientDataInputStreamOIO() throws UnknownHostException, IOException{
		ClientDataInputStreamOIO client = new ClientDataInputStreamOIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientNettyOIO() throws UnknownHostException, IOException{
		ClientNettyOIO client = new ClientNettyOIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientNettyNIO() throws UnknownHostException, IOException{
		ClientNettyNIO client = new ClientNettyNIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientAsyncChannelNIO2() throws UnknownHostException, IOException, InterruptedException, ExecutionException{
		ClientAsyncChannelNIO2 client = new ClientAsyncChannelNIO2();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientBlockedChannelNIO() throws UnknownHostException, IOException, InterruptedException, ExecutionException{
		ClientBlockedChannelNIO client = new ClientBlockedChannelNIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientBusySpinChannelNIO() throws UnknownHostException, IOException, InterruptedException, ExecutionException{
		ClientBusySpinChannelNIO client = new ClientBusySpinChannelNIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	@Test 
	public void testClientNonBlockedSelectorChannelNIO() throws UnknownHostException, IOException, InterruptedException, ExecutionException{
		ClientNonBlockedSelectorChannelNIO client = new ClientNonBlockedSelectorChannelNIO();
		client.run (PORT, bufferSize, TCP_NO_DELAY);
		
		Map<String,String> rslt = client.getResults();
		out.println(rslt.toString());
	}
	
	public static void main(String[] args) {
	    Result result = JUnitCore.runClasses(Main.class);
	    for (Failure failure : result.getFailures()) {
	      out.println(failure.toString());
	    }
	}
}
