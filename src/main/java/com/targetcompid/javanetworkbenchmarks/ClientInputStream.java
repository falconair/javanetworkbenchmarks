package com.targetcompid.javanetworkbenchmarks;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class ClientInputStream {
	Map<String,String> run(int PORT, int bufferSize, boolean TCP_NO_DELAY) throws IOException, UnknownHostException {
		Parser p = new Parser("OIO InputStream");
		
		Socket client = new Socket(InetAddress.getLocalHost(), PORT);
		client.setTcpNoDelay(TCP_NO_DELAY);
		InputStream in = client.getInputStream();
		
		try{
			p.startTimer();
			p.process(in);
			p.endTimer();
		}
		finally{
			client.close();
		}
		Map<String,String> res = p.getResults();
		res.put("TCP_NO_DELAY", Boolean.toString(TCP_NO_DELAY));
		res.put("BUFFER_SIZE", Parser.df.format(0));
		return res;
	}
}
