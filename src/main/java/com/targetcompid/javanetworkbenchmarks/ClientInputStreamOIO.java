package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class ClientInputStreamOIO extends AbstractInputStreamClientOIO{

	public ClientInputStreamOIO() {
		super("OIO InputStream");
	}

	Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {
		
		Socket client = new Socket(InetAddress.getLocalHost(), PORT);
		client.setTcpNoDelay(TCP_NO_DELAY);
		InputStream input = client.getInputStream();
		
		try{
			startTimer();
			process(input);
			endTimer();
		}
		finally{
			client.close();
		}
		Map<String,String> res = getResults();
		res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
		res.put("BUFFER_SIZE", df.format(0));
		return res;
	}
}
